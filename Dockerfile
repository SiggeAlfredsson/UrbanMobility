# Use an official Ubuntu runtime as a parent image
FROM ubuntu:22:04

# Set environment variables for non-interactive installation of packages
#ENV DEBIAN_FRONTEND=noninteractive

# Update and install necessary packages
RUN apt-get update -y \
    && apt-get install -y openjdk-11-jdk maven

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files (pom.xml) and source code into the container
COPY pom.xml .
COPY src ./src

# Build the Maven project inside the container
RUN mvn clean install

# Expose any necessary ports (if your application listens on a port)
EXPOSE 8081

# Define the command to run your application (replace with your actual command)
CMD ["java", "-jar", "demo-0.0.1.jar"]