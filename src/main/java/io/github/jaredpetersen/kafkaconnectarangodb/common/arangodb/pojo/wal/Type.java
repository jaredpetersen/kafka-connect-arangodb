package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum Type {
  CREATE_DATABASE     (1100),
  DROP_DATABASE       (1101),
  CREATE_COLLECTION   (2000),
  DROP_COLLECTION     (2001),
  RENAME_COLLECTION   (2002),
  CHANGE_COLLECTION   (2003),
  TRUNCATE_COLLECTION (2004),
  CREATE_INDEX        (2100),
  DROP_INDEX          (2101),
  CREATE_VIEW         (2110),
  DROP_VIEW           (2111),
  CHANGE_VIEW         (2112),
  START_TRANSACTION   (2200),
  COMMIT_TRANSACTION  (2201),
  ABORT_TRANSACTION   (2202),
  REPSERT_DOCUMENT    (2300),
  REMOVE_DOCUMENT     (2302);

  private static final Map<Integer, Type> typesByValue = new HashMap<Integer, Type>();

  static {
    for (Type type : Type.values()) {
      typesByValue.put(type.code, type);
    }
  }

  private final int code;

  Type(int code) {
    this.code = code;
  }

  @JsonCreator
  public static Type valueOf(int code) {
    return typesByValue.get(code);
  }

  @JsonValue
  public int toValue() {
    return this.code;
  }
}
