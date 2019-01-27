package io.github.jaredpetersen.kafkaconnectarangodb.sink.writer;

import com.arangodb.ArangoDatabase;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.model.DocumentDeleteOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Writer for writing Kafka Connect records to ArangoDB.
 */
public class Writer {
  private enum Operation { REPSERT, DELETE }

  private final ArangoDatabase database;

  /**
   * Construct a new Kafka record writer for ArangoDB.
   * @param database ArangoDB database to write to.
   */
  public Writer(final ArangoDatabase database) {
    this.database = database;

  }

  /**
   * Write Kafka records to ArangoDB.
   * @param records Records to write to ArangoDB.
   */
  public final void write(final Collection<ArangoRecord> records) {
    // Writing in batches to save trips to the database
    // Batch the records based on collection and operation in order

    // Batch
    List<ArangoRecord> batch = null;
    String batchCollection = null;
    Operation batchOperation = null;

    final Iterator<ArangoRecord> recordIterator = records.iterator();

    while (recordIterator.hasNext()) {
      // Get record information necessary for batching
      final ArangoRecord record = recordIterator.next();
      final String recordCollection = record.getCollection();
      final Operation recordOperation = this.getOperation(record);

      // Initialize the batch values for the first record to be processed
      if (batch == null) {
        batch = new ArrayList<>();
        batchCollection = recordCollection;
        batchOperation = recordOperation;
      }

      if (recordCollection.equals(batchCollection) && recordOperation == batchOperation) {
        // Record belongs to the batch, add it
        batch.add(record);
      } else {
        // Record does not belong to the batch, write the batch and start a new one
        this.writeBatch(batch);

        batch = new ArrayList<>();
        batch.add(record);

        batchCollection = recordCollection;
        batchOperation = recordOperation;
      }

      if (!recordIterator.hasNext()) {
        // No records remaining to add to the batch, write the batch and clean up
        this.writeBatch(batch);

        batch = null;
        batchCollection = null;
        batchOperation = null;
      }
    }
  }

  /**
   * Determine the database write operation to perform for the record.
   * @param record Record to determine the write operation for
   * @return Write operation
   */
  private Operation getOperation(final ArangoRecord record) {
    return (record.getValue() == null)
      ? Operation.DELETE
      : Operation.REPSERT;
  }

  /**
   * Write batch of ArangoRecords to the database.
   * @param batch ArangoRecords that all have the same database collection and write operation in common
   */
  private void writeBatch(final List<ArangoRecord> batch) {
    final ArangoRecord representativeRecord = batch.get(0);
    final String batchCollection = representativeRecord.getCollection();
    final Operation batchOperation = this.getOperation(representativeRecord);

    switch (batchOperation) {
      case REPSERT:
        repsertBatch(batchCollection, batch);
        break;
      case DELETE:
        deleteBatch(batchCollection, batch);
        break;
      default:
        // Do nothing
    }
  }

  /**
   * Delete a batch of records from the database.
   * @param collection Name of the collection to delete from
   * @param records Records to delete
   */
  private void deleteBatch(final String collection, final List<ArangoRecord> records) {
    final List<String> documentKeys = new ArrayList<>();

    for (ArangoRecord record : records) {
      documentKeys.add(record.getKey());
    }

    this.database.collection(collection).deleteDocuments(
        documentKeys,
        null,
        new DocumentDeleteOptions()
          .waitForSync(true)
          .silent(true));
  }

  /**
   * Repsert a batch of records to the database.
   * @param collection Name of the collection to repsert to
   * @param records Records to repsert
   */
  private void repsertBatch(final String collection, final List<ArangoRecord> records) {
    final List<String> documentValues = new ArrayList<>();

    for (ArangoRecord record : records) {
      documentValues.add(record.getValue());
    }

    this.database.collection(collection).insertDocuments(
        documentValues,
        new DocumentCreateOptions()
          .overwrite(true)
          .waitForSync(true)
          .silent(true));
  }
}
