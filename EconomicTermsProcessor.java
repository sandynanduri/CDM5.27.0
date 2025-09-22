package com.regnosys.cdm.example;

import cdm.base.datetime.AdjustableDate;
import cdm.base.datetime.AdjustableOrRelativeDate;
import cdm.base.datetime.BusinessDayAdjustments;
import cdm.base.datetime.BusinessDayConventionEnum;
import cdm.base.datetime.BusinessCenters;
import cdm.base.datetime.BusinessCenterEnum;
import cdm.base.datetime.metafields.FieldWithMetaBusinessCenterEnum;
import cdm.product.template.EconomicTerms;
import com.rosetta.model.lib.records.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * EconomicTermsProcessor processes economic terms data for CDM objects.
 * 
 * This processor converts Golden Schema Map data into complete CDM EconomicTerms
 * objects, handling all economic terms fields including dates, payouts, and other terms.
 */
public class EconomicTermsProcessor {
    
    /**
     * Builds complete CDM EconomicTerms from Golden Schema Map data.
     * 
     * This is the main entry point that constructs the full EconomicTerms object
     * by processing all relevant Golden Schema fields and building the nested CDM structure.
     * 
     * @param goldenData the parsed Golden Schema data as Map
     * @return EconomicTerms representing the complete economic terms, or null if no data provided
     * @throws RuntimeException if CDM object construction fails
     */
    public EconomicTerms buildEconomicTerms(Map<String, Object> goldenData) {
        try {
            // Build effective date
            AdjustableOrRelativeDate effectiveDate = buildEffectiveDate(goldenData);
            
            // Build termination date
            AdjustableOrRelativeDate terminationDate = buildTerminationDate(goldenData);
            
            // If no dates provided, return null (empty economic terms)
            if (effectiveDate == null && terminationDate == null) {
                return null;
            }
            
            // Build the complete EconomicTerms object
            EconomicTerms economicTerms = EconomicTerms.builder()
                .setEffectiveDate(effectiveDate)
                .setTerminationDate(terminationDate)
                // TODO: Add other economic terms fields as needed:
                // .setPayout(buildPayout(goldenData))
                // .setCalculationAgent(buildCalculationAgent(goldenData))
                .build();
            
            return economicTerms;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to build CDM EconomicTerms: " + e.getMessage(), e);
        }
    }
    
