package com.ahumadamob.fnanz.dto.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseSuccessDto<T> {
    private String message;
    private T data;

    @Builder.Default
    private Instant timestamp = Instant.now();
}

