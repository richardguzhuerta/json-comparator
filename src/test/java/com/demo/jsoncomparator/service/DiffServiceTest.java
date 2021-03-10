package com.demo.jsoncomparator.service;

import com.demo.jsoncomparator.dao.DiffRecordRepository;
import com.demo.jsoncomparator.dto.DiffResponse;
import com.demo.jsoncomparator.entity.DiffRecord;
import com.demo.jsoncomparator.exception.MalFormedJsonException;
import com.demo.jsoncomparator.exception.NoDataFoundException;
import com.demo.jsoncomparator.util.TestDataLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Execute test cases for DiffServiceTest
 *
 * @author Richard Guzman
 * @version v1, March 2021
 */
@ExtendWith(MockitoExtension.class)
public class DiffServiceTest {
    @InjectMocks
    DiffJsonServiceImpl diffJsonService;
    @Mock
    private DiffRecordRepository diffRecordRepository;

    private static List<Arguments> loadData() {
        return TestDataLoader.loadData();
    }

    @Test
    void saveDiffRecordDataNewRecordTest() {
        String id = "001";
        String file = "ewogICAgIm5hbWUiOiAiam9zZSIKfQ==";
        when(diffRecordRepository.findById(id)).thenReturn(Optional.empty());
        when(diffRecordRepository.save(any())).thenReturn(DiffRecord.builder().build());
        diffJsonService.saveDiffRecordData(id, file, true);
        verify(diffRecordRepository, times(1)).save(any());
    }

    @Test
    void saveDiffRecordDataExistentLeftDataTest() {
        String id = "001";
        String file = "ewogICAgIm5hbWUiOiAiam9zZSIKfQ==";
        DiffRecord diffRecord = DiffRecord.builder().requestId(id).leftFile(file).build();
        when(diffRecordRepository.findById(id)).thenReturn(Optional.of(diffRecord));
        when(diffRecordRepository.save(any())).thenReturn(diffRecord);
        diffJsonService.saveDiffRecordData(id, file, true);
        verify(diffRecordRepository, times(1)).save(any());
        assertNull(diffRecord.getComparisonResult(), "Expected null as this is a new record");
    }

    @Test
    void saveDiffRecordDataExistentRightDataTest() {
        String id = "001";
        String file = "ewogICAgIm5hbWUiOiAiam9zZSIKfQ==";
        String resultEquals = "eyJzdGF0dXMiOiJFUVVBTFMiLCJtZXNzYWdlIjoiQm90aCBmaWxlcyBhcmUgZXF1YWxzIn0=";
        DiffRecord diffRecord = DiffRecord.builder().requestId(id).rightFile(file).build();
        when(diffRecordRepository.findById(id)).thenReturn(Optional.of(diffRecord));
        when(diffRecordRepository.save(any())).thenReturn(diffRecord);
        diffJsonService.saveDiffRecordData(id, file, true);
        verify(diffRecordRepository, times(1)).save(any());
        assertNotNull(diffRecord.getComparisonResult());
        assertEquals(resultEquals, diffRecord.getComparisonResult(), "Value expected when left and right exist");
    }

    @Test
    void saveDiffRecordDataExistentRightDifferentContentDataTest() {
        String id = "001";
        String fileRight = "ewogICAgIm5hbWUiOiAicGVwZSIKfQ==";
        String fileLeft = "ewogICAgIm5hbWUiOiAiam9zZSIKfQ==";
        String result = "eyJzdGF0dXMiOiJESUZGRVJFTlRfQ09OVEVOVCIsIm1lc3NhZ2UiOiJGaWxlcyBoYXZlIGRpZmZlcmVudCBjb250ZW50Iiwib2Zmc2V0TGlzdCI6W3sib2Zmc2V0IjoxNSwibGVuZ3RoIjozfV19";
        DiffRecord diffRecord = DiffRecord.builder().requestId(id).rightFile(fileRight).build();
        when(diffRecordRepository.findById(id)).thenReturn(Optional.of(diffRecord));
        when(diffRecordRepository.save(any())).thenReturn(diffRecord);
        diffJsonService.saveDiffRecordData(id, fileLeft, true);
        verify(diffRecordRepository, times(1)).save(any());
        assertNotNull(diffRecord.getComparisonResult(), "Value expected when left and right exist");
        assertEquals(result, diffRecord.getComparisonResult());
    }

    @Test
    void saveDiffRecordDataExistentRightDifferentSizeDataTest() {
        String id = "001";
        String fileRight = "ewogICAgIm5hbWUiOiAicGVwZSIKfQ==";
        String fileLeft = "ewogICAgIm5hbWUiOiAiam9zZSIsCiAgICAibGFzdG5hbWUiOiAiYWEiCn0=";
        String result = "eyJzdGF0dXMiOiJOT1RfRVFVQUxfU0laRSIsIm1lc3NhZ2UiOiJGaWxlcyBoYXZlIG5vdCBlcXVhbCBzaXplIn0=";
        DiffRecord diffRecord = DiffRecord.builder().requestId(id).rightFile(fileRight).build();
        when(diffRecordRepository.findById(id)).thenReturn(Optional.of(diffRecord));
        when(diffRecordRepository.save(any())).thenReturn(diffRecord);
        diffJsonService.saveDiffRecordData(id, fileLeft, true);
        verify(diffRecordRepository, times(1)).save(any());
        System.out.println(diffRecord.getComparisonResult());
        assertNotNull(diffRecord.getComparisonResult(), "Value expected when left and right exist");
        assertEquals(result, diffRecord.getComparisonResult());
    }


