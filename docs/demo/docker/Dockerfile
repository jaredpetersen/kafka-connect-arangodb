FROM confluentinc/cp-kafka-connect-base:5.3.1

ENV CONNECT_PLUGINS_DIR /usr/share/java/kafka-connect

# Install Kafka Connect ArangoDB
COPY kafka-connect-arangodb-*.jar ${CONNECT_PLUGINS_DIR}/kafka-connect-arangodb/