package com.regnosys.cdm.example.exceptions;

/**
 * Exception thrown when EconomicTerms mapping fails.
 * 
 * This exception provides specific error categorization for EconomicTerms
 * object construction issues in the CDM processing pipeline.
 */
public class EconomicTermsMappingException extends CdmProcessingException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new EconomicTermsMappingException with a message.
     * 
     * @param message the error message
     */
    public EconomicTermsMappingException(String message) {
        super(message);
    }
    
    /**
     * Creates a new EconomicTermsMappingException with a message and cause.
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public EconomicTermsMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
