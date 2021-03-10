package com.demo.jsoncomparator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class DiffResponse implements Serializable {
    private Status status;
    private String message;
    private List<DiffOffset> offsetList;


    public enum Status {
        EQUALS("Both files are equals"),
        NOT_EQUAL_SIZE("Files have not equal size"),
        DIFFERENT_CONTENT("Files have different content");

        private final String description;

        Status(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiffOffset implements Serializable {
        private long offset;
        private long length;
    }
}

