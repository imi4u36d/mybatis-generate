package org.miaomiao.generate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColumnInfo {

    private String columnName;

    private String columnType;

    private String javaName;

    private String javaType;

    private String columnComment;

}
