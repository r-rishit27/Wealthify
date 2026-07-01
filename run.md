# How to Run the Portfolio Manager App

This guide explains how to run the full application and how to fix common issues.

---

## Prerequisites

| Requirement | Purpose |
|-------------|---------|
| **Java 17+** | Spring Boot backend |
| **Node.js 18+** and **npm** | React frontend (Vite) |
| **MySQL 8.0+** (optional) | Database; can use H2 with embedded config |
| **Maven 3.6+** (optional) | Backend build; project includes Maven wrapper (`mvnw`) |
| **Python 3.9+** (optional) | Stock Prediction, Quantum, AI Summarizer services |

---

## Quick Run (Backend + Frontend Only)

Minimum needed to use the app: **backend** and **frontend**.

### 1. Backend (Spring Boot) — Port 8080

```bash
# From project root
cd backend

# If Maven wrapper fails with "No such file or directory" for .mvn/wrapper:
# Copy the wrapper from project root into backend (one-time fix)
cp -r ../.mvn .   # from repo root: .mvn exists at root

# Ensure Java 17+ is available
java -version

# Run the backend
./mvnw spring-boot:run
# Or, if you have Maven installed: mvn spring-boot:run
```

Backend will be at **http://localhost:8080**  
Swagger UI: **http://localhost:8080/swagger-ui.html**

### 2. Frontend (React + Vite) — Port 5173

Open a **second terminal**:

```bash
cd frontend

# First time only: install dependencies
npm install

# Start development server
npm run dev
```

Frontend will be at **http://localhost:5173**

---

## Full Run (All Services)

To run backend, frontend, and optional Python services (Stock Prediction, Quantum, AI Summarizer):

```bash
# From project root
chmod +x run-all.sh
./run-all.sh
```

This starts:

| Service            | URL                      |
|--------------------|--------------------------|
| Backend            | http://localhost:8080    |
| Frontend           | http://localhost:5173    |
| Stock Prediction   | http://localhost:8000    |
| Quantum            | http://localhost:8001    |
| AI Summarizer      | http://localhost:8002    |

Press **Ctrl+C** to stop all.

---

## Manual Run (Separate Terminals)

Use one terminal per service if you prefer not to use `run-all.sh`:

| Terminal | Command | Port |
|----------|---------|------|
| 1 – Backend | `cd backend && ./mvnw spring-boot:run` | 8080 |
| 2 – Frontend | `cd frontend && npm run dev` | 5173 |
| 3 – Stock Prediction | `cd stock_prediction && python main.py` | 8000 |
| 4 – Quantum | `cd Quantum && uvicorn main:app --reload --port 8001` | 8001 |
| 5 – AI Summarizer | `cd AI_SUMRIZER && python app.py` | 8002 |

---
## export JAVA_HOME=/opt/homebrew/opt/openjdk@17
## export PATH="$JAVA_HOME/bin:$PATH"
## Common Issues and Fixes

### 1. "Unable to locate a Java Runtime" / `java: command not found`

**Cause:** Java is not installed or not on your `PATH`.

**Why does it work in my terminal but not when I run the app / run-all.sh / Cursor?**  
New shells (e.g. from Cursor, scripts, or "Run" buttons) often **do not** load your full profile (`.zshrc`, `.bash_profile`). So `JAVA_HOME` and `PATH` are empty there even if you set them in your normal terminal. The app runs in one of these new shells, so it doesn’t see Java unless we fix it.

**Fix (pick one):**

- **A) Make Java available in every new shell (recommended)**  
  Add these lines to your shell profile so **every** new terminal (including Cursor’s) gets Java:

  **macOS (Zsh – default):** edit `~/.zshrc`  
  **macOS (Bash):** edit `~/.bash_profile` or `~/.bashrc`  
  **Linux:** edit `~/.bashrc` or `~/.profile`

  ```bash
  # Java 17+ (adjust path if you use a different version)
  export JAVA_HOME=/opt/homebrew/opt/openjdk@17
  export PATH="$JAVA_HOME/bin:$PATH"
  ```

  If you use Homebrew but aren’t sure of the path, run once in your terminal:
  ```bash
  brew --prefix openjdk@17
  ```
  Then use that path in `JAVA_HOME` (no angle brackets). Save the file, then **open a new terminal** (or restart Cursor) so the change is picked up.

- **B) Use the project’s Java helper in this terminal only**  
  From the project root:
  ```bash
  source set-java.sh
  cd backend && ./mvnw spring-boot:run
  ```
  This only affects the current shell.

- **C) Use `run-all.sh`**  
  `run-all.sh` now tries to find Java automatically (Homebrew paths and `java_home`). If Java is installed in a standard place, running `./run-all.sh` may work without setting `JAVA_HOME` yourself.

- **D) One-off in current terminal (no profile edit)**  
  **macOS (Homebrew):**
  ```bash
  export JAVA_HOME=/opt/homebrew/opt/openjdk@17
  export PATH="$JAVA_HOME/bin:$PATH"
  ```
  Use the path from `brew --prefix openjdk@17` if different. No angle brackets.

  **Linux (apt):**
  ```bash
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
  export PATH="$JAVA_HOME/bin:$PATH"
  ```

