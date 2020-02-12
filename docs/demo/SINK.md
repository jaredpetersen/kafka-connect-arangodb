# Demo: Kafka Connect ArangoDB Sink
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
Create an interactive ephemeral query pod:
```bash
kubectl -n kca-demo run kafka-write-records --generator=run-pod/v1 --image confluentinc/cp-kafka:5.4.0 -it --rm --command /bin/bash
```

Write records to the `airports` topic:
```bash
kafka-console-producer --broker-list kafka-broker-0.kafka-broker:9092 --topic stream.airports --property "parse.key=true" --property "key.separator=|"
>{"id":"PDX"}|{"airport":"Portland International Airport","city":"Portland","state":"OR","country":"USA","lat":45.58872222,"long":-122.5975}
>{"id":"BOI"}|{"airport":"Boise Airport","city":"Boise","state":"ID","country":"USA","lat":43.56444444,"long":-116.2227778}
>{"id":"HNL"}|{"airport":"Daniel K. Inouye International Airport","city":"Honolulu","state":"HI","country":"USA","lat":21.31869111,"long":-157.9224072}
>{"id":"KOA"}|{"airport":"Ellison Onizuka Kona International Airport at Keāhole","city":"Kailua-Kona","state":"HI","country":"USA","lat":19.73876583,"long":-156.0456314}
```

Write records to the `flights` topic:
```bash
kafka-console-producer --broker-list kafka-broker-0.kafka-broker:9092 --topic stream.flights --property "parse.key=true" --property "key.separator=|"
>{"id":1}|{"_from":"airports/PDX","_to":"airports/BOI","depTime":"2008-01-01T21:26:00.000Z","arrTime":"2008-01-01T22:26:00.000Z","uniqueCarrier":"WN","flightNumber":2377,"tailNumber":"N663SW","distance":344}
>{"id":2}|{"_from":"airports/HNL","_to":"airports/PDX","depTime":"2008-01-13T00:16:00.000Z","arrTime":"2008-01-13T05:03:00.000Z","uniqueCarrier":"HA","flightNumber":26,"tailNumber":"N587HA","distance":2603}
>{"id":3}|{"_from":"airports/KOA","_to":"airports/HNL","depTime":"2008-01-15T16:08:00.000Z","arrTime":"2008-01-15T16:50:00.000Z","uniqueCarrier":"YV","flightNumber":1010,"tailNumber":"N693BR","distance":163}
>{"id":4}|{"_from":"airports/BOI","_to":"airports/PDX","depTime":"2008-01-16T02:03:00.000Z","arrTime":"2008-01-16T03:09:00.000Z","uniqueCarrier":"WN","flightNumber":1488,"tailNumber":"N242WN","distance":344}
```

## Validate
Open up the `demo` database again and go to the collections we created earlier. You should now see that they have the data we just wrote into Kafka.
