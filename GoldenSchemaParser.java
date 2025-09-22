package com.regnosys.cdm.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;

/**
 * GoldenSchemaParser converts JSON strings to Java Map objects.
 * 
 * This parser provides a simple interface for converting Golden Schema
 * JSON data into Map<String, Object> for further processing by CDM components.
 */
public class GoldenSchemaParser {
    
    private final ObjectMapper objectMapper;
    
    /**
     * Creates a new GoldenSchemaParser instance.
     */
    public GoldenSchemaParser() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Parses a JSON string into a Map.
     * 
     * @param jsonString the JSON string to parse
     * @return Map containing the parsed JSON data
     * @throws IllegalArgumentException if jsonString is null or empty
     * @throws RuntimeException if JSON parsing fails
     */
    public Map<String, Object> parse(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }
        
        try {
            return objectMapper.readValue(jsonString, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON: " + e.getMessage(), e);
        }
    }
}
