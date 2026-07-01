import os
import json
from typing import List
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, ValidationError
import google.generativeai as genai
from dotenv import load_dotenv

load_dotenv()

GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
if GEMINI_API_KEY:
    genai.configure(api_key=GEMINI_API_KEY)

app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class Asset(BaseModel):
    asset: str
    percentage: float
    investment_value: float
    quantity: float

class Portfolio(BaseModel):
    total_investment: float
    portfolio: List[Asset]

SYSTEM_PROMPT = """
You are a highly skilled financial analyst and portfolio strategist. 
Analyze the user's current portfolio data provided in JSON format.
Your goal is to provide a concise, personalized recommendation summary for growth and optimization.

Focus on:
1. Asset Allocation: Is the portfolio well-diversified?
2. Risk Management: Are there any over-concentrated positions?
3. Optimization: Suggest specific adjustments to improve potential returns or reduce risk.
4. Growth Strategy: Identify if the portfolio aligns with a growth-oriented mindset.

Keep the response precise, actionable, and under 200 words. Use a professional yet accessible tone.
"""

@app.get("/recommend")
async def get_recommendation(portfolio_json: str):
    if not GEMINI_API_KEY:
        raise HTTPException(status_code=500, detail="Gemini API Key not configured.")
    
    try:
       
        import urllib.parse
        decoded_json = urllib.parse.unquote(portfolio_json)
        data = json.loads(decoded_json)
        
        
        if 'total_investment' in data:
            data['total_investment'] = float(data['total_investment'])
        if 'portfolio' in data:
            for item in data['portfolio']:
                item['percentage'] = float(item.get('percentage', 0))
                item['investment_value'] = float(item.get('investment_value', 0))
                item['quantity'] = float(item.get('quantity', 0))
        
        portfolio_data = Portfolio(**data)
        
      
        # Model Used Try in order: gemini-2.5-flash -> gemini-2.0-flash -> gemini-2.0-flash-lite
        prompt = f"User Portfolio Data:\n{portfolio_data.model_dump_json(indent=2)}\n\nPlease provide your recommendation summary based on the system prompt."
        model_names = ["gemini-2.5-flash", "gemini-2.0-flash", "gemini-2.0-flash-lite"]
        summary_text = None
        model_name = None
        last_error = None
        for name in model_names:
            try:
                model = genai.GenerativeModel(name)
                response = model.generate_content([SYSTEM_PROMPT, prompt])
                summary_text = response.text.strip() if response.text else "No summary generated."
                model_name = name
                break
            except Exception as e:
                last_error = e
                continue
        if summary_text is None or model_name is None:
            raise HTTPException(
                status_code=502,
                detail=f"Gemini API error: {str(last_error)}"
            )
        
        return {
            "summary": summary_text,
            "status": "success",
            "model_used": model_name
        }
    except json.JSONDecodeError as e:
        raise HTTPException(status_code=400, detail=f"Invalid JSON: {str(e)}")
    except ValidationError as e:
        raise HTTPException(status_code=400, detail=f"Invalid portfolio structure: {str(e)}")
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error processing request: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8002)
