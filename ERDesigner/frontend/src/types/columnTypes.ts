import { ColumnTypeOption } from '@/types/entity'

export const DefaultDatabaseColumnType: Record<string, ColumnTypeOption> = {
  INT: { label: 'INT', value: 'INT', type: 'INT', commonly: true },
  BIGINT : {label: 'BIGINT', value: 'BIGINT', type: 'INT', commonly: true},
  VARCHAR : {label: 'VARCHAR', value: 'VARCHAR', type: 'CHAR', maxLength: 65535, commonly: true},
  TEXT : {label: 'TEXT', value: 'TEXT', type: 'CHAR', commonly: true},
  DECIMAL : {label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', maxLength: 65, maxScale: 30, commonly: true},
  DATETIME : {label: 'DATETIME', value: 'DATETIME', type: 'DATETIME', commonly: true},
  BOOLEAN : {label: 'BOOLEAN', value: 'BOOLEAN', type: 'BOOLEAN', commonly: true},
  BINARY : {label: 'BINARY', value: 'BINARY', type: 'BINARY', maxLength: 65535, commonly: false}
}

export const MysqlColumnType: Record<string, ColumnTypeOption> = {
  TINYINT : {label: 'TINYINT', value: 'TINYINT', type: 'INT', maxLength: 255, commonly: true},
  INT : {label: 'INT', value: 'INT', type: 'INT', maxLength: 255, commonly: true},
  BIGINT : {label: 'BIGINT', value: 'BIGINT', type: 'INT', maxLength: 255, commonly: true},
  DECIMAL : {label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', maxLength: 65, maxScale: 30, commonly: true},
  BOOLEAN : {label: 'BOOLEAN', value: 'BOOLEAN', type: 'BOOLEAN', commonly: true},   // 1字节整数TINYINT(1)
  CHAR : {label: 'CHAR', value: 'CHAR', type: 'CHAR', maxLength: 255, commonly: true},
  VARCHAR : {label: 'VARCHAR', value: 'VARCHAR', type: 'CHAR', maxLength: 65535, commonly: true},
  TEXT : {label: 'TEXT', value: 'TEXT', type: 'CHAR', commonly: true},
  DATETIME : {label: 'DATETIME', value: 'DATETIME', type: 'DATETIME', maxLength: 6, commonly: true},  
  ENUM : {label: 'ENUM', value: 'ENUM', type: 'ENUM', commonly: true},
  SMALLINT : {label: 'SMALLINT', value: 'SMALLINT', type: 'INT', maxLength: 255, commonly: false},
  MEDIUMINT : {label: 'MEDIUMINT', value: 'MEDIUMINT', type: 'INT', maxLength: 255, commonly: false}, 
  FLOAT : {label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', maxLength: 255, maxScale: 30, commonly: false},
  DOUBLE : {label: 'DOUBLE', value: 'DOUBLE', type: 'DECIMAL', maxLength: 255, maxScale: 30, commonly: false},
  REAL : {label: 'REAL', value: 'REAL', type: 'DECIMAL', maxLength: 255, maxScale: 30, commonly: false},
  NUMERIC : {label: 'NUMERIC', value: 'NUMERIC', type: 'DECIMAL', maxLength: 65, maxScale: 30, commonly: false},
  TINYTEXT : {label: 'TINYTEXT', value: 'TINYTEXT', type: 'CHAR', commonly: false},
  MEDIUMTEXT : {label: 'MEDIUMTEXT', value: 'MEDIUMTEXT', type: 'CHAR', commonly: false},
  LONGTEXT : {label: 'LONGTEXT', value: 'LONGTEXT', type: 'CHAR', commonly: false},
  BINARY : {label: 'BINARY', value: 'BINARY', type: 'BINARY', maxLength: 255, commonly: false},
  VARBINARY : {label: 'VARBINARY', value: 'VARBINARY', type: 'BINARY', maxLength: 65535, commonly: false},
  TINYBLOB : {label: 'TINYBLOB', value: 'TINYBLOB', type: 'BINARY', commonly: false},
  BLOB : {label: 'BLOB', value: 'BLOB', type: 'BINARY', commonly: false},
  MEDIUMBLOB : {label: 'MEDIUMBLOB', value: 'MEDIUMBLOB', type: 'BINARY', commonly: false},
  LONGBLOB : {label: 'LONGBLOB', value: 'LONGBLOB', type: 'BINARY', commonly: false},
  DATE : {label: 'DATE', value: 'DATE', type: 'DATETIME', commonly: false},
  TIME : {label: 'TIME', value: 'TIME', type: 'DATETIME', maxLength: 6, commonly: false},
  TIMESTAMP : {label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'DATETIME', maxLength: 6, commonly: false},
  YEAR : {label: 'YEAR', value: 'YEAR', type: 'DATETIME', commonly: false},
  SET : {label: 'SET', value: 'SET', type: 'SET', commonly: false},
  JSON : {label: 'JSON', value: 'JSON', type: 'JSON', commonly: false}
}

export const MariaDBColumnType: Record<string, ColumnTypeOption> = {
  
  TINYINT: { label: 'TINYINT', value: 'TINYINT', type: 'INT', maxLength: 255, commonly: true },
  INT: { label: 'INT', value: 'INT', type: 'INT', maxLength: 255, commonly: true },
  BIGINT: { label: 'BIGINT', value: 'BIGINT', type: 'INT', maxLength: 255, commonly: true },
  DECIMAL: { label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', maxLength: 65, maxScale: 30, commonly: true },
  BOOLEAN: { label: 'BOOLEAN', value: 'BOOLEAN', type: 'BOOLEAN', commonly: true },
  CHAR: { label: 'CHAR', value: 'CHAR', type: 'CHAR', maxLength: 255, commonly: true },
  VARCHAR: { label: 'VARCHAR', value: 'VARCHAR', type: 'CHAR', maxLength: 65535, commonly: true },
  TEXT: { label: 'TEXT', value: 'TEXT', type: 'CHAR', commonly: true },
  DATETIME: { label: 'DATETIME', value: 'DATETIME', type: 'DATETIME', maxLength: 6, commonly: true },
  ENUM: { label: 'ENUM', value: 'ENUM', type: 'ENUM', commonly: true },

  // 整数类型
  SMALLINT: { label: 'SMALLINT', value: 'SMALLINT', type: 'INT', maxLength: 255, commonly: false },
  MEDIUMINT: { label: 'MEDIUMINT', value: 'MEDIUMINT', type: 'INT', maxLength: 255, commonly: false },
  
  // 精确数值类型
  NUMERIC: { label: 'NUMERIC', value: 'NUMERIC', type: 'DECIMAL', maxLength: 65, maxScale: 30, commonly: false },
  
  // 浮点数类型
  FLOAT: { label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', maxLength: 255, maxScale: 30, commonly: false },
  DOUBLE: { label: 'DOUBLE', value: 'DOUBLE', type: 'DECIMAL', maxLength: 255, maxScale: 30, commonly: false },
  REAL: { label: 'REAL', value: 'REAL', type: 'DECIMAL', maxLength: 255, maxScale: 30, commonly: false },
  
  // 布尔类型
  BOOL: { label: 'BOOL', value: 'BOOL', type: 'BOOLEAN', commonly: false },
  
  // 位类型
  BIT: { label: 'BIT', value: 'BIT', type: 'BIT', maxLength: 64, commonly: false },

  // 二进制类型
  BINARY: { label: 'BINARY', value: 'BINARY', type: 'BINARY', maxLength: 255, commonly: false },
  VARBINARY: { label: 'VARBINARY', value: 'VARBINARY', type: 'BINARY', maxLength: 65535, commonly: false },
  
  // 文本类型
  TINYTEXT: { label: 'TINYTEXT', value: 'TINYTEXT', type: 'CHAR', commonly: false },
  MEDIUMTEXT: { label: 'MEDIUMTEXT', value: 'MEDIUMTEXT', type: 'CHAR', commonly: false },
  LONGTEXT: { label: 'LONGTEXT', value: 'LONGTEXT', type: 'CHAR', commonly: false },
  
  // BLOB类型
  TINYBLOB: { label: 'TINYBLOB', value: 'TINYBLOB', type: 'BINARY', commonly: false },
  BLOB: { label: 'BLOB', value: 'BLOB', type: 'BINARY', commonly: false },
  MEDIUMBLOB: { label: 'MEDIUMBLOB', value: 'MEDIUMBLOB', type: 'BINARY', commonly: false },
  LONGBLOB: { label: 'LONGBLOB', value: 'LONGBLOB', type: 'BINARY', commonly: false },
  
  // 日期时间类型
  DATE: { label: 'DATE', value: 'DATE', type: 'DATETIME', commonly: false },
  TIME: { label: 'TIME', value: 'TIME', type: 'DATETIME', maxLength: 6, commonly: false },
  TIMESTAMP: { label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'DATETIME', maxLength: 6, commonly: false },
  YEAR: { label: 'YEAR', value: 'YEAR', type: 'DATETIME', commonly: false },
  
  // 枚举和集合类型
  SET: { label: 'SET', value: 'SET', type: 'SET', commonly: false },
  
  // JSON类型
  JSON: { label: 'JSON', value: 'JSON', type: 'JSON', commonly: false }
}

export const FirebirdColumnType: Record<string, ColumnTypeOption> = {
  // 常用类型
  INTEGER: { label: 'INTEGER', value: 'INTEGER', type: 'INT', commonly: true },
  BIGINT: { label: 'BIGINT', value: 'BIGINT', type: 'INT', commonly: true },
  DECIMAL: { label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', maxLength: 38, maxScale: 38, commonly: true },
  NUMERIC: { label: 'NUMERIC', value: 'NUMERIC', type: 'DECIMAL', maxLength: 38, maxScale: 38, commonly: true },
  VARCHAR: { label: 'VARCHAR', value: 'VARCHAR', type: 'TEXT', maxLength: 32765, commonly: true },
  CHAR: { label: 'CHAR', value: 'CHAR', type: 'TEXT', maxLength: 32767, commonly: true },
  DATE: { label: 'DATE', value: 'DATE', type: 'DATETIME', commonly: true },
  TIME: { label: 'TIME', value: 'TIME', type: 'DATETIME', commonly: true },
  TIMESTAMP: { label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'DATETIME', commonly: true },
  BOOLEAN: { label: 'BOOLEAN', value: 'BOOLEAN', type: 'BOOLEAN', commonly: true },
  
  // 其他类型
  SMALLINT: { label: 'SMALLINT', value: 'SMALLINT', type: 'INT', commonly: false },
  FLOAT: { label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', commonly: false },
  DOUBLE_PRECISION: { label: 'DOUBLE PRECISION', value: 'DOUBLE PRECISION', type: 'DECIMAL', commonly: false },
  CHARACTER: { label: 'CHARACTER', value: 'CHARACTER', type: 'TEXT', maxLength: 32767, commonly: false },
  CHAR_VARYING: { label: 'CHAR VARYING', value: 'CHAR VARYING', type: 'TEXT', maxLength: 32765, commonly: false },
  CHARACTER_VARYING: { label: 'CHARACTER VARYING', value: 'CHARACTER VARYING', type: 'TEXT', maxLength: 32765, commonly: false },
  BINARY: { label: 'BINARY', value: 'BINARY', type: 'BINARY', maxLength: 32767, commonly: false },
  VARBINARY: { label: 'VARBINARY', value: 'VARBINARY', type: 'BINARY', maxLength: 32765, commonly: false },
  BINARY_VARYING: { label: 'BINARY VARYING', value: 'BINARY VARYING', type: 'BINARY', maxLength: 32765, commonly: false },
  BLOB: { label: 'BLOB', value: 'BLOB', type: 'BINARY', commonly: false },
  TIME_WITH_TIME_ZONE: { label: 'TIME WITH TIME ZONE', value: 'TIME WITH TIME ZONE', type: 'DATETIME', commonly: false },
  TIMESTAMP_WITH_TIME_ZONE: { label: 'TIMESTAMP WITH TIME ZONE', value: 'TIMESTAMP WITH TIME ZONE', type: 'DATETIME', commonly: false }
}

export const CockroachDBColumnType: Record<string, ColumnTypeOption> = {
  // 常用类型
  INT: { label: 'INT', value: 'INT', type: 'INT', commonly: true },
  BIGINT: { label: 'BIGINT', value: 'BIGINT', type: 'INT', commonly: true },
  DECIMAL: { label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', maxLength: 1000, maxScale: 1000, commonly: true },
  STRING: { label: 'STRING', value: 'STRING', type: 'TEXT', commonly: true },
  VARCHAR: { label: 'VARCHAR', value: 'VARCHAR', type: 'TEXT', commonly: true },
  DATE: { label: 'DATE', value: 'DATE', type: 'DATETIME', commonly: true },
  TIMESTAMP: { label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'DATETIME', commonly: true },
  TIMESTAMPTZ: { label: 'TIMESTAMPTZ', value: 'TIMESTAMPTZ', type: 'DATETIME', commonly: true },
  BOOL: { label: 'BOOL', value: 'BOOL', type: 'BOOLEAN', commonly: true },
  UUID: { label: 'UUID', value: 'UUID', type: 'TEXT', commonly: true },
  
  // 其他类型
  SMALLINT: { label: 'SMALLINT', value: 'SMALLINT', type: 'INT', commonly: false },
  INTEGER: { label: 'INTEGER', value: 'INTEGER', type: 'INT', commonly: false },
  INT2: { label: 'INT2', value: 'INT2', type: 'INT', commonly: false },
  INT4: { label: 'INT4', value: 'INT4', type: 'INT', commonly: false },
  INT8: { label: 'INT8', value: 'INT8', type: 'INT', commonly: false },
  SERIAL: { label: 'SERIAL', value: 'SERIAL', type: 'INT', commonly: false },
  BIGSERIAL: { label: 'BIGSERIAL', value: 'BIGSERIAL', type: 'INT', commonly: false },
  SMALLSERIAL: { label: 'SMALLSERIAL', value: 'SMALLSERIAL', type: 'INT', commonly: false },
  FLOAT: { label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', commonly: false },
  FLOAT4: { label: 'FLOAT4', value: 'FLOAT4', type: 'DECIMAL', commonly: false },
  FLOAT8: { label: 'FLOAT8', value: 'FLOAT8', type: 'DECIMAL', commonly: false },
  REAL: { label: 'REAL', value: 'REAL', type: 'DECIMAL', commonly: false },
  DOUBLE_PRECISION: { label: 'DOUBLE PRECISION', value: 'DOUBLE PRECISION', type: 'DECIMAL', commonly: false },
  NUMERIC: { label: 'NUMERIC', value: 'NUMERIC', type: 'DECIMAL', maxLength: 1000, maxScale: 1000, commonly: false },
  CHAR: { label: 'CHAR', value: 'CHAR', type: 'TEXT', commonly: false },
  CHARACTER: { label: 'CHARACTER', value: 'CHARACTER', type: 'TEXT', commonly: false },
  TEXT: { label: 'TEXT', value: 'TEXT', type: 'TEXT', commonly: false },
  BYTES: { label: 'BYTES', value: 'BYTES', type: 'BINARY', commonly: false },
  BYTEA: { label: 'BYTEA', value: 'BYTEA', type: 'BINARY', commonly: false },
  TIME: { label: 'TIME', value: 'TIME', type: 'DATETIME', commonly: false },
  TIMETZ: { label: 'TIMETZ', value: 'TIMETZ', type: 'DATETIME', commonly: false },
  INTERVAL: { label: 'INTERVAL', value: 'INTERVAL', type: 'DATETIME', commonly: false },
  JSONB: { label: 'JSONB', value: 'JSONB', type: 'JSON', commonly: false },
  INET: { label: 'INET', value: 'INET', type: 'TEXT', commonly: false },
  ARRAY: { label: 'ARRAY', value: 'ARRAY', type: 'TEXT', commonly: false },
  ENUM: { label: 'ENUM', value: 'ENUM', type: 'ENUM', commonly: false }
}

export const DuckDBColumnType: Record<string, ColumnTypeOption> = {
  // 常用类型
  INTEGER: { label: 'INTEGER', value: 'INTEGER', type: 'INT', commonly: true },
  BIGINT: { label: 'BIGINT', value: 'BIGINT', type: 'INT', commonly: true },
  DECIMAL: { label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', maxLength: 38, maxScale: 38, commonly: true },
  NUMERIC: { label: 'NUMERIC', value: 'NUMERIC', type: 'DECIMAL', maxLength: 38, maxScale: 38, commonly: true },
  VARCHAR: { label: 'VARCHAR', value: 'VARCHAR', type: 'TEXT', commonly: true },
  TEXT: { label: 'TEXT', value: 'TEXT', type: 'TEXT', commonly: true },
  DATE: { label: 'DATE', value: 'DATE', type: 'DATETIME', commonly: true },
  TIMESTAMP: { label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'DATETIME', commonly: true },
  TIMESTAMPTZ: { label: 'TIMESTAMPTZ', value: 'TIMESTAMPTZ', type: 'DATETIME', commonly: true },
  BOOLEAN: { label: 'BOOLEAN', value: 'BOOLEAN', type: 'BOOLEAN', commonly: true },
  UUID: { label: 'UUID', value: 'UUID', type: 'TEXT', commonly: true },
  
  // 其他类型
  TINYINT: { label: 'TINYINT', value: 'TINYINT', type: 'INT', commonly: false },
  SMALLINT: { label: 'SMALLINT', value: 'SMALLINT', type: 'INT', commonly: false },
  HUGEINT: { label: 'HUGEINT', value: 'HUGEINT', type: 'INT', commonly: false },
  UTINYINT: { label: 'UTINYINT', value: 'UTINYINT', type: 'INT', commonly: false },
  USMALLINT: { label: 'USMALLINT', value: 'USMALLINT', type: 'INT', commonly: false },
  UINTEGER: { label: 'UINTEGER', value: 'UINTEGER', type: 'INT', commonly: false },
  UBIGINT: { label: 'UBIGINT', value: 'UBIGINT', type: 'INT', commonly: false },
  VARINT: { label: 'VARINT', value: 'VARINT', type: 'INT', commonly: false },
  FLOAT: { label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', commonly: false },
  DOUBLE: { label: 'DOUBLE', value: 'DOUBLE', type: 'DECIMAL', commonly: false },
  CHAR: { label: 'CHAR', value: 'CHAR', type: 'TEXT', commonly: false },
  BPCHAR: { label: 'BPCHAR', value: 'BPCHAR', type: 'TEXT', commonly: false },
  STRING: { label: 'STRING', value: 'STRING', type: 'TEXT', commonly: false },
  BLOB: { label: 'BLOB', value: 'BLOB', type: 'BINARY', commonly: false },
  BYTEA: { label: 'BYTEA', value: 'BYTEA', type: 'BINARY', commonly: false },
  BINARY: { label: 'BINARY', value: 'BINARY', type: 'BINARY', commonly: false },
  VARBINARY: { label: 'VARBINARY', value: 'VARBINARY', type: 'BINARY', commonly: false },
  BIT: { label: 'BIT', value: 'BIT', type: 'TEXT', commonly: false },
  TIME: { label: 'TIME', value: 'TIME', type: 'DATETIME', commonly: false },
  DATETIME: { label: 'DATETIME', value: 'DATETIME', type: 'DATETIME', commonly: false },
  INTERVAL: { label: 'INTERVAL', value: 'INTERVAL', type: 'DATETIME', commonly: false },
  JSON: { label: 'JSON', value: 'JSON', type: 'JSON', commonly: false },
  ARRAY: { label: 'ARRAY', value: 'ARRAY', type: 'TEXT', commonly: false },
  LIST: { label: 'LIST', value: 'LIST', type: 'TEXT', commonly: false },
  MAP: { label: 'MAP', value: 'MAP', type: 'TEXT', commonly: false },
  STRUCT: { label: 'STRUCT', value: 'STRUCT', type: 'TEXT', commonly: false },
  UNION: { label: 'UNION', value: 'UNION', type: 'TEXT', commonly: false }
}

export const PolarDBColumnType: Record<string, ColumnTypeOption> = {
  // 常用类型
  INT: { label: 'INT', value: 'INT', type: 'INT', commonly: true },
  INTEGER: { label: 'INTEGER', value: 'INTEGER', type: 'INT', commonly: true },
  BIGINT: { label: 'BIGINT', value: 'BIGINT', type: 'INT', commonly: true },
  DECIMAL: { label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', maxLength: 65, maxScale: 30, commonly: true },
  NUMERIC: { label: 'NUMERIC', value: 'NUMERIC', type: 'DECIMAL', maxLength: 65, maxScale: 30, commonly: true },
  VARCHAR: { label: 'VARCHAR', value: 'VARCHAR', type: 'TEXT', maxLength: 65535, commonly: true },
  CHAR: { label: 'CHAR', value: 'CHAR', type: 'TEXT', maxLength: 255, commonly: true },
  TEXT: { label: 'TEXT', value: 'TEXT', type: 'TEXT', commonly: true },
  DATE: { label: 'DATE', value: 'DATE', type: 'DATETIME', commonly: true },
  DATETIME: { label: 'DATETIME', value: 'DATETIME', type: 'DATETIME', commonly: true },
  TIMESTAMP: { label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'DATETIME', commonly: true },
  JSON: { label: 'JSON', value: 'JSON', type: 'JSON', commonly: true },
  
  // 其他类型
  TINYINT: { label: 'TINYINT', value: 'TINYINT', type: 'INT', commonly: false },
  SMALLINT: { label: 'SMALLINT', value: 'SMALLINT', type: 'INT', commonly: false },
  MEDIUMINT: { label: 'MEDIUMINT', value: 'MEDIUMINT', type: 'INT', commonly: false },
  FLOAT: { label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', commonly: false },
  REAL: { label: 'REAL', value: 'REAL', type: 'DECIMAL', commonly: false },
  DOUBLE: { label: 'DOUBLE', value: 'DOUBLE', type: 'DECIMAL', commonly: false },
  DOUBLE_PRECISION: { label: 'DOUBLE PRECISION', value: 'DOUBLE PRECISION', type: 'DECIMAL', commonly: false },
  BINARY: { label: 'BINARY', value: 'BINARY', type: 'BINARY', maxLength: 255, commonly: false },
  VARBINARY: { label: 'VARBINARY', value: 'VARBINARY', type: 'BINARY', maxLength: 65535, commonly: false },
  BLOB: { label: 'BLOB', value: 'BLOB', type: 'BINARY', commonly: false },
  TINYBLOB: { label: 'TINYBLOB', value: 'TINYBLOB', type: 'BINARY', commonly: false },
  MEDIUMBLOB: { label: 'MEDIUMBLOB', value: 'MEDIUMBLOB', type: 'BINARY', commonly: false },
  LONGBLOB: { label: 'LONGBLOB', value: 'LONGBLOB', type: 'BINARY', commonly: false },
  TINYTEXT: { label: 'TINYTEXT', value: 'TINYTEXT', type: 'TEXT', commonly: false },
  MEDIUMTEXT: { label: 'MEDIUMTEXT', value: 'MEDIUMTEXT', type: 'TEXT', commonly: false },
  LONGTEXT: { label: 'LONGTEXT', value: 'LONGTEXT', type: 'TEXT', commonly: false },
  TIME: { label: 'TIME', value: 'TIME', type: 'DATETIME', commonly: false },
  YEAR: { label: 'YEAR', value: 'YEAR', type: 'DATETIME', commonly: false },
  ENUM: { label: 'ENUM', value: 'ENUM', type: 'ENUM', commonly: false },
  SET: { label: 'SET', value: 'SET', type: 'SET', commonly: false }
}

export const OceanBaseColumnType: Record<string, ColumnTypeOption> = {
  // 常用类型
  INT: { label: 'INT', value: 'INT', type: 'INT', commonly: true },
  INTEGER: { label: 'INTEGER', value: 'INTEGER', type: 'INT', commonly: true },
  BIGINT: { label: 'BIGINT', value: 'BIGINT', type: 'INT', commonly: true },
  DECIMAL: { label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', maxLength: 65, maxScale: 30, commonly: true },
  VARCHAR: { label: 'VARCHAR', value: 'VARCHAR', type: 'TEXT', maxLength: 65535, commonly: true },
  CHAR: { label: 'CHAR', value: 'CHAR', type: 'TEXT', maxLength: 255, commonly: true },
  BOOLEAN: { label: 'BOOLEAN', value: 'BOOLEAN', type: 'BOOLEAN', commonly: true },
  DATE: { label: 'DATE', value: 'DATE', type: 'DATETIME', commonly: true },
  DATETIME: { label: 'DATETIME', value: 'DATETIME', type: 'DATETIME', commonly: true },
  TIMESTAMP: { label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'DATETIME', commonly: true },
  JSON: { label: 'JSON', value: 'JSON', type: 'JSON', commonly: true },
  
  // 其他类型
  TINYINT: { label: 'TINYINT', value: 'TINYINT', type: 'INT', commonly: false },
  SMALLINT: { label: 'SMALLINT', value: 'SMALLINT', type: 'INT', commonly: false },
  MEDIUMINT: { label: 'MEDIUMINT', value: 'MEDIUMINT', type: 'INT', commonly: false },
  NUMERIC: { label: 'NUMERIC', value: 'NUMERIC', type: 'DECIMAL', maxLength: 65, maxScale: 30, commonly: false },
  FLOAT: { label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', commonly: false },
  DOUBLE: { label: 'DOUBLE', value: 'DOUBLE', type: 'DECIMAL', commonly: false },
  BIT: { label: 'BIT', value: 'BIT', type: 'TEXT', commonly: false },
  BOOL: { label: 'BOOL', value: 'BOOL', type: 'BOOLEAN', commonly: false },
  BINARY: { label: 'BINARY', value: 'BINARY', type: 'BINARY', maxLength: 255, commonly: false },
  VARBINARY: { label: 'VARBINARY', value: 'VARBINARY', type: 'BINARY', maxLength: 65535, commonly: false },
  TIME: { label: 'TIME', value: 'TIME', type: 'DATETIME', commonly: false },
  YEAR: { label: 'YEAR', value: 'YEAR', type: 'DATETIME', commonly: false }
}

export const TiDBColumnType: Record<string, ColumnTypeOption> = {
  // 常用类型
  INT: { label: 'INT', value: 'INT', type: 'INT', commonly: true },
  INTEGER: { label: 'INTEGER', value: 'INTEGER', type: 'INT', commonly: true },
  BIGINT: { label: 'BIGINT', value: 'BIGINT', type: 'INT', commonly: true },
  DECIMAL: { label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', maxLength: 65, maxScale: 30, commonly: true },
  NUMERIC: { label: 'NUMERIC', value: 'NUMERIC', type: 'DECIMAL', maxLength: 65, maxScale: 30, commonly: true },
  VARCHAR: { label: 'VARCHAR', value: 'VARCHAR', type: 'TEXT', maxLength: 65535, commonly: true },
  CHAR: { label: 'CHAR', value: 'CHAR', type: 'TEXT', maxLength: 255, commonly: true },
  TEXT: { label: 'TEXT', value: 'TEXT', type: 'TEXT', commonly: true },
  DATE: { label: 'DATE', value: 'DATE', type: 'DATETIME', commonly: true },
  DATETIME: { label: 'DATETIME', value: 'DATETIME', type: 'DATETIME', commonly: true },
  TIMESTAMP: { label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'DATETIME', commonly: true },
  JSON: { label: 'JSON', value: 'JSON', type: 'JSON', commonly: true },
  
  // 其他类型
  TINYINT: { label: 'TINYINT', value: 'TINYINT', type: 'INT', commonly: false },
  SMALLINT: { label: 'SMALLINT', value: 'SMALLINT', type: 'INT', commonly: false },
  MEDIUMINT: { label: 'MEDIUMINT', value: 'MEDIUMINT', type: 'INT', commonly: false },
  FLOAT: { label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', commonly: false },
  DOUBLE: { label: 'DOUBLE', value: 'DOUBLE', type: 'DECIMAL', commonly: false },
  BIT: { label: 'BIT', value: 'BIT', type: 'TEXT', commonly: false },
  BOOL: { label: 'BOOL', value: 'BOOL', type: 'BOOLEAN', commonly: false },
  BOOLEAN: { label: 'BOOLEAN', value: 'BOOLEAN', type: 'BOOLEAN', commonly: false },
  BINARY: { label: 'BINARY', value: 'BINARY', type: 'BINARY', maxLength: 255, commonly: false },
  VARBINARY: { label: 'VARBINARY', value: 'VARBINARY', type: 'BINARY', maxLength: 65535, commonly: false },
  BLOB: { label: 'BLOB', value: 'BLOB', type: 'BINARY', commonly: false },
  TINYBLOB: { label: 'TINYBLOB', value: 'TINYBLOB', type: 'BINARY', commonly: false },
  MEDIUMBLOB: { label: 'MEDIUMBLOB', value: 'MEDIUMBLOB', type: 'BINARY', commonly: false },
  LONGBLOB: { label: 'LONGBLOB', value: 'LONGBLOB', type: 'BINARY', commonly: false },
  TINYTEXT: { label: 'TINYTEXT', value: 'TINYTEXT', type: 'TEXT', commonly: false },
  MEDIUMTEXT: { label: 'MEDIUMTEXT', value: 'MEDIUMTEXT', type: 'TEXT', commonly: false },
  LONGTEXT: { label: 'LONGTEXT', value: 'LONGTEXT', type: 'TEXT', commonly: false },
  TIME: { label: 'TIME', value: 'TIME', type: 'DATETIME', commonly: false },
  YEAR: { label: 'YEAR', value: 'YEAR', type: 'DATETIME', commonly: false },
  ENUM: { label: 'ENUM', value: 'ENUM', type: 'ENUM', commonly: false },
  SET: { label: 'SET', value: 'SET', type: 'SET', commonly: false }
}

export const YugabyteColumnType: Record<string, ColumnTypeOption> = {
  // 常用类型
  INT: { label: 'INT', value: 'INT', type: 'INT', commonly: true },
  BIGINT: { label: 'BIGINT', value: 'BIGINT', type: 'INT', commonly: true },
  DECIMAL: { label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', commonly: true },
  VARCHAR: { label: 'VARCHAR', value: 'VARCHAR', type: 'TEXT', commonly: true },
  TEXT: { label: 'TEXT', value: 'TEXT', type: 'TEXT', commonly: true },
  DATE: { label: 'DATE', value: 'DATE', type: 'DATETIME', commonly: true },
  TIMESTAMP: { label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'DATETIME', commonly: true },
  TIMESTAMPTZ: { label: 'TIMESTAMPTZ', value: 'TIMESTAMPTZ', type: 'DATETIME', commonly: true },
  BOOLEAN: { label: 'BOOLEAN', value: 'BOOLEAN', type: 'BOOLEAN', commonly: true },
  UUID: { label: 'UUID', value: 'UUID', type: 'TEXT', commonly: true },
  JSONB: { label: 'JSONB', value: 'JSONB', type: 'JSON', commonly: true },
  
  // 其他类型
  SMALLINT: { label: 'SMALLINT', value: 'SMALLINT', type: 'INT', commonly: false },
  TINYINT: { label: 'TINYINT', value: 'TINYINT', type: 'INT', commonly: false },
  VARINT: { label: 'VARINT', value: 'VARINT', type: 'INT', commonly: false },
  SERIAL: { label: 'SERIAL', value: 'SERIAL', type: 'INT', commonly: false },
  BIGSERIAL: { label: 'BIGSERIAL', value: 'BIGSERIAL', type: 'INT', commonly: false },
  SMALLSERIAL: { label: 'SMALLSERIAL', value: 'SMALLSERIAL', type: 'INT', commonly: false },
  NUMERIC: { label: 'NUMERIC', value: 'NUMERIC', type: 'DECIMAL', commonly: false },
  FLOAT: { label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', commonly: false },
  DOUBLE: { label: 'DOUBLE', value: 'DOUBLE', type: 'DECIMAL', commonly: false },
  REAL: { label: 'REAL', value: 'REAL', type: 'DECIMAL', commonly: false },
  CHAR: { label: 'CHAR', value: 'CHAR', type: 'TEXT', commonly: false },
  TIME: { label: 'TIME', value: 'TIME', type: 'DATETIME', commonly: false },
  INTERVAL: { label: 'INTERVAL', value: 'INTERVAL', type: 'DATETIME', commonly: false },
  TIMEUUID: { label: 'TIMEUUID', value: 'TIMEUUID', type: 'TEXT', commonly: false },
  BIT: { label: 'BIT', value: 'BIT', type: 'TEXT', commonly: false },
  BIT_VARYING: { label: 'BIT VARYING', value: 'BIT VARYING', type: 'TEXT', commonly: false },
  VARBIT: { label: 'VARBIT', value: 'VARBIT', type: 'TEXT', commonly: false },
  BYTEA: { label: 'BYTEA', value: 'BYTEA', type: 'BINARY', commonly: false },
  JSON: { label: 'JSON', value: 'JSON', type: 'JSON', commonly: false },
  LIST: { label: 'LIST', value: 'LIST', type: 'TEXT', commonly: false },
  MAP: { label: 'MAP', value: 'MAP', type: 'TEXT', commonly: false },
  SET: { label: 'SET', value: 'SET', type: 'SET', commonly: false },
  ENUM: { label: 'ENUM', value: 'ENUM', type: 'ENUM', commonly: false }
}

export const SQLiteColumnType: Record<string, ColumnTypeOption> =  {
  INT: { label: 'INT', value: 'INT', type: 'INT', commonly: true },
  BIGINT: { label: 'BIGINT', value: 'BIGINT', type: 'INT', commonly: true },
  BOOLEAN: { label: 'BOOLEAN', value: 'BOOLEAN', type: 'INT', commonly: true },
  DECIMAL: { label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', commonly: true },
  VARCHAR: { label: 'VARCHAR', value: 'VARCHAR', type: 'TEXT', commonly: true },
  TEXT: { label: 'TEXT', value: 'TEXT', type: 'TEXT', commonly: true },
  DATETIME: { label: 'DATETIME', value: 'DATETIME', type: 'TEXT', commonly: true },
  TINYINT: { label: 'TINYINT', value: 'TINYINT', type: 'INT', commonly: false },
  SMALLINT: { label: 'SMALLINT', value: 'SMALLINT', type: 'INT', commonly: false },
  MEDIUMINT: { label: 'MEDIUMINT', value: 'MEDIUMINT', type: 'INT', commonly: false },
  REAL: { label: 'REAL', value: 'REAL', type: 'DECIMAL', commonly: false },
  DOUBLE: { label: 'DOUBLE', value: 'DOUBLE', type: 'DECIMAL', commonly: false },
  FLOAT: { label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', commonly: false },
  MONEY: { label: 'MONEY', value: 'MONEY', type: 'DECIMAL', commonly: false },
  CURRENCY: { label: 'CURRENCY', value: 'CURRENCY', type: 'DECIMAL', commonly: false },
  NUMERIC: { label: 'NUMERIC', value: 'NUMERIC', type: 'DECIMAL', commonly: false },
  CHAR: { label: 'CHAR', value: 'CHAR', type: 'TEXT', commonly: false },
  NCHAR: { label: 'NCHAR', value: 'NCHAR', type: 'TEXT', commonly: false },
  NVARCHAR: { label: 'NVARCHAR', value: 'NVARCHAR', type: 'TEXT', commonly: false },
  CLOB: { label: 'CLOB', value: 'CLOB', type: 'TEXT', commonly: false },
  BLOB: { label: 'BLOB', value: 'BLOB', type: 'BLOB', commonly: false },
  DATE: { label: 'DATE', value: 'DATE', type: 'TEXT', commonly: false },
  TIME: { label: 'TIME', value: 'TIME', type: 'TEXT', commonly: false },
  TIMESTAMP: { label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'TEXT', commonly: false }
}

export const PostgreSQLColumnType: Record<string, ColumnTypeOption> =  {

  INTEGER: { label: 'INTEGER', value: 'INTEGER', type: 'INT', commonly: true },
  BIGINT: { label: 'BIGINT', value: 'BIGINT', type: 'INT', commonly: true },
  SERIAL: { label: 'SERIAL', value: 'SERIAL', type: 'INT', commonly: true },
  DECIMAL: { label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', maxLength: 38, maxScale: 10, commonly: true },
  VARCHAR: { label: 'VARCHAR', value: 'VARCHAR', type: 'TEXT', maxLength: 65535, commonly: true },
  TEXT: { label: 'TEXT', value: 'TEXT', type: 'TEXT', commonly: true },
  TIMESTAMP: { label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'DATETIME', maxLength: 6, commonly: true },
  // 布尔类型
  BOOLEAN: { label: 'BOOLEAN', value: 'BOOLEAN', type: 'BOOLEAN', commonly: true },
  JSONB: { label: 'JSONB', value: 'JSONB', type: 'JSON', commonly: true },
  // UUID类型
  UUID: { label: 'UUID', value: 'UUID', type: 'TEXT', commonly: true },
  // 整数类型
  SMALLINT: { label: 'SMALLINT', value: 'SMALLINT', type: 'INT', commonly: false },
  BIGSERIAL: { label: 'BIGSERIAL', value: 'BIGSERIAL', type: 'INT', commonly: false },
  // 浮点数类型
  REAL: { label: 'REAL', value: 'REAL', type: 'DECIMAL', commonly: false },
  FLOAT: { label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', commonly: false },
  DOUBLE_PRECISION: { label: 'DOUBLE', value: 'DOUBLE PRECISION', type: 'DECIMAL', commonly: false },
  // 精确数值类型
  NUMERIC: { label: 'NUMERIC', value: 'NUMERIC', type: 'DECIMAL', maxLength: 38, maxScale: 10, commonly: false },
  MONEY: { label: 'MONEY', value: 'MONEY', type: 'DECIMAL', commonly: false },
  // 字符串类型
  CHAR: { label: 'CHAR', value: 'CHAR', type: 'TEXT', maxLength: 255, commonly: false },
  // 二进制类型
  BYTEA: { label: 'BYTEA', value: 'BYTEA', type: 'BINARY', commonly: false },
  // 日期时间类型
  DATE: { label: 'DATE', value: 'DATE', type: 'DATETIME', commonly: false },
  TIME: { label: 'TIME', value: 'TIME', type: 'DATETIME', maxLength: 6, commonly: false },
  TIMESTAMPTZ: { label: 'TIMESTAMPTZ', value: 'TIMESTAMPTZ', type: 'DATETIME', maxLength: 6, commonly: false },
  INTERVAL: { label: 'INTERVAL', value: 'INTERVAL', type: 'DATETIME', commonly: false },
  // JSON类型
  JSON: { label: 'JSON', value: 'JSON', type: 'JSON', commonly: false },
  // 网络地址类型
  INET: { label: 'INET', value: 'INET', type: 'TEXT', commonly: false },
  CIDR: { label: 'CIDR', value: 'CIDR', type: 'TEXT', commonly: false },
  MACADDR: { label: 'MACADDR', value: 'MACADDR', type: 'TEXT', commonly: false },
  // 位串类型
  BIT: { label: 'BIT', value: 'BIT', type: 'TEXT', maxLength: 64, commonly: false },
  BIT_VARYING: { label: 'BIT_VARYING', value: 'BIT_VARYING', type: 'TEXT', maxLength: 1024, commonly: false },
  // 全文搜索类型
  TSVECTOR: { label: 'TSVECTOR', value: 'TSVECTOR', type: 'TEXT', commonly: false },
  TSQUERY: { label: 'TSQUERY', value: 'TSQUERY', type: 'TEXT', commonly: false },
  // 几何类型
  POINT: { label: 'POINT', value: 'POINT', type: 'TEXT', commonly: false },
  LINE: { label: 'LINE', value: 'LINE', type: 'TEXT', commonly: false },
  LSEG: { label: 'LSEG', value: 'LSEG', type: 'TEXT', commonly: false },
  BOX: { label: 'BOX', value: 'BOX', type: 'TEXT', commonly: false },
  PATH: { label: 'PATH', value: 'PATH', type: 'TEXT', commonly: false },
  POLYGON: { label: 'POLYGON', value: 'POLYGON', type: 'TEXT', commonly: false },
  CIRCLE: { label: 'CIRCLE', value: 'CIRCLE', type: 'TEXT', commonly: false },
  // 数组类型（示例）
  INTEGER_ARRAY: { label: 'INTEGER[]', value: 'INTEGER[]', type: 'TEXT', commonly: false },
  TEXT_ARRAY: { label: 'TEXT[]', value: 'TEXT[]', type: 'TEXT', commonly: false },
  // XML类型
  XML: { label: 'XML', value: 'XML', type: 'TEXT', commonly: false }
}

export const OracleColumnType: Record<string, ColumnTypeOption> =  {
  // 数值类型
  NUMBER: { label: 'NUMBER', value: 'NUMBER', type: 'DECIMAL', maxLength: 38, maxScale: 38, commonly: true },
  INTEGER: { label: 'INTEGER', value: 'INTEGER', type: 'INT', commonly: true },
  VARCHAR2: { label: 'VARCHAR2', value: 'VARCHAR2', type: 'TEXT', maxLength: 4000, commonly: true },
  // 日期时间类型
  DATE: { label: 'DATE', value: 'DATE', type: 'DATETIME', commonly: true },
  TIMESTAMP: { label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'DATETIME', maxLength: 9, commonly: true },
  // Oracle没有原生BOOLEAN类型，23ai之前使用NUMBER(1)模拟，0表示false，1表示true   之后增加了boolean类型
  BOOLEAN: { label: 'BOOLEAN', value: 'BOOLEAN', type: 'BOOLEAN', commonly: true },

  FLOAT: { label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', maxLength: 126, commonly: false },
  BINARY_FLOAT: { label: 'BINARY_FLOAT', value: 'BINARY_FLOAT', type: 'DECIMAL', commonly: false },
  BINARY_DOUBLE: { label: 'BINARY_DOUBLE', value: 'BINARY_DOUBLE', type: 'DECIMAL', commonly: false },
  // 字符类型
  CHAR: { label: 'CHAR', value: 'CHAR', type: 'TEXT', maxLength: 2000, commonly: false },
  NCHAR: { label: 'NCHAR', value: 'NCHAR', type: 'TEXT', maxLength: 1000, commonly: false },
  NVARCHAR2: { label: 'NVARCHAR2', value: 'NVARCHAR2', type: 'TEXT', maxLength: 2000, commonly: false },
  LONG: { label: 'LONG', value: 'LONG', type: 'TEXT', commonly: false },
  // 大对象类型
  CLOB: { label: 'CLOB', value: 'CLOB', type: 'TEXT', commonly: false },
  NCLOB: { label: 'NCLOB', value: 'NCLOB', type: 'TEXT', commonly: false },
  BLOB: { label: 'BLOB', value: 'BLOB', type: 'BINARY', commonly: false },
  BFILE: { label: 'BFILE', value: 'BFILE', type: 'BINARY', commonly: false },
  // 二进制类型
  RAW: { label: 'RAW', value: 'RAW', type: 'BINARY', maxLength: 2000, commonly: false },
  LONG_RAW: { label: 'LONG RAW', value: 'LONG RAW', type: 'BINARY', commonly: false },
  // 行标识符类型
  ROWID: { label: 'ROWID', value: 'ROWID', type: 'TEXT', commonly: false },
  UROWID: { label: 'UROWID', value: 'UROWID', type: 'TEXT', maxLength: 4000, commonly: false },
  // XML和JSON类型
  XMLTYPE: { label: 'XMLTYPE', value: 'XMLTYPE', type: 'TEXT', commonly: false },
  JSON: { label: 'JSON', value: 'JSON', type: 'JSON', commonly: false },
}

export const SqlServerColumnType: Record<string, ColumnTypeOption> = {

  INT: { label: 'INT', value: 'INT', type: 'DECIMAL', commonly: true },
  BIGINT: { label: 'BIGINT', value: 'BIGINT', type: 'DECIMAL', commonly: true },
  DECIMAL: { label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', maxLength: 38, maxScale: 38, commonly: true },
  VARCHAR: { label: 'VARCHAR', value: 'VARCHAR', type: 'TEXT', maxLength: 8000, commonly: true },
  DATETIME2: { label: 'DATETIME2', value: 'DATETIME2', type: 'DATETIME', maxLength: 7, commonly: true },
  BIT: { label: 'BIT', value: 'BIT', type: 'BOOLEAN', commonly: true },  // Boolean的true和false
  
  // 整数类型
  TINYINT: { label: 'TINYINT', value: 'TINYINT', type: 'DECIMAL', commonly: false },
  SMALLINT: { label: 'SMALLINT', value: 'SMALLINT', type: 'DECIMAL', commonly: false },
  // 精确数值类型
  NUMERIC: { label: 'NUMERIC', value: 'NUMERIC', type: 'DECIMAL', maxLength: 38, maxScale: 38, commonly: false },
  MONEY: { label: 'MONEY', value: 'MONEY', type: 'DECIMAL', commonly: false },
  SMALLMONEY: { label: 'SMALLMONEY', value: 'SMALLMONEY', type: 'DECIMAL', commonly: false },
  // 近似数值类型
  FLOAT: { label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', commonly: false },
  REAL: { label: 'REAL', value: 'REAL', type: 'DECIMAL', commonly: false },
  // 字符串类型
  CHAR: { label: 'CHAR', value: 'CHAR', type: 'TEXT', maxLength: 8000, commonly: false },
  // Unicode字符串类型
  NCHAR: { label: 'NCHAR', value: 'NCHAR', type: 'TEXT', maxLength: 4000, commonly: false },
  NVARCHAR: { label: 'NVARCHAR', value: 'NVARCHAR', type: 'TEXT', maxLength: 4000, commonly: false },
  TEXT: { label: 'TEXT', value: 'TEXT', type: 'TEXT', commonly: false },
  NTEXT: { label: 'NTEXT', value: 'NTEXT', type: 'TEXT', commonly: false },
  // 二进制类型
  BINARY: { label: 'BINARY', value: 'BINARY', type: 'BINARY', maxLength: 8000, commonly: false },
  VARBINARY: { label: 'VARBINARY', value: 'VARBINARY', type: 'BINARY', maxLength: 8000, commonly: false },
  IMAGE: { label: 'IMAGE', value: 'IMAGE', type: 'BINARY', commonly: false },
  // 日期时间类型
  DATE: { label: 'DATE', value: 'DATE', type: 'DATETIME', commonly: false },
  TIME: { label: 'TIME', value: 'TIME', type: 'DATETIME', maxLength: 7, commonly: false },
  DATETIME: { label: 'DATETIME', value: 'DATETIME', type: 'DATETIME', commonly: false },
  SMALLDATETIME: { label: 'SMALLDATETIME', value: 'SMALLDATETIME', type: 'DATETIME', commonly: false },
  DATETIMEOFFSET: { label: 'DATETIMEOFFSET', value: 'DATETIMEOFFSET', type: 'DATETIME', maxLength: 7, commonly: false },
  TIMESTAMP: { label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'BINARY', commonly: false },
  // 其他类型
  UNIQUEIDENTIFIER: { label: 'UNIQUEIDENTIFIER', value: 'UNIQUEIDENTIFIER', type: 'TEXT', commonly: false },
  XML: { label: 'XML', value: 'XML', type: 'TEXT', commonly: false },
  SQL_VARIANT: { label: 'SQL_VARIANT', value: 'SQL_VARIANT', type: 'TEXT', commonly: false },
  GEOGRAPHY: { label: 'GEOGRAPHY', value: 'GEOGRAPHY', type: 'TEXT', commonly: false },
  GEOMETRY: { label: 'GEOMETRY', value: 'GEOMETRY', type: 'TEXT', commonly: false },
  HIERARCHYID: { label: 'HIERARCHYID', value: 'HIERARCHYID', type: 'TEXT', commonly: false }
}

export const DB2ColumnType: Record<string, ColumnTypeOption> = {

  INTEGER: { label: 'INTEGER', value: 'INTEGER', type: 'INT', commonly: true },
  BIGINT: { label: 'BIGINT', value: 'BIGINT', type: 'INT', commonly: true },
  DECIMAL: { label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', maxLength: 31, maxScale: 31, commonly: true },
  BOOLEAN: { label: 'BOOLEAN', value: 'BOOLEAN', type: 'BOOLEAN', commonly: true },
  CHAR: { label: 'CHAR', value: 'CHAR', type: 'TEXT', maxLength: 254, commonly: true },
  VARCHAR: { label: 'VARCHAR', value: 'VARCHAR', type: 'TEXT', maxLength: 32672, commonly: true },
  CLOB: { label: 'CLOB', value: 'CLOB', type: 'TEXT', commonly: true },
  BLOB: { label: 'BLOB', value: 'BLOB', type: 'BINARY', commonly: true },
  TIMESTAMP: { label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'DATETIME', commonly: true },

    // 整数类型
  SMALLINT: { label: 'SMALLINT', value: 'SMALLINT', type: 'INT', commonly: false },

  // 精确数值类型
  NUMERIC: { label: 'NUMERIC', value: 'NUMERIC', type: 'DECIMAL', maxLength: 31, maxScale: 31, commonly: false },
  DECFLOAT: { label: 'DECFLOAT', value: 'DECFLOAT', type: 'DECIMAL', commonly: false },

  // 浮点类型
  REAL: { label: 'REAL', value: 'REAL', type: 'DECIMAL', commonly: false },
  DOUBLE: { label: 'DOUBLE', value: 'DOUBLE', type: 'DECIMAL', commonly: false },
  FLOAT: { label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', commonly: false },

  // 字符类型
  CHARACTER: { label: 'CHARACTER', value: 'CHARACTER', type: 'TEXT', maxLength: 254, commonly: false },
  LONG_VARCHAR: { label: 'LONG VARCHAR', value: 'LONG VARCHAR', type: 'TEXT', commonly: false },

  // 图形字符类型（双字节）
  GRAPHIC: { label: 'GRAPHIC', value: 'GRAPHIC', type: 'TEXT', maxLength: 127, commonly: false },
  VARGRAPHIC: { label: 'VARGRAPHIC', value: 'VARGRAPHIC', type: 'TEXT', maxLength: 16336, commonly: false },
  LONG_VARGRAPHIC: { label: 'LONG VARGRAPHIC', value: 'LONG VARGRAPHIC', type: 'TEXT', commonly: false },
  DBCLOB: { label: 'DBCLOB', value: 'DBCLOB', type: 'TEXT', commonly: false },

  // 二进制类型
  CHAR_FOR_BIT_DATA: { label: 'CHAR FOR BIT DATA', value: 'CHAR FOR BIT DATA', type: 'BINARY', maxLength: 254, commonly: false },
  VARCHAR_FOR_BIT_DATA: { label: 'VARCHAR FOR BIT DATA', value: 'VARCHAR FOR BIT DATA', type: 'BINARY', maxLength: 32672, commonly: false },
  LONG_VARCHAR_FOR_BIT_DATA: { label: 'LONG VARCHAR FOR BIT DATA', value: 'LONG VARCHAR FOR BIT DATA', type: 'BINARY', commonly: false },
  
  // 日期时间类型
  DATE: { label: 'DATE', value: 'DATE', type: 'DATETIME', commonly: false },
  TIME: { label: 'TIME', value: 'TIME', type: 'DATETIME', commonly: false },

  // 特殊类型
  XML: { label: 'XML', value: 'XML', type: 'TEXT', commonly: false },
  ROWID: { label: 'ROWID', value: 'ROWID', type: 'TEXT', commonly: false }
}

export const CubridColumnType: Record<string, ColumnTypeOption> = {
  // 常用类型
  INT: { label: 'INT', value: 'INT', type: 'INT', commonly: true },
  BIGINT: { label: 'BIGINT', value: 'BIGINT', type: 'INT', commonly: true },
  DECIMAL: { label: 'DECIMAL', value: 'DECIMAL', type: 'DECIMAL', maxLength: 38, maxScale: 38, commonly: true },
  VARCHAR: { label: 'VARCHAR', value: 'VARCHAR', type: 'TEXT', maxLength: 4000, commonly: true },
  CHAR: { label: 'CHAR', value: 'CHAR', type: 'TEXT', maxLength: 255, commonly: true },
  DATETIME: { label: 'DATETIME', value: 'DATETIME', type: 'DATETIME', commonly: true },
  DATE: { label: 'DATE', value: 'DATE', type: 'DATETIME', commonly: true },
  TIME: { label: 'TIME', value: 'TIME', type: 'DATETIME', commonly: true },
  TIMESTAMP: { label: 'TIMESTAMP', value: 'TIMESTAMP', type: 'DATETIME', commonly: true },
  
  // 其他类型
  SMALLINT: { label: 'SMALLINT', value: 'SMALLINT', type: 'INT', commonly: false },
  NUMERIC: { label: 'NUMERIC', value: 'NUMERIC', type: 'DECIMAL', maxLength: 38, maxScale: 38, commonly: false },
  FLOAT: { label: 'FLOAT', value: 'FLOAT', type: 'DECIMAL', commonly: false },
  DOUBLE: { label: 'DOUBLE', value: 'DOUBLE', type: 'DECIMAL', commonly: false },
  MONETARY: { label: 'MONETARY', value: 'MONETARY', type: 'DECIMAL', commonly: false },
  BIT: { label: 'BIT', value: 'BIT', type: 'TEXT', maxLength: 8000, commonly: false },
  BIT_VARYING: { label: 'BIT VARYING', value: 'BIT VARYING', type: 'TEXT', maxLength: 8000, commonly: false },
  NCHAR: { label: 'NCHAR', value: 'NCHAR', type: 'TEXT', maxLength: 255, commonly: false },
  NCHAR_VARYING: { label: 'NCHAR VARYING', value: 'NCHAR VARYING', type: 'TEXT', maxLength: 4000, commonly: false },
  STRING: { label: 'STRING', value: 'STRING', type: 'TEXT', commonly: false },
  CLOB: { label: 'CLOB', value: 'CLOB', type: 'TEXT', commonly: false },
  BLOB: { label: 'BLOB', value: 'BLOB', type: 'BINARY', commonly: false },
  SET: { label: 'SET', value: 'SET', type: 'SET', commonly: false },
  MULTISET: { label: 'MULTISET', value: 'MULTISET', type: 'SET', commonly: false },
  SEQUENCE: { label: 'SEQUENCE', value: 'SEQUENCE', type: 'SET', commonly: false },
  ENUM: { label: 'ENUM', value: 'ENUM', type: 'ENUM', commonly: false },
  JSON: { label: 'JSON', value: 'JSON', type: 'JSON', commonly: false }
}