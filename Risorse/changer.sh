#!/bin/bash

# Script to show the change between codes

jar="../target/CompilerMZ-0.5.0-Alpha-jar-with-dependencies.jar"
sleepTime=2
f=fausto.mz

# Find all .json files in the resources directory, remove the .json extension
dialects=$(find ../src/main/resources -name "*.json" -exec basename -s .json {} \;)

# Convert the dialects to an array
dialectsArray=($dialects)

# Get the length of the array
dialectsLength=${#dialectsArray[@]}

while true; do
    # Loop over the array of dialects
    for (( i=0; i<dialectsLength; i++ )); do
        # Get the current and next dialect
        currentDialect=${dialectsArray[$i]}
        nextDialect=${dialectsArray[$(( (i+1) % dialectsLength ))]}

        # Translate the code
        java -jar "$jar" -i "$f" -o "$f" -t "$currentDialect,$nextDialect" 2&> /dev/null
        java -jar "$jar" -i "$f" -d "$nextDialect"
        if [ $? -ne 0 ]; then
            echo "One language does not support the other language's code. Exiting..."
            exit 1
        fi
        # Sleep
        sleep "$sleepTime"
    done
done