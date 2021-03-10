package com.demo.jsoncomparator.service;

import com.demo.jsoncomparator.dto.DiffResponse;
import com.demo.jsoncomparator.exception.MalFormedJsonException;
import reactor.core.publisher.Mono;

/**
 * Defines files comparison operations
 *
 * @author Richard Guzman
 * @version v1, March 2021
 */
public interface DiffService {
    /**
     * Save the content in the database. If the record is complete (left and right side),
     * include the details of comparison result when saving a diff record
     *
     * @param requestId id of diff request
     * @param content json in base64
     * @param isLeft flag that indicates where the content will be stored
     * @throws MalFormedJsonException
     */
    void saveDiffRecordData(final String requestId, final String content, boolean isLeft);

    /**
     * Retrieve from database calculated diff result given a request id
     *
     * @param requestId id of diff request
     * @return object containing diff result including details of offset and length for differences
     */
    Mono<DiffResponse> generateDiff(final String requestId);

}
