# Demo
Let's run Kafka Connect ArangoDB in a local Kafka + ArangoDB Kubernetes cluster via MiniKube so that we can get a feel for how it all works together.

## Setup
### Minikube
Let's set up the cluster. We're going to use Minikube for this so [make sure you have it installed](https://minikube.sigs.k8s.io/docs/start/) along with [`kubectl` 1.14 or higher](https://kubernetes.io/docs/tasks/tools/install-kubectl/).

Set up the cluster with a docker registry and some extra juice:
```bash
minikube start --cpus 2 --memory 8g
```

### Docker
Now that we have a cluster, we'll need a Docker image that contains the Kafka Connect ArangoDB plugin. We don't publish a Docker image to public Docker registries for consumption since you will usually end up with multiple Kafka Connect plugins on one image.

Navigate to `/demo/docker` and run the following commands to download the plugin and build the image:
```bash
curl -O https://search.maven.org/remotecontent?filepath=io/github/jaredpetersen/kafka-connect-arangodb/1.0.4/kafka-connect-arangodb-1.0.4.jar
eval $(minikube docker-env)
docker build --tag jaredpetersen/kafka-connect-arangodb:1.0.4 .
```

Alternatively, build from source with `mvn package` at the root of the repository to get the JAR file.

### Kubernetes Manifests
Everything's ready to go! Let's deploy the manifests:
```bash
kubectl apply -k kubernetes
```


Tag the Docker image for the minikube registry and push it:
```
docker tag jaredpetersen/kafka-connect-arangodb:1.0.4 $(minikube ip):5000/jaredpetersen/kafka-connect-arangodb:1.0.4
docker push $(minikube ip):5000/jaredpetersen/kafka-connect-arangodb:1.0.4
```

## Usage
TODO