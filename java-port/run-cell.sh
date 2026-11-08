#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
export JAVA_HOME="$PWD/.tools/jdk-21.0.13+9/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
if [ "$#" -ne 1 ]; then
  echo "用法: ./run-cell.sh Cell03 或 ./run-cell.sh c3.n31.Cell05" >&2
  exit 2
fi
if [[ "$1" == Cell* ]]; then
  main_class="com.aliyun.acp.c2.n21.$1"
else
  main_class="com.aliyun.acp.$1"
fi
"$PWD/.tools/apache-maven-3.10.0/bin/mvn" -q compile exec:java -Dexec.mainClass="$main_class"
