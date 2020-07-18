# Demo: Kafka Connect ArangoDB Source
## Configure Connector
Acquire a [superuser Json Web Token (JWT)](https://www.arangodb.com/docs/stable/http/general.html#superuser-jwt-token). This is needed because Kafka Connect ArangoDB uses the Write Ahead Log API on each database server to generate generate records. If you have the JWT secret, you can generate a token using the [jwtgen tool](https://www.npmjs.com/package/jwtgen). In our case, the secret is `arangodbjwtsecret`.

```bash
npx jwtgen -s arangodbjwtsecret -v -a "HS256" -c 'iss=arangodb' -c 'server_id=myclient'
```

We will also want to make sure that Kafka Connect ArangoDb has some way of talking to all of the ArangoDB database servers. In our case, we have a headless service in Kubernetes that handles tracking all of those database servers for us, `arangodb-prmr`. If the listing changes, the Kubernetes service also changes and Kafka Connect ArangoDB will dynamically respond.

If you're having trouble setting this up, it can be helpful to run the following to debug your headless service:
```
kubectl -n kca-demo run -it --rm --restart=Never alpine --image=alpine
nslookup arangodb-prmr.kca-demo.svc.cluster.local
```

Send a request to the Kafka Connect REST API to configure it to use Kafka Connect ArangoDB:
```bash
curl --request POST \
    --url "$(minikube -n kca-demo service kafka-connect --url)/connectors" \
    --header 'content-type: application/json' \
    --data '{
        "name": "demo-arangodb-connector",
        "config": {
            "connector.class": "io.github.jaredpetersen.kafkaconnectarangodb.source.ArangoDbSourceConnector",
            "tasks.max": "1",
            "topics": "stream.airports,stream.flights",
            "connection.url": "arangodb-prmr.kca-demo.svc.cluster.local",
            "connection.jwt": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1OTM1ODIxNDUsImlzcyI6ImFyYW5nb2RiIiwic2VydmVyX2lkIjoibXljbGllbnQifQ.irkCPiMXynLuDSiq-y5d9BfEsnWCLctdh2DwuSXZEO8",
            "db.name": "_system"
        }
    }'

curl "$(minikube -n kca-demo service kafka-connect --url)/connectors"
curl "$(minikube -n kca-demo service kafka-connect --url)/connectors/demo-arangodb-connector/tasks"

curl --request POST \
    --url "$(minikube -n kca-demo service kafka-connect --url)/connectors/demo-arangodb-connector/tasks/0/restart"

curl --verbose -H 'Authorization: bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1OTM1ODIxNDUsImlzcyI6ImFyYW5nb2RiIiwic2VydmVyX2lkIjoibXljbGllbnQifQ.irkCPiMXynLuDSiq-y5d9BfEsnWCLctdh2DwuSXZEO8' http://172.17.0.18:8529/_db/airline/_api/wal/tail
```

```
:8529/_db/airline
curl '172.17.0.18/_api/wal/tail' -H 'Authorization: bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1OTM5MjE1NjksImlzcyI6ImFyYW5nb2RiIiwic2VydmVyX2lkIjoibXljbGllbnQifQ.V3kIrd9acWQuxf8kgMMJgnHHDMg9_-IVQtSxx5yo5yE'
```

```

```

TODO Proxy arangodb to localhost for local testing because this is slow

## Write Records
TODO

https://www.arangodb.com/wp-content/uploads/2019/02/ArangoDB-GraphCourse_Beginners.pdf
https://www.arangodb.com/arangodb_graphcourse_demodata/

## Validate
TODO
