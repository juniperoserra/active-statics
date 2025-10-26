# Dockerfile for compiling Java applets
# Uses Java 8 JDK for compatibility with legacy Java 1.1.8 source code

FROM openjdk:8-jdk-alpine

# Set working directory
WORKDIR /workspace

# Install bash for build script
RUN apk add --no-cache bash

# Copy Java source files
COPY old_java_src/ActiveStatics/src /workspace/src

# Create output directory for compiled classes
RUN mkdir -p /workspace/build

# Compile all Java files
WORKDIR /workspace/src
RUN javac -source 1.8 -target 1.8 -d /workspace/build truss/*.java

# Create JAR file
WORKDIR /workspace/build
RUN jar cvf ActiveStatics.jar truss/*.class

# The JAR file will be in /workspace/build/ActiveStatics.jar
CMD ["sh", "-c", "cp /workspace/build/ActiveStatics.jar /output/"]