    @Test
    void saveDiffRecordDataInvalidJsonDataTest() {
        String id = "001";
        String fileRight = "ewogICAgIm5hbWUiOiAicGVwZSIKfQ==";
        String fileLeft = "ewogICAgbmFtZTogam9zZQp9";
        DiffRecord diffRecord = DiffRecord.builder().requestId(id).rightFile(fileRight).build();
        assertThrows(MalFormedJsonException.class, () -> {
            diffJsonService.saveDiffRecordData(id, fileLeft, true);
        }, "Expected exception as left is not a valid json");
    }

    @Test
    void generateDiffDifferentSizeTest() {
        String id = "001";
        String fileRight = "ewogICAgIm5hbWUiOiAicGVwZSIKfQ==";
        String fileLeft = "ewogICAgIm5hbWUiOiAiam9zZSIsCiAgICAibGFzdG5hbWUiOiAiYWEiCn0=";
        String result = "eyJzdGF0dXMiOiJOT1RfRVFVQUxfU0laRSIsIm1lc3NhZ2UiOiJEaWZmZXJlbnQgc2l6ZSwgTGVmdDo0NCBSaWdodDogMjIifQ==";
        DiffRecord diffRecord = DiffRecord.builder()
                .requestId(id)
                .rightFile(fileRight)
                .leftFile(fileLeft)
                .comparisonResult(result)
                .build();
        when(diffRecordRepository.findById(id)).thenReturn(Optional.of(diffRecord));
        StepVerifier.create(diffJsonService.generateDiff(id)).expectNextMatches(v -> {
            assertEquals(DiffResponse.Status.NOT_EQUAL_SIZE, v.getStatus());
            return true;
        }).verifyComplete();
    }

    @Test
    void generateDiffDifferentContentTest() {
        String id = "001";
        String fileRight = "ewogICAgIm5hbWUiOiAicGVwZSIKfQ==";
        String fileLeft = "ewogICAgIm5hbWUiOiAiam9zZSIKfQ==";
        String result = "eyJzdGF0dXMiOiJESUZGRVJFTlRfQ09OVEVOVCIsIm1lc3NhZ2UiOiJEaWZmZXJlbnQgQ29udGVudCIsIm9mZnNldExpc3QiOlt7Im9mZnNldCI6MTUsImxlbmd0aCI6M31dfQ==";
        DiffRecord diffRecord = DiffRecord.builder()
                .requestId(id)
                .rightFile(fileRight)
                .leftFile(fileLeft)
                .comparisonResult(result)
                .build();
        when(diffRecordRepository.findById(id)).thenReturn(Optional.of(diffRecord));

        StepVerifier.create(diffJsonService.generateDiff(id)).expectNextMatches(v -> {
            assertEquals(DiffResponse.Status.DIFFERENT_CONTENT, v.getStatus());
            return true;
        }).verifyComplete();
    }

    @Test
    void generateDiffEqualsContentTest() {
        String id = "001";
        String file = "ewogICAgIm5hbWUiOiAiam9zZSIKfQ==";
        String result = "eyJzdGF0dXMiOiJFUVVBTFMiLCJtZXNzYWdlIjoiRXF1YWxzIn0=";
        DiffRecord diffRecord = DiffRecord.builder()
                .requestId(id)
                .rightFile(file)
                .leftFile(file)
                .comparisonResult(result)
                .build();
        when(diffRecordRepository.findById(id)).thenReturn(Optional.of(diffRecord));
        StepVerifier.create(diffJsonService.generateDiff(id)).expectNextMatches(v -> {
            assertEquals(DiffResponse.Status.EQUALS, v.getStatus());
            return true;
        }).verifyComplete();
    }

    @Test
    void generateDiffNoResultTest() {
        String id = "001";
        when(diffRecordRepository.findById(id)).thenReturn(Optional.empty());
        //Expected an error as id is empty
        StepVerifier.create(diffJsonService.generateDiff(id))
                .expectError(NoDataFoundException.class);
    }

    @Test
    void generateDiffMissingLeftTest() {
        String id = "001";
        String file = "ewogICAgIm5hbWUiOiAiam9zZSIKfQ==";
        DiffRecord diffRecord = DiffRecord.builder()
                .requestId(id)
                .rightFile(file)
                .build();
        when(diffRecordRepository.findById(id)).thenReturn(Optional.of(diffRecord));
        StepVerifier.create(diffJsonService.generateDiff(id)).expectNextMatches(v -> {
            assertEquals("Please load the left file", v.getMessage());
            return true;
        }).verifyComplete();
    }

    @Test
    void generateDiffMissingRightTest() {
        String id = "001";
        String file = "ewogICAgIm5hbWUiOiAiam9zZSIKfQ==";
        DiffRecord diffRecord = DiffRecord.builder()
                .requestId(id)
                .leftFile(file)
                .build();
        when(diffRecordRepository.findById(id)).thenReturn(Optional.of(diffRecord));
        StepVerifier.create(diffJsonService.generateDiff(id)).expectNextMatches(v -> {
            assertEquals("Please load the right file", v.getMessage());
            return true;
        }).verifyComplete();
    }

    @Test
    void generateDiffMissingResultTest() {
        String id = "001";
        String file = "ewogICAgIm5hbWUiOiAiam9zZSIKfQ==";
        DiffRecord diffRecord = DiffRecord.builder()
                .requestId(id)
                .leftFile(file)
                .rightFile(file)
                .build();
        when(diffRecordRepository.findById(id)).thenReturn(Optional.of(diffRecord));
        StepVerifier.create(diffJsonService.generateDiff(id)).expectNextMatches(v -> {
            assertEquals("Unable to generate the result. Please load files again", v.getMessage());
            return true;
        }).verifyComplete();
    }

}
