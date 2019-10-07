ARG VERSION=5.3.1
FROM confluentinc/cp-kafka-connect-base:${VERSION}
ENV CONNECT_PLUGIN_PATH = /etc/kafka-connect/jars
COPY kafka-connect-arangodb-*.jar ${CONNECT_PLUGIN_PATH}/kafka-connect-arangodb/