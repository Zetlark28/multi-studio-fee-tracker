package com.zetlark.multistudiofeetrackerbe.application.common.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseError {
    private Integer statusCode;
    private String errorMessage;
}