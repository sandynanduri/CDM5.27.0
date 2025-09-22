package com.regnosys.cdm.example.exceptions;

/**
 * Exception thrown when Golden Schema JSON parsing fails.
 * 
 * This exception provides specific error categorization for JSON parsing issues
 * in the CDM processing pipeline.
 */
public class GoldenSchemaParseException extends CdmProcessingException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new GoldenSchemaParseException with a message.
     * 
     * @param message the error message
     */
    public GoldenSchemaParseException(String message) {
        super(message);
    }
    
    /**
     * Creates a new GoldenSchemaParseException with a message and cause.
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public GoldenSchemaParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