Verify in the **same** terminal (or Cursor’s terminal) where you run the app: `java -version` and `echo $JAVA_HOME`.

---

### 2. Maven wrapper: "No such file or directory" for `.mvn/wrapper/maven-wrapper.properties`

**Cause:** The Maven wrapper (`.mvn` folder) lives at the **repository root**, but you are running `./mvnw` from `backend/`, which looks for `backend/.mvn`.

**Fix (one-time):** Copy the wrapper into `backend`:

```bash
# From project root
cp -r .mvn backend/
```

Then from `backend/` run:

```bash
./mvnw spring-boot:run
```

Alternatively, from project root using system Maven:

```bash
mvn -f backend/pom.xml spring-boot:run
```

---

### 3. Port already in use (8080, 5173, etc.)

**Cause:** Another process is using the port.

**Fix:**

- Find and kill the process (macOS/Linux):
  ```bash
  lsof -ti:8080   # replace 8080 with the port
  kill -9 $(lsof -ti:8080)
  ```

- Or change the port in config:
  - Backend: `backend/src/main/resources/application.properties` → `server.port=8081` (and update frontend API base URL if needed).
  - Frontend: in `frontend/vite.config.ts` (or env) set a different port for the dev server.

---

### 4. Database connection errors (MySQL)

**Cause:** MySQL not running, wrong host/port, or wrong credentials.

**Fix:**

- Start MySQL (e.g. `brew services start mysql` on macOS, or your system service manager).
- Ensure database exists:
  ```sql
  CREATE DATABASE portfolio_manager;
  ```
- Check `backend/src/main/resources/application.properties`:
  - `spring.datasource.url`, `username`, `password` match your MySQL setup.

**Alternative:** Use H2 for local dev (no MySQL needed) by switching to an H2 datasource in `application.properties` and adding the H2 dependency if not already present.

---

### 5. Frontend can't reach backend (CORS / network errors)

**Cause:** Frontend (e.g. http://localhost:5173) calls backend (http://localhost:8080); backend may block or not allow the origin.

**Fix:**

- Backend already has CORS config in `WebConfig.java` for development. Ensure the frontend `API_BASE_URL` (e.g. in `frontend/src/utils/constants.ts`) is `http://localhost:8080/api/v1`.
- If you use a different host/port for backend, update both backend CORS allowed origins and frontend `API_BASE_URL`.

---

### 6. `npm install` or `npm run dev` fails (frontend)

**Cause:** Node version, broken `node_modules`, or registry issues.

**Fix:**

- Use Node 18+:
  ```bash
  node -v
  ```
- Clean install:
  ```bash
  cd frontend
  rm -rf node_modules package-lock.json
  npm install
  npm run dev
  ```

---

### 7. Stock Prediction / AI features not working

**Cause:** Optional Python services (Stock Prediction on 8000, AI Summarizer on 8002) are not running.

**Fix:**

- Start the service(s) as in "Manual Run" above.
- Backend config: `application.properties` has `prediction.service-url=http://localhost:8000`. If the service runs on another port, update this.
- AI Summarizer needs a Gemini API key in `AI_SUMRIZER/.env` (see README).

---

### 8. No data loading / empty dashboard

**Cause:** The **backend** (Spring Boot on port 8080) is not running. The frontend loads data from `http://localhost:8080/api/v1`. If you see "Unable to locate a Java Runtime" in the terminal when running `./run-all.sh`, the backend failed to start and nothing will load.

**Fix:**

- See **Issue 1** above: ensure Java 17+ is installed and that `run-all.sh` can find it (we now detect a *working* Java and prefer Homebrew JDK over the macOS stub).
- Run the backend manually in a terminal where Java works: `export JAVA_HOME=/opt/homebrew/opt/openjdk@17` (or your JDK path), then `cd backend && ./mvnw spring-boot:run`. Once the backend is up, refresh the frontend.

---

### 9. News ticker shows "Loading news..." or no items

**Cause:** Backend news endpoint uses Yahoo Finance and Gemini; rate limits, network, or API keys can cause failures.

**Fix:**

- Backend caches news for 1 hour; first load or after cache expiry may be slow or empty if Yahoo/Gemini fail.
- Ensure `news.gemini.api-key` and `news.yahoo.endpoint` in `application.properties` are set (and valid).
- Check backend logs for errors from `NewsService`; on failure the API may return an empty list or fall back to cached data.

---

## Summary

| Goal              | Steps |
|-------------------|--------|
| Run app (minimal) | 1) Start backend: `cd backend && ./mvnw spring-boot:run` (fix .mvn/Java if needed). 2) Start frontend: `cd frontend && npm install && npm run dev`. 3) Open http://localhost:5173. |
| Run everything    | `./run-all.sh` from project root (after prerequisites and any one-time fixes above). |
| Troubleshoot      | Use the "Common Issues and Fixes" section above (Java, Maven wrapper, ports, DB, CORS, Node, optional services, news ticker). |
