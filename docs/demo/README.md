# Demo
Let's run Kafka Connect ArangoDB in a local Apache Kafka + ArangoDB Kubernetes cluster via MiniKube so that we can get a feel for how it all works together.

## Setup
### Minikube
Let's set up the cluster. We're going to use [Minikube](https://minikube.sigs.k8s.io/docs/start/) for this so along with [`kubectl` 1.14 or higher](https://kubernetes.io/docs/tasks/tools/install-kubectl/).

Set up the cluster with a docker registry and some extra juice:
```bash
minikube start --cpus 2 --memory 10g --kubernetes-version v1.17.6
```

Unfortunately, we have to peg the Kubernetes version to v1.17.6 due to a [bug in Minikube](https://github.com/kubernetes/minikube/issues/7828).

### Docker
Now that we have a cluster, we'll need a Docker image that contains the Kafka Connect ArangoDB plugin. Navigate to `docker/` and run the following commands in a separate terminal to download the plugin and build the image for Minikube:
```bash
curl -O 'https://search.maven.org/remotecontent?filepath=io/github/jaredpetersen/kafka-connect-arangodb/1.0.6/kafka-connect-arangodb-1.0.6.jar'
eval $(minikube docker-env)
docker build -t jaredpetersen/kafka-connect-arangodb:latest .
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

## Usage
[Source Connector](SOURCE.md)

[Sink Connector](SINK.md)

## Teardown
Remove all manifests:
```bash
k delete -k kubernetes
```

Delete the minikube cluster
```bash
minikube delete
```
