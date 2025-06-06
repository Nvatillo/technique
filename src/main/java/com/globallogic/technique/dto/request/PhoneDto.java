package com.globallogic.technique.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhoneDto {

    @NotNull(message = "number is required")
    @Min(value = 1000000, message = "number must be valid")
    private long number;
    @NotNull(message = "number is required")
    @Min(value = 1000000, message = "number must be valid")
    private int citycode;
    @NotBlank(message = "country code cannot be empty")
    private String contrycode;
}