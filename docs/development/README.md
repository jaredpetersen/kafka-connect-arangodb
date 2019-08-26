# Development
## Useful Commands
```bash
mvn clean install
mvn clean compile
mvn clean test
mvn clean package
```

## Local Cluster
A local cluster has been provided through the usage of [Docker](https://docs.docker.com/engine/docker-overview/) and [Kubernetes](https://docs.docker.com/compose/overview/) to exhibit how Kafka Connect ArangoDB can be integrated into a Kafka cluster. Developers may also find it useful for manual end-to-end testing.

Assumes the application has already been compiled and packaged via `mvn clean package`.

### Setup

`minikube start --vm-driver=hyperkit --cpus=2 --memory=8g`

1. Set up the namespace:
    ```bash
    kubectl apply -f namespace.yaml
    ```

2. Install the operators:

    Arangodb:
    ```bash
    kubectl apply -f https://raw.githubusercontent.com/arangodb/kube-arangodb/0.3.14/manifests/arango-crd.yaml
    kubectl apply -f https://raw.githubusercontent.com/arangodb/kube-arangodb/0.3.14/manifests/arango-deployment.yaml
    ```

    Kafka:
    ```bash
    # TODO
    ```

    Zookeeper:
    ```bash
    # TODO
    ```

3. Create the cluster:
    ```bash
    kubectl apply -f arangodeployment.yaml
    ```

To uninstall, run `kubectl delete -f <manifest>` in reverse order.

### Usage

1. Create a Kafka Broker Client.
    ```bash
    kubectl apply -f kafka/broker/kafka-broker-client-pod.yaml
    ```

2. Create Kafka Topics using the Kafka Broker Client pod.
    ```bash
    kubectl -n kafka-connect-arangodb-dev exec -it kafka-broker-client -- /bin/bash
    ```
    ```bash
    kafka-topics --zookeeper zookeeper-headless.kafka-connect-arangodb-dev:2181 --create --replication-factor 1 --partitions 1 --topic stream.airports
    kafka-topics --zookeeper zookeeper-headless.kafka-connect-arangodb-dev:2181 --create --replication-factor 1 --partitions 1 --topic stream.flights
    ```

3. Configure Kafka Connect ArangoDB using any terminal.
    ```bash
    curl --request POST \
        --url http://localhost:8083/connectors \
        --header 'content-type: application/json' \
        --data '{
            "name": "development-arangodb-connector",
            "config": {
                "connector.class": "io.github.jaredpetersen.kafkaconnectarangodb.sink.ArangoDbSinkConnector",
                "tasks.max": "1",
                "topics": "stream.airports,stream.flights",
                "arangodb.host": "arangodb",
                "arangodb.port": 8529,
                "arangodb.user": "root",
                "arangodb.password": "password",
                "arangodb.database.name": "development"
            }
        }'
    ```

4. Write records to topic.
    ```bash
    docker-compose exec broker kafka-console-producer --broker-list broker:9092 --topic stream.airports --property "parse.key=true" --property "key.separator=|"
    >{"id":"PDX"}|{"airport":"Portland International Airport","city":"Portland","state":"OR","country":"USA","lat":45.58872222,"long":-122.5975}
    >{"id":"BOI"}|{"airport":"Boise Airport","city":"Boise","state":"ID","country":"USA","lat":43.56444444,"long":-116.2227778}
    >{"id":"HNL"}|{"airport":"Daniel K. Inouye International Airport","city":"Honolulu","state":"HI","country":"USA","lat":21.31869111,"long":-157.9224072}
    >{"id":"KOA"}|{"airport":"Ellison Onizuka Kona International Airport at Keāhole","city":"Kailua-Kona","state":"HI","country":"USA","lat":19.73876583,"long":-156.0456314}
    ```
    ```bash
    docker-compose exec broker kafka-console-producer --broker-list broker:9092 --topic stream.flights --property "parse.key=true" --property "key.separator=|"
    >{"id":1}|{"_from":"airports/PDX","_to":"airports/BOI","depTime":"2008-01-01T21:26:00.000Z","arrTime":"2008-01-01T22:26:00.000Z","uniqueCarrier":"WN","flightNumber":2377,"tailNumber":"N663SW","distance":344}
    >{"id":2}|{"_from":"airports/HNL","_to":"airports/PDX","depTime":"2008-01-13T00:16:00.000Z","arrTime":"2008-01-13T05:03:00.000Z","uniqueCarrier":"HA","flightNumber":26,"tailNumber":"N587HA","distance":2603}
    >{"id":3}|{"_from":"airports/KOA","_to":"airports/HNL","depTime":"2008-01-15T16:08:00.000Z","arrTime":"2008-01-15T16:50:00.000Z","uniqueCarrier":"YV","flightNumber":1010,"tailNumber":"N693BR","distance":163}
    >{"id":4}|{"_from":"airports/BOI","_to":"airports/PDX","depTime":"2008-01-16T02:03:00.000Z","arrTime":"2008-01-16T03:09:00.000Z","uniqueCarrier":"WN","flightNumber":1488,"tailNumber":"N242WN","distance":344}
    ```

5. View records in the database. Get the IP address from running `kubectl get service` and locating `arangodeployment-ea` in the listing. Always put `https://` in front of the IP. Username is `root` and the password is empty. All records should be stored in the `development` database.

6. View cluster health and logging in the [Confluent Control Center](http://localhost:9021).

7. Clean up all containers and created volumes when you're done.
    ```bash
    docker-compose down -v
    ```
