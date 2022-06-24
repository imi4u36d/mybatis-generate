package org.miaomiao.generate.model;

import java.util.Arrays;
import java.util.List;

public enum ColumnType {

    INT("Integer", ""),
    BIGINT("Long", ""),
    VARCHAR("String", ""),
    TEXT("String", ""),
    MEDIUMTEXT("String", ""),
    BINARY("byte[]", ""),
    LONGTEXT("String", ""),
    DATETIME("LocalDateTime", "java.time.LocalDateTime"),
    DATE("Date", "java.sql.Date"),
    TIME("TIME", "java.sql.Time"),
    TIMESTAMP("Timestamp", "java.sql.Timestamp"),
    FLOAT("Float", ""),
    BIT("Integer", "");


    private final String fieldType;

    private final String packageName;

    ColumnType(String fieldType, String packageName) {
        this.fieldType = fieldType;
        this.packageName = packageName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getPackageName() {
        return packageName;
    }

    public static List<ColumnType> getColumnTypeList() {
        return Arrays.asList(ColumnType.values());
    }
}
