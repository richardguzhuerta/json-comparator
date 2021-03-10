package com.demo.jsoncomparator.dto;

import com.demo.jsoncomparator.util.DiffUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiffRequest {
    @Valid
    @NotBlank(message = "Content can not be empty")
    private String fileContent;

    @Valid
    @AssertTrue(message = "File content is not a Base64 encode Json")
    public boolean isValidContent() {
        return DiffUtil.isBase64EncodedJson(fileContent);
    }
}
