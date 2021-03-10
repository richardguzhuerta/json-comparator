package com.demo.jsoncomparator.service;

import com.demo.jsoncomparator.dao.DiffRecordRepository;
import com.demo.jsoncomparator.dto.DiffResponse;
import com.demo.jsoncomparator.entity.DiffRecord;
import com.demo.jsoncomparator.exception.MalFormedJsonException;
import com.demo.jsoncomparator.exception.NoDataFoundException;
import com.demo.jsoncomparator.util.DiffUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Defines JSON files comparison operations
 *
 * @author Richard Guzman
 * @version v1, March 2021
 */
@Service
public class DiffJsonServiceImpl implements DiffService {
    @Autowired
    private DiffRecordRepository diffRecordRepository;

    /**
     * Save the content in the database. If the record is complete (left and right side),
     * include the details of comparison result when saving a diff record
     *
     * @param requestId id of diff request
     * @param content json in base64
     * @param isLeft flag that indicates where the content will be stored
     * @throws MalFormedJsonException
     */
    @Override
    public void saveDiffRecordData(String requestId, String content, boolean isLeft) throws MalFormedJsonException {
        if (DiffUtil.isBase64EncodedJson(content)) {
            Optional<DiffRecord> optionalDiffRecord = diffRecordRepository.findById(requestId);
            DiffRecord diffRecord = optionalDiffRecord.orElseGet(() -> DiffRecord.builder().requestId(requestId).build());
            if (isLeft) {
                diffRecord.setLeftFile(content);
            } else {
                diffRecord.setRightFile(content);
            }

            if (StringUtils.isNotEmpty(diffRecord.getLeftFile()) &&
                    StringUtils.isNotEmpty(diffRecord.getRightFile())
            ) {
                DiffResponse diffResponse = compareJson(diffRecord.getLeftFile(),
                        diffRecord.getRightFile());
                diffRecord.setComparisonResult(DiffUtil.toBase64(diffResponse));
            }

            diffRecordRepository.save(diffRecord);
        } else {
            throw new MalFormedJsonException();
        }
    }

    /**
     * Compares 2 given json
     *
     * @param left  json in base64 corresponding to left side
     * @param right json in base64 corresponding to right side
     * @return object containing diff result including details of offset and length for differences
     */
    private DiffResponse compareJson(String left, String right) {
        DiffResponse diffResponse = null;
        if (StringUtils.isNotEmpty(left) &&
                StringUtils.isNotEmpty(right)) {
            diffResponse = DiffResponse.builder().build();
            String jsonLeft = DiffUtil.getJson(left);
            String jsonRight = DiffUtil.getJson(right);
            if (jsonLeft.equals(jsonRight)) {
                diffResponse.setStatus(DiffResponse.Status.EQUALS);
                diffResponse.setMessage(DiffResponse.Status.EQUALS.getDescription());
            } else if (jsonLeft.length() != jsonRight.length()) {
                diffResponse.setStatus(DiffResponse.Status.NOT_EQUAL_SIZE);
                diffResponse.setMessage(DiffResponse.Status.NOT_EQUAL_SIZE.getDescription());
            } else {
                diffResponse.setStatus(DiffResponse.Status.DIFFERENT_CONTENT);
                diffResponse.setMessage(DiffResponse.Status.DIFFERENT_CONTENT.getDescription());
                diffResponse.setOffsetList(compareStrings(jsonLeft, jsonRight));
            }
        }
        return diffResponse;
    }

    /**
     * Calculate the offset and length when a difference is encountered
     *
     * @param left string in json corresponding to left side
     * @param right string in json corresponding to right side
     * @return list of differences including details about offset and length
     */
    private List<DiffResponse.DiffOffset> compareStrings(String left, String right) {
        List<DiffResponse.DiffOffset> results = new ArrayList<>();
        long currentOffset = -1;
        long currentLength = 0;
        for (int i = 0; i < left.length(); i++) {
            if (left.charAt(i) != right.charAt(i)) {
                if (currentLength == 0) {
                    currentOffset = i;
                }
                currentLength++;
            } else {
                if (currentOffset != -1) {
                    results.add(DiffResponse.DiffOffset.builder()
                            .offset(currentOffset)
                            .length(currentLength)
                            .build()
                    );
                    currentOffset = -1;
                    currentLength = 0;
                }
            }
        }
        if (currentOffset != -1) {
            results.add(DiffResponse.DiffOffset.builder()
                    .offset(currentOffset)
                    .length(currentLength)
                    .build()
            );
        }
        return results;
    }

    /**
     * Retrieve from database calculated diff result given a request id
     *
     * @param requestId id of diff request
     * @return object containing diff result including details of offset and length for differences
     */
    @Override
    public Mono<DiffResponse> generateDiff(String requestId) {
        Optional<DiffRecord> optionalDiffRecord = diffRecordRepository.findById(requestId);
        DiffResponse diffResponse = null;
        if (optionalDiffRecord.isPresent()) {
            DiffRecord diffRecord = optionalDiffRecord.get();
            if (StringUtils.isNotEmpty(diffRecord.getComparisonResult())) {
                diffResponse = DiffUtil.toDiffResponse(diffRecord.getComparisonResult());
            } else {
                diffResponse = DiffResponse.builder().build();
                if (StringUtils.isEmpty(diffRecord.getLeftFile())) {
                    diffResponse.setMessage("Please load the left file");
                } else if (StringUtils.isEmpty(diffRecord.getRightFile())) {
                    diffResponse.setMessage("Please load the right file");
                } else {
                    diffResponse.setMessage("Unable to generate the result. Please load files again");
                }
            }
        } else {
            return Mono.error(new NoDataFoundException("Id not found"));
        }
        return Mono.just(diffResponse);
    }
}
