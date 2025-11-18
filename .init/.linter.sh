#!/bin/bash
cd /home/kavia/workspace/code-generation/noteease-42570-42579/android_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

