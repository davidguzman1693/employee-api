# employee-api

## Description
This is a technical challenge for a CRUD operation for Employee Information. Technical challenges to be tackled:
- Message Broker
- Database
- Conterization: Docker
- Swagger
- Authentication

How to run it?
You should first build a docker image from the employee-service.
For that just run:
- mvn clean install.

Once you have the image, you can just run the following command on docker-compose:
- docker-compose -f common.yml -f database.yml -f kafka_cluster.yml -f service.yml up

There's an issue with the schema-registry as sometimes it doesn't get assigned a node. 
By now I have a policy to restart the service automatically, but in case it doesn't work, just run it again and it will get a node assigned.

Once everything is running, you can start making requests to the endpoint: localhost:8080.

In case you wanna debug/check more in depth the code, you can just run:
- docker-compose -f common.yml -f database.yml -f kafka_cluster.yml up
- Then just run the employee-service in your IDE.