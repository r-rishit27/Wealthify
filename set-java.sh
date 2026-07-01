#!/bin/bash
# Source this before running the backend if your terminal doesn't find Java:
#   source set-java.sh
# Or: . set-java.sh

if command -v java &>/dev/null; then
  echo "Java already on PATH: $(java -version 2>&1 | head -1)"
  return 0 2>/dev/null || exit 0
fi

if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  export PATH="$JAVA_HOME/bin:$PATH"
  echo "Using JAVA_HOME: $JAVA_HOME"
  return 0 2>/dev/null || exit 0
fi

# Homebrew OpenJDK (Apple Silicon and Intel)
for jdk in /opt/homebrew/opt/openjdk@17 /opt/homebrew/opt/openjdk@21 /opt/homebrew/opt/openjdk /usr/local/opt/openjdk@17; do
  if [ -x "$jdk/bin/java" ]; then
    export JAVA_HOME="$jdk"
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "Using Java: $JAVA_HOME"
    return 0 2>/dev/null || exit 0
  fi
done

# macOS system Java
if [ -x "/usr/libexec/java_home" ]; then
  JAVA_HOME=$(/usr/libexec/java_home 2>/dev/null)
  if [ -n "$JAVA_HOME" ]; then
    export JAVA_HOME
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "Using Java: $JAVA_HOME"
    return 0 2>/dev/null || exit 0
  fi
fi

echo "Java not found. Install JDK 17+ or set JAVA_HOME. See run.md."
return 1 2>/dev/null || exit 1
