package com.demo.jsoncomparator.util;

import com.demo.jsoncomparator.dto.DiffResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.demo.jsoncomparator.dto.DiffResponse.Status.DIFFERENT_CONTENT;
import static com.demo.jsoncomparator.dto.DiffResponse.Status.EQUALS;
import static com.demo.jsoncomparator.dto.DiffResponse.Status.NOT_EQUAL_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Execute test cases for DiffUtil
 *
 * @author Richard Guzman
 * @version v1, March 2021
 */
public class DiffUtilTest {

    public static List<Arguments> toBase64Data() {
        return Arrays.asList(
                Arguments.of(DiffResponse.builder()
                                .status(EQUALS)
                                .message(EQUALS.getDescription()).build(),
                        "eyJzdGF0dXMiOiJFUVVBTFMiLCJtZXNzYWdlIjoiQm90aCBmaWxlcyBhcmUgZXF1YWxzIn0="
                ),
                Arguments.of(DiffResponse.builder()
                                .status(NOT_EQUAL_SIZE)
                                .message(NOT_EQUAL_SIZE.getDescription()).build(),
                        "eyJzdGF0dXMiOiJOT1RfRVFVQUxfU0laRSIsIm1lc3NhZ2UiOiJGaWxlcyBoYXZlIG5vdCBlcXVhbCBzaXplIn0="
                ),
                Arguments.of(DiffResponse.builder()
                                .status(DIFFERENT_CONTENT)
                                .message(DIFFERENT_CONTENT.getDescription())
                                .offsetList(Collections.singletonList(DiffResponse.DiffOffset.builder().offset(21L).length(21L).build()))
                                .build(),
                        "eyJzdGF0dXMiOiJESUZGRVJFTlRfQ09OVEVOVCIsIm1lc3NhZ2UiOiJGaWxlcyBoYXZlIGRpZmZlcmVudCBjb250ZW50Iiwib2Zmc2V0TGlzdCI6W3sib2Zmc2V0IjoyMSwibGVuZ3RoIjoyMX1dfQ=="
                )
        );
    }

    @ParameterizedTest
    @CsvSource({
            "ewogICAgIm5hbWUiOiAiam9zZSIsCiAgICAibGFzdG5hbWUiOiAiYWEiCn0=,true",
            "ewogICAgIm5hbWUiOiAiam9zZSIsCiAgICAibGFzdG5hbWUiOiBhYQp9,false"
    })
    public void isBase64EncodedJsonTest(String content, Boolean expected) {
        assertEquals(expected, DiffUtil.isBase64EncodedJson(content));
    }

    @ParameterizedTest
    @CsvSource({"eyJuYW1lIjogImpvc2UifQ==,{\"name\": \"jose\"}"})
    public void getJsonTest(String content, String expected) {
        assertEquals(expected, DiffUtil.getJson(content));
    }

    @ParameterizedTest
    @MethodSource("toBase64Data")
    public void toBase64Test(DiffResponse content, String expected) {
        assertEquals(expected, DiffUtil.toBase64(content));
    }

    @ParameterizedTest
    @MethodSource("toBase64Data")
    public void toDiffResponseTest(DiffResponse expected, String content) {
        DiffResponse result = DiffUtil.toDiffResponse(content);
        assertNotNull(result);
        assertEquals(expected.getStatus(), result.getStatus());
        assertEquals(expected.getMessage(), result.getMessage());
        if (expected.getOffsetList() != null) {
            assertNotNull(result.getOffsetList());
            assertEquals(expected.getOffsetList().size(), result.getOffsetList().size());
        } else {
            assertNull(result.getOffsetList());
        }
    }

}
