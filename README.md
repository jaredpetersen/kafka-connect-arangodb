# Kafka Connect ArangoDB Connector
[![Build Status](https://travis-ci.org/jaredpetersen/kafka-connect-arangodb.svg?branch=master)](https://travis-ci.org/jaredpetersen/kafka-connect-arangodb)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.jaredpetersen/kafka-connect-arangodb/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.jaredpetersen/kafka-connect-arangodb)

Kafka Connect Sink Connector for ArangoDB

## Usage
Kafka Connect ArangoDB is a Kafka Connector that translates record data into `REPSERT` and `DELETE` queries that are performed against ArangoDB. Only sinking data is supported at this time.

Requires ArangoDB 3.4 or higher.

A full example of how Kafka Connect ArangoDB can be integrated into a Kafka cluster is available in the [development documentation](/docs/development/).

### Record Formats and Structures
The following record formats are supported:
* Avro (Recommended)
* JSON with Schema
* Plain JSON

With each of these formats, the record value can be structured in one of the following ways:
* Simple (Default)
* Change Data Capture

#### Simple
The Simple format is a slim record value structure that only provides the information necessary for writing to the database.

When written as plain JSON, the record value looks something like:
```json
{
  "someKey": "changed value",
  "otherKey": true
}
```

When the Kafka Connect ArangoDB Connector receives records adhering to this format, it will translate it into the following ArangoDB database changes and perform them:
* Records with a `null` value are "tombstone" records and will result in the deletion of the document from the database
* Records with a non-`null` value will be repserted (replace the full document if it exists already, insert the full document if it does not)

This format is the default, so no extra configuration is required.

#### CDC
The CDC format is a record value structure that is designed to handle records produced by Change Data Capture systems like [Debezium](https://debezium.io/). Each record value should be an object with properties `before` and `after` that store the "before" and "after" state of the document, respectively.

When written as plain JSON, the record value looks something like:
```json
{
  "before": {
    "someKey": "some value",
    "otherKey": false
  },
  "after": {
    "someKey": "changed value",
    "otherKey": true
  }
}
```

When the Kafka Connect ArangoDB Connector receives this data, it will translate it into the following ArangoDB database changes and perform them:
* Records with a `null` value are "tombstone" records and are are ignored
* Record with a non-`null` value and a `null` value for `after` will result in the deletion of the document
* Records with a non-`null` value and a non-`null` value for `after` will be repserted (replace the full document if it exists already, insert the full document if it does not)

To use this record format, configure it as a Kafka Connect Single Message Transformation in the connector's config:
```json
{
  . . .
  "transforms": "cdc",
  "transforms.cdc.type": "io.github.jaredpetersen.kafkaconnectarangodb.sink.transforms.Cdc"
}
```

### Topics
The name of the topic determines the name of the collection the record will be written to.

Record with topics that are just a plain string like `products` will go into a collection with the name `products`. If the record's topic name is period-separated like `dbserver1.mydatabase.customers`, the last period-separated value will be the collection's name (`customers` in this case). Each configured Kafka Connect ArangoDB Connector will only output data into a single database instance.

### Foreign Keys and Edge Collections
In most situations, the record values that you will want to sink into ArangoDB is not in a format that ArangoDB can use effectively. ArangoDB has it's own format for foreign keys (`{ "foreignKey": "MyCollection/1234" }`) and edges between vertices (`{ "_from": "MyCollection/1234", "_to": "MyCollection/5678" }`) that your input data likely doesn't implement by default. It is recommended that you build your own custom [Kafka Streams application](https://kafka.apache.org/documentation/streams/) to perform these mappings.

## Configuration
### Connector Properties
| Name                     | Description                         | Type     | Default | Importance |
| ------------------------ | ----------------------------------- | -------- | ------- | ---------- |
| `arangodb.host`          | ArangoDB server host.               | string   |         | high       |
| `arangodb.port`          | ArangoDB server host port number.   | int      |         | high       |
| `arangodb.user`          | ArangoDB connection username.       | string   |         | high       |
| `arangodb.password`      | ArangoDB connection password.       | password | ""      | high       |
| `arangodb.database.name` | ArangoDB database name.             | string   |         | high       |

### Single Message Transformations
| Type                                                               | Description                                        |
| ------------------------------------------------------------------ | -------------------------------------------------- |
| `io.github.jaredpetersen.kafkaconnectarangodb.sink.transforms.Cdc` | Converts records from CDC format to Simple format. |
