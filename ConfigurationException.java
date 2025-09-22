package com.regnosys.cdm.example.exceptions;

/**
 * Exception thrown when configuration issues occur.
 * 
 * This exception provides specific error categorization for configuration
 * and file path issues in the CDM processing pipeline.
 */
public class ConfigurationException extends CdmProcessingException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new ConfigurationException with a message.
     * 
     * @param message the error message
     */
    public ConfigurationException(String message) {
        super(message);
    }
    
    /**
     * Creates a new ConfigurationException with a message and cause.
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
