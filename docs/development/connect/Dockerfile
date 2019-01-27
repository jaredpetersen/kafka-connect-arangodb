ARG VERSION
FROM confluentinc/cp-kafka-connect-base:${VERSION}

ENV CONNECT_PLUGINS_DIR /usr/share/java/kafka-connect

# Create Plugin Directory
RUN mkdir ${CONNECT_PLUGINS_DIR}

# Install Kafka Connect ArangoDB
RUN mkdir ${CONNECT_PLUGINS_DIR}/kafka-connect-arangodb
COPY ./target/kafka-connect-arangodb-*.jar ${CONNECT_PLUGINS_DIR}/kafka-connect-arangodb
