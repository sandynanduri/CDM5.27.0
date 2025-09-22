package com.regnosys.cdm.example.exceptions;

/**
 * Base exception for CDM processing errors.
 * 
 * This exception provides a common base for all CDM processing-related exceptions
 * with proper error categorization and context.
 */
public class CdmProcessingException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new CdmProcessingException with a message.
     * 
     * @param message the error message
     */
    public CdmProcessingException(String message) {
        super(message);
    }
    
    /**
     * Creates a new CdmProcessingException with a message and cause.
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public CdmProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
