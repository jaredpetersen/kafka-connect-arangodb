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
            "db.name": "airline"
        }
    }'
```

```
java.lang.ClassCastException: java.lang.String cannot be cast to java.util.List
  at org.apache.kafka.common.config.AbstractConfig.getList(AbstractConfig.java:186)
  at io.github.jaredpetersen.kafkaconnectarangodb.source.config.ArangoDbSourceTaskConfig.getConnectionUrls(ArangoDbSourceTaskConfig.java:36)
  at io.github.jaredpetersen.kafkaconnectarangodb.source.ArangoDbSourceTask.start(ArangoDbSourceTask.java:46)
  at org.apache.kafka.connect.runtime.WorkerSourceTask.execute(WorkerSourceTask.java:208)
  at org.apache.kafka.connect.runtime.WorkerTask.doRun(WorkerTask.java:177)
  at org.apache.kafka.connect.runtime.WorkerTask.run(WorkerTask.java:227)
  at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
  at java.util.concurrent.FutureTask.run(FutureTask.java:266)
  at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
  at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
  at java.lang.Thread.run(Thread.java:748)
 [2020-07-01 07:12:39,402] ERROR WorkerSourceTask{id=demo-arangodb-connector-0} Task is being killed and will not recover until manually restarted (org.apache.kafka.connect.runtime.WorkerTask)
 [2020-07-01 07:12:39,402] INFO [Producer clientId=connector-producer-demo-arangodb-connector-0] Closing the Kafka producer with timeoutMillis = 30000 ms. (org.apache.kafka.clients.producer.KafkaProducer)
 [2020-07-01 07:13:06,812] INFO retrieving database addresses (io.github.jaredpetersen.kafkaconnectarangodb.source.DatabaseHostMonitorThread)
 [2020-07-01 07:13:06,816] INFO found database servers [arangodb-prmr.kca-demo.svc.cluster.local/172.17.0.17, arangodb-prmr.kca-demo.svc.cluster.local/172.17.0.22, arangodb-prmr.kca-demo.svc.cluster.local/172.17.0.18] (io.github.jaredpetersen.kafkaconnectarangodb.source.DatabaseHostMonitorThread)
 [2020-07-01 07:13:36,817] INFO retrieving database addresses (io.github.jaredpetersen.kafkaconnectarangodb.source.DatabaseHostMonitorThread)
 [2020-07-01 07:13:36,819] INFO found database servers [arangodb-prmr.kca-demo.svc.cluster.local/172.17.0.18, arangodb-prmr.kca-demo.svc.cluster.local/172.17.0.17, arangodb-prmr.kca-demo.svc.cluster.local/172.17.0.22] (io.github.jaredpetersen.kafkaconnectarangodb.source.DatabaseHostMonitorThread)
 [2020-07-01 07:13:39,380] INFO WorkerSourceTask{id=demo-arangodb-connector-0} Committing offsets (org.apache.kafka.connect.runtime.WorkerSourceTask)
```

## Write Records
TODO

## Validate
TODO
