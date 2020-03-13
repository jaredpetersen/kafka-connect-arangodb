Source system

- DB servers contain the data
- Each DB server has its own WAL
- Duplication of records will occur because of sharding across db servers
  - This is fine, it's kafka and you have to expect duplicate records anyhow
- WAL api does exist on db servers, but not accessible to users
  - Find out what not accessible actually means -- only root, etc.
- Looks like we'd use GET /_api/wal/tail
  - Uses jsonl for some reason (newline separators for objects)

- Going to need a headless service so that we can get all of the db server addresses

- Think about task configs
  - Have each task dedicated to a DB server so that you don't have to one task tailing the WAL for all
  - People are still going to be able to specify a different task count than the db servers though, so each task has to be able to get the logs for multiple servers
  - Besides, we want to give people flexibility -- ArangoDB allows you to scale up and down your database cluster and we don't want to have them also need to reconfigure the connector
  - https://www.confluent.io/blog/create-dynamic-kafka-connect-source-connectors/
  - If we split tasks by db servers, we may have a problem with new db servers that come up
    - Looks like that's what start is for on the connector. Start a thread that polls the service and then ask for a reconfiguration if the data changes
    
- Abbreviations
  - crdn = coordinator
  - agnt = agent
  - prmr = db server?
  
- Can get hosts of headless service by doing nslookup arangodb-prmr
  - Need a java equivalent
  
- Authentication
  - For a cluster, we need a superuser jwt
  - Generate one ourselves using secret
  
https://www.arangodb.com/docs/stable/deployment-kubernetes-services-and-load-balancer.html
  
kubectl -n kca-demo run -it --rm --restart=Never alpine --image=alpine
apk add curl

https://www.arangodb.com/docs/stable/http/general.html#superuser-jwt-token


```
curl --request POST \
    --url "arangodb-ea:8529/_open/auth" \
    --header 'content-type: application/json' \
    --data '{
        "username": "root",
        "password": ""
    }'

npx jwtgen -s arangodbjwtsecret -e 3600 -v -a "HS256" -c 'iss=arangodb' -c 'server_id=myclient'

eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjEuNTgxNDA3OTU2NzUzOTA4MmUrNiwiZXhwIjoxNTgzOTk5OTU2LCJpc3MiOiJhcmFuZ29kYiIsInByZWZlcnJlZF91c2VybmFtZSI6InJvb3QifQ==.D1zvCjP8h6XUWGC0s6ox0STJfBgfbGpBPNYBDNxqZHs=
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1ODE0MDEyMjcsImlzcyI6ImFyYW5nb2RiIiwic2VydmVyX2lkIjoibXljbGllbnQifQ.ccOwW5xin_gHMscnNSchYsRSBaOfQo6Y7S6FbxTYf3Y

curl --request GET \
    --url "172.17.0.13:8529/_api/version" \
    --header 'Authorization: bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1ODMyOTkyMzksImV4cCI6MTU4MzMwMjgzOSwiaXNzIjoiYXJhbmdvZGIiLCJzZXJ2ZXJfaWQiOiJteWNsaWVudCJ9.ssiZLLYuaSIrq2ujcnbV-P-_W5tQdlITBUkzgVs9K2Y' \
    --header 'content-type: application/json'


curl --request GET \
    --url "172.17.0.11:8529/_db/testdb/_api/wal/tail?syncerId=10078&from=1" \
    --header 'Authorization: bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1ODM2NTQ5NTEsImV4cCI6MTU4MzY1ODU1MSwiaXNzIjoiYXJhbmdvZGIiLCJzZXJ2ZXJfaWQiOiJteWNsaWVudCJ9.tYWMU5l9CDKe-_q8KXmRRMzb0d341AhqSeFYrO_tNbw' \
    --header 'content-type: application/json'

curl --request GET \
    --url "172.17.0.12:8529/_db/testdb/_api/wal/tail?syncerId=10078&from=1" \
    --header 'Authorization: bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1ODM2NTQ5NTEsImV4cCI6MTU4MzY1ODU1MSwiaXNzIjoiYXJhbmdvZGIiLCJzZXJ2ZXJfaWQiOiJteWNsaWVudCJ9.tYWMU5l9CDKe-_q8KXmRRMzb0d341AhqSeFYrO_tNbw' \
    --header 'content-type: application/json'

curl --request GET \
    --url "172.17.0.13:8529/_db/testdb/_api/wal/tail?syncerId=10078&from=1" \
    --header 'Authorization: bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1ODM2NTQ5NTEsImV4cCI6MTU4MzY1ODU1MSwiaXNzIjoiYXJhbmdvZGIiLCJzZXJ2ZXJfaWQiOiJteWNsaWVudCJ9.tYWMU5l9CDKe-_q8KXmRRMzb0d341AhqSeFYrO_tNbw' \
    --header 'content-type: application/json'
```

Some takeaways:
- tick values can differ between servers (but not always) so it can't be fully relied on as a global order
- have to watch out for fields like collection id and instead refer to the global collection id, since ids can differ between servers as well
- not every server has every record
- could kind of do a rough ordering and then de-duplicate
- will need syncerId field "required to have a chance at fetching reading all operations with the rocksdb storage engine"

Maybe combine data and reverse-engineer timestamps from tick and server time values?

TOD) pass syncer ID


https://github.com/arangodb/arangodb/pull/9473/files

_rev uses Hybrid Logical Clock. 

Can't decode _rev though because it's not supported and because it doesn't exist on every object
