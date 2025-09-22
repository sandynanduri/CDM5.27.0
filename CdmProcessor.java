package com.regnosys.cdm.example;

import cdm.product.template.EconomicTerms;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.regnosys.cdm.example.exceptions.ConfigurationException;
import com.regnosys.cdm.example.exceptions.EconomicTermsMappingException;
import com.regnosys.cdm.example.exceptions.GoldenSchemaParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

/**
 * Enterprise-grade CdmProcessor orchestrates the conversion of Golden Schema data to CDM objects.
 * 
 * This processor coordinates the parsing of Golden Schema JSON and passes
 * the result to appropriate CDM processing modules with proper logging,
 * error handling, and observability.
 */
public class CdmProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(CdmProcessor.class);
    
    private final GoldenSchemaParser parser;
    private final EconomicTermsProcessor economicTermsProcessor;
    private final ObjectMapper jsonMapper;
    private final String correlationId;
    
    /**
     * Creates a new CdmProcessor instance with correlation ID for tracing.
     */
    public CdmProcessor() {
        this.correlationId = UUID.randomUUID().toString();
        this.parser = new GoldenSchemaParser();
        this.economicTermsProcessor = new EconomicTermsProcessor();
        this.jsonMapper = createObjectMapper();
        
        // Set correlation ID in MDC for logging
        MDC.put("correlationId", correlationId);
        logger.info("CdmProcessor initialized with correlationId: {}", correlationId);
    }
    
    /**
     * Creates a new CdmProcessor instance with specific correlation ID.
     * 
     * @param correlationId the correlation ID for request tracing
     */
    public CdmProcessor(String correlationId) {
        this.correlationId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        this.parser = new GoldenSchemaParser();
        this.economicTermsProcessor = new EconomicTermsProcessor();
        this.jsonMapper = createObjectMapper();
        
        // Set correlation ID in MDC for logging
        MDC.put("correlationId", this.correlationId);
        logger.info("CdmProcessor initialized with correlationId: {}", this.correlationId);
    }
    
    /**
     * Creates a configured ObjectMapper instance.
     * 
     * @return configured ObjectMapper
     */
    private ObjectMapper createObjectMapper() {
        return new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
    
    /**
     * Processes Golden Schema JSON through the CDM pipeline with enterprise-grade error handling.
     * 
     * @param goldenSchemaJson the Golden Schema JSON string to process
     * @return EconomicTerms representing the complete economic terms, or null if not provided
     * @throws GoldenSchemaParseException if JSON parsing fails
     * @throws EconomicTermsMappingException if EconomicTerms construction fails
     * @throws IllegalArgumentException if goldenSchemaJson is null or empty
     */
    public EconomicTerms process(String goldenSchemaJson) throws GoldenSchemaParseException, EconomicTermsMappingException {
        logger.debug("Starting Golden Schema processing for correlationId: {}", correlationId);
        
        if (goldenSchemaJson == null || goldenSchemaJson.trim().isEmpty()) {
            logger.error("Golden Schema JSON is null or empty for correlationId: {}", correlationId);
            throw new IllegalArgumentException("Golden Schema JSON cannot be null or empty");
        }
        
        try {
            // Step 1: Parse Golden Schema JSON to Map
            logger.debug("Parsing Golden Schema JSON for correlationId: {}", correlationId);
            Map<String, Object> parsedData = parser.parse(goldenSchemaJson);
            logger.debug("Successfully parsed Golden Schema JSON for correlationId: {}", correlationId);
            
            // Step 2: Build complete CDM EconomicTerms from parsed data
            logger.debug("Building EconomicTerms from parsed data for correlationId: {}", correlationId);
            EconomicTerms economicTerms = economicTermsProcessor.buildEconomicTerms(parsedData);
            logger.info("Successfully built EconomicTerms for correlationId: {}", correlationId);
            
            return economicTerms;
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Golden Schema JSON for correlationId: {} - {}", correlationId, e.getMessage());
            throw new GoldenSchemaParseException("Invalid Golden Schema JSON: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Failed to process Golden Schema for correlationId: {} - {}", correlationId, e.getMessage(), e);
            throw new EconomicTermsMappingException("Failed to build EconomicTerms: " + e.getMessage(), e);
        }
    }
    
    /**
     * Processes Golden Schema JSON from a file path with streaming for scalability.
     * 
     * @param filePath the path to the Golden Schema JSON file
     * @return EconomicTerms representing the complete economic terms, or null if not provided
     * @throws ConfigurationException if file cannot be read
     * @throws GoldenSchemaParseException if JSON parsing fails
     * @throws EconomicTermsMappingException if EconomicTerms construction fails
     */
    public EconomicTerms processFromFile(String filePath) throws ConfigurationException, GoldenSchemaParseException, EconomicTermsMappingException {
        logger.info("Processing Golden Schema from file: {} for correlationId: {}", filePath, correlationId);
        
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new ConfigurationException("Golden Schema file does not exist: " + filePath);
            }
            
            // Use streaming for large files
            try (InputStream inputStream = Files.newInputStream(path)) {
                String goldenSchemaJson = new String(inputStream.readAllBytes());
                return process(goldenSchemaJson);
            }
            
        } catch (IOException e) {
            logger.error("Failed to read Golden Schema file: {} for correlationId: {} - {}", filePath, correlationId, e.getMessage());
            throw new ConfigurationException("Failed to read Golden Schema file: " + filePath, e);
        }
    }
    
    /**
     * Processes Golden Schema JSON from classpath resource with streaming.
     * 
     * @param resourcePath the classpath resource path to the Golden Schema JSON
     * @return EconomicTerms representing the complete economic terms, or null if not provided
     * @throws ConfigurationException if resource cannot be read
     * @throws GoldenSchemaParseException if JSON parsing fails
     * @throws EconomicTermsMappingException if EconomicTerms construction fails
     */
    public EconomicTerms processFromResource(String resourcePath) throws ConfigurationException, GoldenSchemaParseException, EconomicTermsMappingException {
        logger.info("Processing Golden Schema from classpath resource: {} for correlationId: {}", resourcePath, correlationId);
        
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new ConfigurationException("Golden Schema resource not found: " + resourcePath);
            }
            
            String goldenSchemaJson = new String(inputStream.readAllBytes());
            return process(goldenSchemaJson);
            
        } catch (IOException e) {
            logger.error("Failed to read Golden Schema resource: {} for correlationId: {} - {}", resourcePath, correlationId, e.getMessage());
            throw new ConfigurationException("Failed to read Golden Schema resource: " + resourcePath, e);
        }
    }
    
    /**
     * Main method to run CdmProcessor with enterprise-grade configuration and error handling.
     * 
     * Usage: java CdmProcessor [file-path|resource-path]
     * - file-path: Path to Golden Schema JSON file (e.g., /path/to/golden-schema.json)
     * - resource-path: Classpath resource path (e.g., golden-schema.json)
     * - Default: Uses classpath resource 'golden-schema.json'
     */
    public static void main(String[] args) {
        Logger mainLogger = LoggerFactory.getLogger("CdmProcessor.main");
        mainLogger.info("Starting enterprise-grade CdmProcessor...");
        
        try {
            // Determine input source from command line arguments
            String inputSource = determineInputSource(args);
            mainLogger.info("Using input source: {}", inputSource);
            
            CdmProcessor processor = new CdmProcessor();
            
            // Process based on input source
            EconomicTerms economicTerms = processInput(processor, inputSource, mainLogger);
            
            // Display results with proper logging
            displayResults(economicTerms, processor, mainLogger);
            
            mainLogger.info("CdmProcessor completed successfully!");
            
        } catch (Exception e) {
            mainLogger.error("CdmProcessor failed with error: {}", e.getMessage(), e);
            System.exit(1);
        } finally {
            // Clean up MDC
            MDC.clear();
        }
    }
    
    /**
     * Determines the input source from command line arguments.
     * 
     * @param args command line arguments
     * @return input source path
     */
    private static String determineInputSource(String[] args) {
        if (args.length > 0) {
            return args[0];
        }
        // Default to classpath resource
        return "golden-schema.json";
    }
    
    /**
     * Processes input based on the source type.
     * 
     * @param processor the CdmProcessor instance
     * @param inputSource the input source path
     * @param logger the logger instance
     * @return processed EconomicTerms
     * @throws Exception if processing fails
     */
    private static EconomicTerms processInput(CdmProcessor processor, String inputSource, Logger logger) throws Exception {
        try {
            // Try as file path first
            if (Files.exists(Paths.get(inputSource))) {
                logger.info("Processing from file: {}", inputSource);
                return processor.processFromFile(inputSource);
            } else {
                // Try as classpath resource
                logger.info("Processing from classpath resource: {}", inputSource);
                return processor.processFromResource(inputSource);
            }
        } catch (ConfigurationException | GoldenSchemaParseException | EconomicTermsMappingException e) {
            logger.error("Processing failed: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Displays the processing results with proper logging.
     * 
     * @param economicTerms the processed EconomicTerms
     * @param processor the CdmProcessor instance
     * @param logger the logger instance
     */
    private static void displayResults(EconomicTerms economicTerms, CdmProcessor processor, Logger logger) {
        logger.info("CDM EconomicTerms Object:");
        if (economicTerms != null) {
            logger.info("EconomicTerms created successfully");
            logger.info("CDM Object Type: {}", economicTerms.getClass().getSimpleName());
            
            logger.info("Complete CDM EconomicTerms JSON:");
            try {
                String economicTermsJson = processor.jsonMapper.writeValueAsString(economicTerms);
                System.out.println(economicTermsJson);
            } catch (Exception e) {
                logger.error("Error serializing to JSON: {}", e.getMessage());
                System.out.println("Fallback toString(): " + economicTerms.toString());
            }
        } else {
            logger.warn("No economic terms data provided in Golden Schema");
        }
    }
    
    /**
     * Gets the correlation ID for this processor instance.
     * 
     * @return the correlation ID
     */
    public String getCorrelationId() {
        return correlationId;
    }
    
    /**
     * Cleans up resources and clears MDC.
     */
    public void cleanup() {
        MDC.clear();
        logger.debug("CdmProcessor cleanup completed for correlationId: {}", correlationId);
    }
}
