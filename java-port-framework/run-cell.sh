#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
tools_dir="../java-port/.tools"
export JAVA_HOME="$PWD/$tools_dir/jdk-21.0.13+9/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
if [ "$#" -ne 1 ]; then
  echo "用法: ./run-cell.sh c2.n22.Cell19 或 ./run-cell.sh c3.n31.Cell13" >&2
  exit 2
fi
"$PWD/$tools_dir/apache-maven-3.10.0/bin/mvn" -q compile exec:java -Dexec.mainClass="com.aliyun.acp.$1"
