package com.demo.jsoncomparator.util;


import com.demo.jsoncomparator.dto.DiffResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.params.provider.Arguments;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Read data for data driven tests
 *
 * @author Richard Guzman
 * @version v1, March 2021
 */
@Slf4j
public class TestDataLoader {

    /**
     * Read the data from a base64 encoded json give a name
     *
     * @param fileName json file name for testing
     * @return content of the file
     */
    private static String readFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try {
            Path path = Paths.get(TestDataLoader.class.getClassLoader().getResource(fileName).toURI());
            try (Stream<String> lines = Files.lines(path)) {
                lines.forEach(content::append);
            }
        } catch (Exception e) {
            throw new RuntimeException("File not found:" + fileName);
        }
        return content.toString();
    }

    /**
     * Read offsets from a formatted string
     *
     * @param data formatted string containing details of offsets
     * @return List of offset objects
     */
    private static List<DiffResponse.DiffOffset> buildResult(String data) {
        List<DiffResponse.DiffOffset> results = new ArrayList<>();
        try {
            if (StringUtils.isNotEmpty(data)) {
                String[] listValues = data.split("\\|");

                for (String listValue : listValues) {
                    String[] values = listValue.split("<-->");
                    if (values.length == 2) {
                        results.add(DiffResponse.DiffOffset.builder()
                                .offset(Long.parseLong(values[0]))
                                .length(Long.parseLong(values[1])).build());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Unable to parse offset: " + e.getMessage());
        }
        return results;

    }

    /**
     * Prepare the data for data driven test
     *
     * @return List of arguments for parametrized test
     */
    public static List<Arguments> loadData() {
        List<Arguments> data = new ArrayList<Arguments>();
        try {
            Path path = Paths.get(TestDataLoader.class.getClassLoader().getResource("testcases.txt").toURI());

            try (Stream<String> lines = Files.lines(path)) {
                lines.forEach(p -> {
                    String[] values = p.split(",");
                    List<DiffResponse.DiffOffset> results;
                    if (values.length > 2) {
                        results = buildResult(values[2]);
                    } else {
                        results = new ArrayList<>();
                    }
                    data.add(Arguments.of(values[0],
                            readFile(values[0] + "_A.txt"),
                            readFile(values[0] + "_B.txt"),
                            DiffResponse.Status.valueOf(values[1]),
                            results
                    ));
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Problem loading data:" + e.getMessage());
        }
        return data;
    }
}
