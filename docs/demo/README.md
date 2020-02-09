# Demo
Let's run Kafka Connect ArangoDB in a local Apache Kafka + ArangoDB Kubernetes cluster via MiniKube so that we can get a feel for how it all works together.

## Setup
### Minikube
Let's set up the cluster. We're going to use Minikube for this so [make sure you have it installed](https://minikube.sigs.k8s.io/docs/start/) along with [`kubectl` 1.14 or higher](https://kubernetes.io/docs/tasks/tools/install-kubectl/).

Set up the cluster with a docker registry and some extra juice:
```bash
minikube start --cpus 2 --memory 10g
```

### Docker
Now that we have a cluster, we'll need a Docker image that contains the Kafka Connect ArangoDB plugin. We don't publish a Docker image to public Docker registries since you will usually install multiple Kafka Connect plugins on one image. Additionally, that base image may vary depending on your preferences and use case.

Navigate to `demo/docker/` and run the following commands in a separate terminal to download the plugin and build the image for Minikube:
```bash
curl -O https://search.maven.org/remotecontent?filepath=io/github/jaredpetersen/kafka-connect-arangodb/1.0.6/kafka-connect-arangodb-1.0.6.jar
eval $(minikube docker-env)
docker build -t jaredpetersen/kafka-connect-arangodb:1.0.6 .
```

Close out this terminal when you're done -- we want to go back to our normal Docker environment.

Alternatively, build from source with `mvn package` at the root of this repository to get the JAR file.

### Kubernetes Manifests
Let's start running everything. We're going to be using [kube-arangodb](https://github.com/arangodb/kube-arangodb) to help us manage our ArangoDB cluster and just plain manifests for Zookeeper, Apache Kafka, and Kafka Connect.

Apply the manifests (you may have to give this a couple of tries due to race conditions):
```bash
kubectl apply -k kubernetes
```

Check in on the pods and wait for everything to come up:
```bash
kubectl -n kca-demo get pods
```

Be patient, this can take a few minutes.

## Usage
### Database
Log in to the database. Minikube will take you there by running:
```bash
minikube -n kca-demo service arangodb-ea
```

The username is `root` and the password is empty.

Create a new database with the name `demo`. Switch to this new database and create a document collection with the name `airports` and an edge collection with the name `flights`.

### Create Kafka Topics
Create an interactive ephemeral query pod:
```bash
kubectl -n kca-demo run kafka-create-topics --generator=run-pod/v1 --image confluentinc/cp-kafka:5.4.0 -it --rm --command /bin/bash
```

Create topics:
```
kafka-topics --create --zookeeper zookeeper-0.zookeeper:2181 --replication-factor 1 --partitions 1 --topic stream.airports
kafka-topics --create --zookeeper zookeeper-0.zookeeper:2181 --replication-factor 1 --partitions 1 --topic stream.flights
```

### Configure Kafka Connect ArangoDB
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

### Write Records
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

### Validate
Open up the `demo` database again and go to the collections we created earlier. You should now see that they have the data we just wrote into Kafka.

## Teardown
Remove all manifests:
```bash
k delete -k kubernetes
```

Delete the minikube cluster
```bash
minikube delete
```
