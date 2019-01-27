# ArangoDB Docker Image
Docker image for ArangoDB used in the Kafka cluster example.

Creates a new ArangoDB server and initializes it with a database named `development`, a document collection named `airports`, and an edge collection named `flights`. User credentials are username `root` and password `password`.

For local development usage only. Please read [ArangoDB's documenation](https://hub.docker.com/_/arangodb) for more information on how to run ArangoDB in Docker.
