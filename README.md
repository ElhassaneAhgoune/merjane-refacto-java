# Project Name

## Description

This project is built with Java 17 and uses Maven as the build tool. It leverages Docker Compose to manage the environment (e.g., databases, external services) required for running the tests.

## Prerequisites

- Docker and Docker Compose
  - Install Docker: [Get Docker](https://docs.docker.com/get-docker/)
  - Install Docker Compose: [Get Docker Compose](https://docs.docker.com/compose/install/)
- Java 17 (or a compatible version)
  - Install Java 17: [Install Java 17](https://adoptium.net/temurin/releases/?version=17)

## Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/ElhassaneAhgoune/merjane-refacto-java.git
   cd merjane-refacto-java 
2. run :
   mvn clean install 
   
3. run 
  docker-compose up 

4. run  
   mvn test 

