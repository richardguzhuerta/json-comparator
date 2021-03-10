package com.demo.jsoncomparator.integration;

import com.demo.jsoncomparator.dto.DiffRequest;
import com.demo.jsoncomparator.dto.DiffResponse;
import com.demo.jsoncomparator.util.TestDataLoader;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test
 *
 * @author Richard Guzman
 * @version v1, March 2021
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    private static final String HOST = "http://localhost:";
    private static final String BASE_PATH = "/v1/diff/";

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient web;

    /**
     * Data provider method
     *
     * @return List of arguments for parametrized test
     */
    private static List<Arguments> loadData() {
        return TestDataLoader.loadData();

    }

    @ParameterizedTest
    @MethodSource("loadData")
    void testComparison(String id, String fileA, String fileB, DiffResponse.Status status, List<DiffResponse.DiffOffset> results) {
        HttpHeaders headers = new HttpHeaders();
        DiffRequest diffRequestA = new DiffRequest(fileA);
        DiffRequest diffRequestB = new DiffRequest(fileB);
        HttpEntity<DiffRequest> requestLeft = new HttpEntity<>(diffRequestA, headers);
        HttpEntity<DiffRequest> requestRight = new HttpEntity<>(diffRequestB, headers);

        web.post().uri(HOST + port + BASE_PATH + id + "/left")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(diffRequestA), DiffRequest.class)
                .exchange().expectStatus().isOk();

        web.post().uri(HOST + port + BASE_PATH + id + "/right")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(diffRequestB), DiffRequest.class)
                .exchange().expectStatus().isOk();

        web.get().uri(HOST + port + BASE_PATH + id)
                .exchange().expectStatus().isOk().expectBody(DiffResponse.class)
                .value(DiffResponse::getStatus, Matchers.equalTo(status))
                .value(p -> {
                    if (results.size() > 0) {
                        assertNotNull(p.getOffsetList());
                        assertEquals(p.getOffsetList().size(), results.size());
                        p.getOffsetList().forEach(d -> assertTrue(results.contains(d)));
                    } else {
                        assertNull(p.getOffsetList());
                    }
                });
    }
}
