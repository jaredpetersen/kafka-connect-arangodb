# Development
## Useful Commands
```bash
mvn clean install
mvn clean compile
mvn clean test
mvn clean verify
mvn clean package
```

## Development Cluster
See [Demo] for how to run Kafka Connect ArangoDB in a local minikube cluster. This can be useful for manual end-to-end testing.

To use local code instead of one of the releases, run `mvn clean package` and copy the resulting JAR to `docs/demo/docker/kafka-connect` instead of downloading one of the releases.

### Useful Commands
```bash
kubectl -n kca-demo get pods
kubectl -n kca-demo get svc
kubectl -n kca-demo logs kafka-connect-<pod-id> -f
kubectl -n kca-demo logs kafka-connect-<pod-id> | grep arangodb
kubectl -n kca-demo exec -it kafka-connect-<pod-id> -- /bin/bash
```
