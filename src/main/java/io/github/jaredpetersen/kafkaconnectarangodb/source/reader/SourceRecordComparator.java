package io.github.jaredpetersen.kafkaconnectarangodb.source.reader;

import org.apache.kafka.connect.source.SourceRecord;

import java.util.Comparator;

public class SourceRecordComparator implements Comparator<SourceRecord> {
  @Override
  public int compare(SourceRecord sourceRecord1, SourceRecord sourceRecord2) {
    Long sourceRecord1Rev = Long.parseLong((String) sourceRecord1.sourceOffset().get("_rev"));
    Long sourceRecord2Rev = Long.parseLong((String) sourceRecord2.sourceOffset().get("_rev"));
    return sourceRecord1Rev.compareTo(sourceRecord2Rev);
  }
}
