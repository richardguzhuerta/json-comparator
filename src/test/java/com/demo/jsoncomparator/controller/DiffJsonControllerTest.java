package com.demo.jsoncomparator.controller;

import com.demo.jsoncomparator.dto.DiffRequest;
import com.demo.jsoncomparator.dto.DiffResponse;
import com.demo.jsoncomparator.service.DiffService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.demo.jsoncomparator.dto.DiffResponse.Status.EQUALS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Execute test cases for DiffJsonController
 *
 * @author Richard Guzman
 * @version v1, March 2021
 */
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = DiffJsonController.class)
public class DiffJsonControllerTest {
    private static final String BASE_PATH = "/v1/diff/";

    @MockBean
    DiffService diffJsonService;

    @Autowired
    private WebTestClient webClient;

    @Test
    public void saveLeftDiffRecordDataTest() {
        String id = "001";
        String content = "ewogICAgIm5hbWUiOiAiam9zZSIsCiAgICAibGFzdG5hbWUiOiAiYWEiCn0=";
        DiffRequest diffRequest = new DiffRequest(content);
        webClient.post().uri(BASE_PATH + id + "/left")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(diffRequest), DiffRequest.class)
                .exchange().expectStatus().isOk();
        verify(diffJsonService, times(1)).saveDiffRecordData(id, content, true);
    }

    @Test
    public void saveRightDiffRecordDataTest() {
        String id = "001";
        String content = "ewogICAgIm5hbWUiOiAiam9zZSIsCiAgICAibGFzdG5hbWUiOiAiYWEiCn0=";
        DiffRequest diffRequest = new DiffRequest(content);
        webClient.post().uri(BASE_PATH + id + "/right")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(diffRequest), DiffRequest.class)
                .exchange().expectStatus().isOk();
        verify(diffJsonService, times(1)).saveDiffRecordData(id, content, false);
    }

    @Test
    public void generateDiffTest() {
        String id = "001";
        DiffResponse diffResponse = DiffResponse.builder()
                .status(EQUALS)
                .message(EQUALS.getDescription()).build();
        when(diffJsonService.generateDiff(id)).thenReturn(Mono.just(diffResponse));
        webClient.get().uri(BASE_PATH + id)
                .exchange().expectStatus().isOk().expectBody(DiffResponse.class)
                .value(DiffResponse::getStatus, Matchers.equalTo(diffResponse.getStatus()));
        verify(diffJsonService, times(1)).generateDiff(id);
    }
}
