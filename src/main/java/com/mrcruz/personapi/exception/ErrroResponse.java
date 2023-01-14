package com.mrcruz.personapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrroResponse {

    private LocalDateTime timestamp;
    private Integer status;
    private String mensagem;
    private String path;
    private List<Field> fields;

    @Getter
    @AllArgsConstructor
    public static class Field {
        private String campo;
        private String mensagem;
    }
}