    /**
     * Builds CDM AdjustableOrRelativeDate from Golden Schema Map data.
     * 
     * Processes the following Golden Schema keys:
     * - effectiveDate: The unadjusted date (ISO format: YYYY-MM-DD)
     * - effectiveDateBusinessDayConvention: Business day convention (e.g., FOLLOWING)
     * - effectiveDateBusinessCenter: Business center (e.g., SGSI)
     * 
     * @param goldenData the parsed Golden Schema data as Map
     * @return AdjustableOrRelativeDate representing the effective date, or null if not provided
     * @throws RuntimeException if CDM object construction fails
     */
    private AdjustableOrRelativeDate buildEffectiveDate(Map<String, Object> goldenData) {
        try {
            // Extract data from Golden Schema Map
            String effectiveDateStr = (String) goldenData.get("effectiveDate");
            String businessDayConvention = (String) goldenData.get("effectiveDateBusinessDayConvention");
            String businessCenter = (String) goldenData.get("effectiveDateBusinessCenter");
            
            // Check if effectiveDate data is provided (all optional in CDM)
            if (effectiveDateStr == null || effectiveDateStr.trim().isEmpty()) {
                // No effective date provided - return null (optional field)
                return null;
            }
            
            // Parse the date from ISO format (YYYY-MM-DD)
            LocalDate parsedDate = LocalDate.parse(effectiveDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            Date cdmDate = Date.of(parsedDate.getYear(), parsedDate.getMonthValue(), parsedDate.getDayOfMonth());
            
            // Convert business day convention string to enum
            BusinessDayConventionEnum conventionEnum = BusinessDayConventionEnum.valueOf(businessDayConvention);
            
            // Convert business center string to enum
            BusinessCenterEnum centerEnum = BusinessCenterEnum.valueOf(businessCenter);
            
            // Build FieldWithMetaBusinessCenterEnum for the business center
            FieldWithMetaBusinessCenterEnum fieldWithMetaBusinessCenter = FieldWithMetaBusinessCenterEnum.builder()
                .setValue(centerEnum)
                .build();
            
            // Build BusinessCenters
            BusinessCenters businessCenters = BusinessCenters.builder()
                .addBusinessCenter(fieldWithMetaBusinessCenter)
                .build();
            
            // Build BusinessDayAdjustments
            BusinessDayAdjustments businessDayAdjustments = BusinessDayAdjustments.builder()
                .setBusinessDayConvention(conventionEnum)
                .setBusinessCenters(businessCenters)
                .build();
            
            // Build AdjustableDate
            AdjustableDate adjustableDate = AdjustableDate.builder()
                .setUnadjustedDate(cdmDate)
                .setDateAdjustments(businessDayAdjustments)
                .build();
            
            // Build AdjustableOrRelativeDate
            AdjustableOrRelativeDate effectiveDate = AdjustableOrRelativeDate.builder()
                .setAdjustableDate(adjustableDate)
                .build();
            
            return effectiveDate;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to build CDM effectiveDate: " + e.getMessage(), e);
        }
    }
    
    /**
     * Builds CDM AdjustableOrRelativeDate for termination date from Golden Schema Map data.
     * 
     * Processes the following Golden Schema keys:
     * - terminationDate: The unadjusted date (ISO format: YYYY-MM-DD)
     * - terminationDateBusinessDayConvention: Business day convention (e.g., FOLLOWING)
     * - terminationDateBusinessCenter: Business center (e.g., SGSI)
     * 
     * @param goldenData the parsed Golden Schema data as Map
     * @return AdjustableOrRelativeDate representing the termination date, or null if not provided
     * @throws RuntimeException if CDM object construction fails
     */
    private AdjustableOrRelativeDate buildTerminationDate(Map<String, Object> goldenData) {
        try {
            // Extract data from Golden Schema Map
            String terminationDateStr = (String) goldenData.get("terminationDate");
            String businessDayConvention = (String) goldenData.get("terminationDateBusinessDayConvention");
            String businessCenter = (String) goldenData.get("terminationDateBusinessCenter");
            
            // Check if terminationDate data is provided (all optional in CDM)
            if (terminationDateStr == null || terminationDateStr.trim().isEmpty()) {
                // No termination date provided - return null (optional field)
                return null;
            }
            
            // Parse the date from ISO format (YYYY-MM-DD)
            LocalDate parsedDate = LocalDate.parse(terminationDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            Date cdmDate = Date.of(parsedDate.getYear(), parsedDate.getMonthValue(), parsedDate.getDayOfMonth());
            
            // Convert business day convention string to enum
            BusinessDayConventionEnum conventionEnum = BusinessDayConventionEnum.valueOf(businessDayConvention);
            
            // Convert business center string to enum
            BusinessCenterEnum centerEnum = BusinessCenterEnum.valueOf(businessCenter);
            
            // Build FieldWithMetaBusinessCenterEnum for the business center
            FieldWithMetaBusinessCenterEnum fieldWithMetaBusinessCenter = FieldWithMetaBusinessCenterEnum.builder()
                .setValue(centerEnum)
                .build();
            
            // Build BusinessCenters
            BusinessCenters businessCenters = BusinessCenters.builder()
                .addBusinessCenter(fieldWithMetaBusinessCenter)
                .build();
            
            // Build BusinessDayAdjustments
            BusinessDayAdjustments businessDayAdjustments = BusinessDayAdjustments.builder()
                .setBusinessDayConvention(conventionEnum)
                .setBusinessCenters(businessCenters)
                .build();
            
            // Build AdjustableDate
            AdjustableDate adjustableDate = AdjustableDate.builder()
                .setUnadjustedDate(cdmDate)
                .setDateAdjustments(businessDayAdjustments)
                .build();
            
            // Build AdjustableOrRelativeDate
            AdjustableOrRelativeDate terminationDate = AdjustableOrRelativeDate.builder()
                .setAdjustableDate(adjustableDate)
                .build();
            
            return terminationDate;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to build CDM terminationDate: " + e.getMessage(), e);
        }
    }
}