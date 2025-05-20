package br.com.wallet.api.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
public class ProblemDetail {
    
    private Integer status;
    private String title;
    private String detail;
    private LocalDateTime timestamp;
    private Map<String, String> fields;
}