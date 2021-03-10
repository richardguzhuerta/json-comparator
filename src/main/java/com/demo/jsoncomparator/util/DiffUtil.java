package com.demo.jsoncomparator.util;

import com.demo.jsoncomparator.dto.DiffResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;


/**
 * Application utility class containing common functions
 *
 * @author Richard Guzman
 * @version v1, March 2021
 */
@Slf4j
public class DiffUtil {
    /**
     * Validate if a given input is base64 encoded
     *
     * @param content data to verify encoding
     * @return true if base64 encoded,false on the contrary.
     */
    public static boolean isBase64(final String content) {
        return StringUtils.isNotEmpty(content) && Base64.isBase64(content);
    }

    /**
     * Verify if a given input is a valid Base64 encoded Json
     *
     * @param content data to verify if represents a valid Json
     * @return true if the given data is a valid Json
     */
    public static boolean isBase64EncodedJson(final String content) {
        boolean isValid = false;
        if (isBase64(content)) {
            String decodedString = new String(Base64.decodeBase64(content.getBytes()));
            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.readTree(decodedString);
                isValid = true;
            } catch (JsonProcessingException e) {
                log.error("Unable to parse given string to json: " + e.getMessage());
            }
        }
        return isValid;
    }

    /**
     * Extract Json content from a Base64 encoded data
     *
     * @param content Base64 encoded data
     * @return Json content
     */
    public static String getJson(final String content) {
        return isBase64EncodedJson(content) ? new String(Base64.decodeBase64(content.getBytes())) : null;
    }

    /**
     * Encode diffResponse to base64
     *
     * @param diffResponse object containing diff result including details of offset and length for differences
     * @return the base64 representation
     */
    public static String toBase64(DiffResponse diffResponse) {
        ObjectMapper mapper = new ObjectMapper();
        String result = null;
        if (diffResponse != null) {
            try {
                result = new String(Base64.encodeBase64(mapper.writeValueAsString(diffResponse).getBytes()));
            } catch (Exception e) {
                log.error("Unable to encode diffResponse to base64: " + e.getMessage());
            }
        }
        return result;
    }

    /**
     * Decode base64 to DiffResponse
     *
     * @param base64 the base64 representation of DiffResponse
     * @return object containing diff result including details of offset and length for differences
     */
    public static DiffResponse toDiffResponse(String base64) {
        ObjectMapper mapper = new ObjectMapper();
        DiffResponse diffResponse = null;
        if (base64 != null) {
            try {
                diffResponse = mapper.readValue(getJson(base64), DiffResponse.class);

            } catch (Exception e) {
                diffResponse = DiffResponse.builder().message("Unable to get the results").build();
            }
        }
        return diffResponse;
    }
}
