#!/bin/bash
# Run all services for the portfolio manager app.
# Ports: backend 8080, stock_prediction 8000, quantum 8001, ai_summarizer 8002, frontend 5173

set -e
ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

# Ensure a real Java is on PATH (macOS has a /usr/bin/java stub that fails; prefer Homebrew JDK)
java_ok() { "$1" -version &>/dev/null; }
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ] && java_ok "$JAVA_HOME/bin/java"; then
  export PATH="$JAVA_HOME/bin:$PATH"
elif [ -x "/opt/homebrew/opt/openjdk@17/bin/java" ] && java_ok "/opt/homebrew/opt/openjdk@17/bin/java"; then
  export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
  export PATH="$JAVA_HOME/bin:$PATH"
elif [ -x "/opt/homebrew/opt/openjdk@21/bin/java" ] && java_ok "/opt/homebrew/opt/openjdk@21/bin/java"; then
  export JAVA_HOME="/opt/homebrew/opt/openjdk@21"
  export PATH="$JAVA_HOME/bin:$PATH"
elif [ -x "/opt/homebrew/opt/openjdk/bin/java" ] && java_ok "/opt/homebrew/opt/openjdk/bin/java"; then
  export JAVA_HOME="/opt/homebrew/opt/openjdk"
  export PATH="$JAVA_HOME/bin:$PATH"
elif [ -x "/usr/libexec/java_home" ]; then
  JH=$(/usr/libexec/java_home 2>/dev/null) && [ -x "$JH/bin/java" ] && java_ok "$JH/bin/java" && export JAVA_HOME="$JH" && export PATH="$JAVA_HOME/bin:$PATH"
fi
if ! command -v java &>/dev/null || ! java -version &>/dev/null; then
  echo "Java not found or not working (macOS stub?). Install JDK 17+ (e.g. brew install openjdk@17) or set JAVA_HOME. See run.md."
  exit 1
fi

echo "Stopping any existing processes on 8080, 8000, 8001, 8002, 5173..."
for port in 8080 8000 8001 8002 5173; do
  pid=$(lsof -ti:$port 2>/dev/null) || true
  if [ -n "$pid" ]; then
    kill -TERM $pid 2>/dev/null || true
    sleep 2
    pid=$(lsof -ti:$port 2>/dev/null) || true
    [ -n "$pid" ] && kill -9 $pid 2>/dev/null || true
  fi
done
sleep 2

echo "Starting Stock Prediction (8000)..."
cd "$ROOT/stock_prediction" && python main.py &
PID_SP=$!
sleep 2

echo "Starting AI Summarizer (8002)..."
cd "$ROOT/AI_SUMRIZER" && python app.py &
PID_AI=$!
sleep 2

echo "Starting Quantum Optimization (8001)..."
cd "$ROOT/Quantum" && pip install -q -r requirements.txt 2>/dev/null; python -m uvicorn main:app --host 0.0.0.0 --port 8001 &
PID_Q=$!
sleep 3

echo "Starting Backend (8080)..."
cd "$ROOT/backend" && chmod +x mvnw 2>/dev/null; bash mvnw spring-boot:run -q &
PID_B=$!
echo "Backend starting in background (may take 1-2 min)..."

echo "Starting Frontend (5173)..."
cd "$ROOT/frontend" && npm run dev &
PID_F=$!

echo ""
echo "All services starting."
echo "  Stock Prediction:  http://localhost:8000"
echo "  Quantum:          http://localhost:8001"
echo "  AI Summarizer:    http://localhost:8002"
echo "  Backend:          http://localhost:8080"
echo "  Frontend:         http://localhost:5173"
echo ""
echo "Press Ctrl+C to stop all (or kill PIDs: $PID_SP $PID_AI $PID_Q $PID_B $PID_F)"
wait
