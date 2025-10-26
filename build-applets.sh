#!/bin/bash
# Build script for compiling Java applets

set -e  # Exit on error

echo "Building Java applets..."

# Create output directories
mkdir -p applets/lib
mkdir -p build/classes

# Compile Java source files
echo "Compiling Java source files..."
cd old_java_src/ActiveStatics/src
javac -encoding ISO-8859-1 -source 8 -target 8 -d ../../../build/classes truss/*.java

# Create JAR file
echo "Creating JAR file..."
cd ../../../build/classes
jar cvf ../../applets/lib/ActiveStatics.jar truss/*.class

cd ../..

echo "Build complete! JAR file created at applets/lib/ActiveStatics.jar"

# Verify JAR was created
if [ -f applets/lib/ActiveStatics.jar ]; then
  echo "Success: ActiveStatics.jar ($(du -h applets/lib/ActiveStatics.jar | cut -f1))"
  echo "Listing contents:"
  jar tf applets/lib/ActiveStatics.jar | head -20
else
  echo "Error: JAR file was not created"
  exit 1
fi
