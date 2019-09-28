# Demo
Let's run Kafka Connect ArangoDB in a local Kafka + ArangoDB Kubernetes cluster via MiniKube.

ArangoDB is managed by the ArangoDB operator.

Zookeeper, Kafka, Kafka Connect, etc. are managed by the Confluent Operator.

## Prerequisites
- [MiniKube](https://minikube.sigs.k8s.io/docs/start/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
- [Helm](https://helm.sh/docs/using_helm/#installing-helm) (Tillerless)

## Setup
### MiniKube
Start up a minikube instance -- we'll need more resources than what the default provides:
```bash
minikube start --cpus=2 --memory=8g
```

### Confluent Operator + Kafka Cluster
Download and extract the Confluent Operator bundle from their [website](https://docs.confluent.io/current/installation/operator/co-deployment.html):
```bash
curl -O https://platform-ops-bin.s3-us-west-1.amazonaws.com/operator/confluent-operator-20190912-v0.65.1.tar.gz
tar xvfz confluent-operator-20190912-v0.65.1.tar.gz
```

Modify replica counts in `helm/providers/private.yaml`

Install the operator:
```bash
cd confluent-operator-20190912-v0.65.1/helm
helm template --values ./providers/private.yaml --set operator.enabled=true ./confluent-operator | kubectl apply -f -
```

Install the cluster:
```bash
cd confluent-operator-20190912-v0.65.1/helm
helm template --values ./providers/private.yaml --set operator.enabled=true ./confluent-operator | kubectl apply -f -
helm template --values ./providers/private.yaml --set zookeeper.enabled=true ./confluent-operator | kubectl apply -f -
helm template --values ./providers/private.yaml --set kafka.enabled=true ./confluent-operator | kubectl apply -f -
helm template --values ./providers/private.yaml --set schemaregistry.enabled=true ./confluent-operator | kubectl apply -f -
helm template --values ./providers/private.yaml --set connect.enabled=true ./confluent-operator --output-dir ./manifests | kubectl apply -f -
helm template --values ./providers/private.yaml --set controlcenter.enabled=true ./confluent-operator | kubectl apply -f -
```

### ArangoDB Operator + ArangoDB Cluster
Install the operator:
```bash
kubectl apply -f https://raw.githubusercontent.com/arangodb/kube-arangodb/0.3.14/manifests/arango-crd.yaml
kubectl apply -f https://raw.githubusercontent.com/arangodb/kube-arangodb/0.3.14/manifests/arango-deployment.yaml
```

Install the cluster:
```bash
kubectl apply -f arangodeployment.yaml
```






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

    ^^ possible solution is to curl -L, pipe to sed,

    Zookeeper:
    ```bash
    kubectl apply -f https://raw.githubusercontent.com/pravega/zookeeper-operator/master/deploy/crds/zookeeper_v1beta1_zookeepercluster_crd.yaml
    ```

    Kafka:
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
