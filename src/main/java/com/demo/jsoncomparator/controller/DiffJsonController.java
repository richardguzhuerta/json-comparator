package com.demo.jsoncomparator.controller;

import com.demo.jsoncomparator.dto.DiffRequest;
import com.demo.jsoncomparator.dto.DiffResponse;
import com.demo.jsoncomparator.service.DiffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * Endpoints exposed to handle json comparison functionality
 *
 * @author Richard Guzman
 * @version v1, March 2021
 */
@Slf4j
@RestController
@RequestMapping("${api.diff.path}")
public class DiffJsonController {

    private DiffService diffJsonService;

    public DiffJsonController(DiffService diffJsonService) {
        this.diffJsonService = diffJsonService;
    }

    /**
     * Exposed GET endpoint to  get the result of the diff given a request id
     *
     * @param requestId id of diff request
     * @return object containing diff result including details of offset and length for differences
     */
    @GetMapping("/{id}")
    public Mono<DiffResponse> generateDiff(@PathVariable("id") @NotBlank String requestId) {
        return diffJsonService.generateDiff(requestId);
    }

    /**
     * Upload left side json for diff
     *
     * @param requestId id of diff request
     * @param request   detailed data request including encoded json content
     */
    @PostMapping("/{id}/left")
    public void saveLeftDiffRecordData(@PathVariable("id") @NotBlank String requestId, @Valid @RequestBody DiffRequest request) {
        diffJsonService.saveDiffRecordData(requestId, request.getFileContent(), true);
    }

    /**
     * Upload right side json for diff
     *
     * @param requestId id of diff request
     * @param request   detailed data request including encoded json content
     */
    @PostMapping("/{id}/right")
    public void saveRightDiffRecordData(@PathVariable("id") @NotBlank String requestId, @Valid @RequestBody DiffRequest request) {
        diffJsonService.saveDiffRecordData(requestId, request.getFileContent(), false);
    }
}
