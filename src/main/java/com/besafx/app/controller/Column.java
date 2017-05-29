package com.besafx.app.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Column {
    private String name;
    private boolean view;
    private boolean groupBy;
    private boolean sortBy;

    @JsonCreator
    public static Column Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Column column = mapper.readValue(jsonString, Column.class);
        return column;
    }
}
