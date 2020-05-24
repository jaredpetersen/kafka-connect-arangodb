package io.github.jaredpetersen.kafkaconnectarangodb.source.reader;

import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import io.github.jaredpetersen.kafkaconnectarangodb.sink.writer.ArangoRecord;

import java.util.Comparator;

public class WalEntryRevComparator implements Comparator<WalEntry> {
  private final byte[] decodeTable = {
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   //   0 - 15
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   //  16 - 31
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, 0,  -1, -1,   //  32 - 47
      54, 55, 56, 57, 58, 59, 60, 61,
      62, 63, -1, -1, -1, -1, -1, -1,   //  48 - 63
      -1, 2,  3,  4,  5,  6,  7,  8,
      9,  10, 11, 12, 13, 14, 15, 16,   //  64 - 79
      17, 18, 19, 20, 21, 22, 23, 24,
      25, 26, 27, -1, -1, -1, -1, 1,    //  80 - 95
      -1, 28, 29, 30, 31, 32, 33, 34,
      35, 36, 37, 38, 39, 40, 41, 42,   //  96 - 111
      43, 44, 45, 46, 47, 48, 49, 50,
      51, 52, 53, -1, -1, -1, -1, -1,   // 112 - 127
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   // 128 - 143
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   // 144 - 159
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   // 160 - 175
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   // 176 - 191
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   // 192 - 207
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   // 208 - 223
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   // 224 - 239
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1 }; // 240 - 255

  @Override
  public int compare(WalEntry walEntry1, WalEntry walEntry2) {
    return 0;
  }

  private long decode(String rev) {
    long r = 0;
    for (char c : rev.toCharArray()) {
      int i = decodeTable[c];

      if (i < 0) {
        System.out.println("uh oh");
      }

      r = (r << 6) | i;
    }

    return r;
  }
}
