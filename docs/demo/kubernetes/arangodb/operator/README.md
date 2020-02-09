# ArangoDB Kubernetes Operator
All Kubernetes manifests in this directory are sourced from [kube-arangodb](https://github.com/arangodb/kube-arangodb) version 0.4.3.

These manifests are copied into the repository directly because Kustomize does not allow remote manifests and it's more convenient to run one `kubectl apply` command.
