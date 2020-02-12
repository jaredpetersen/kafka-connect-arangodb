# Demo: Kafka Connect ArangoDB Source
## Configure Connector
Send a request to the Kafka Connect REST API to configure it to use Kafka Connect ArangoDB:
```bash
curl --request POST \
    --url "$(minikube -n kca-demo service kafka-connect --url)/connectors" \
    --header 'content-type: application/json' \
    --data '{
        "name": "demo-arangodb-connector",
        "config": {
            "connector.class": "io.github.jaredpetersen.kafkaconnectarangodb.sink.ArangoDbSinkConnector",
            "tasks.max": "1",
            "topics": "stream.airports,stream.flights",
            "arangodb.host": "arangodb-ea",
            "arangodb.port": 8529,
            "arangodb.user": "root",
            "arangodb.password": "",
            "arangodb.database.name": "demo"
        }
    }'
```

## Write Records
TODO

## Validate
TODO
