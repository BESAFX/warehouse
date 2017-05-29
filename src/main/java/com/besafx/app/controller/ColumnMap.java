package com.besafx.app.controller;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ColumnMap {
    private String columnName;
    private String columnExpression;
    private String columnValue;
    private String columnNestedValue;
    private String columnValueClassName;
    private boolean view;
    private boolean groupBy;
    private boolean sortBy;
}
