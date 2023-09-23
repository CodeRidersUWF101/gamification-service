package com.coderiders.gamificationservice.exception;

import lombok.Builder;

@Builder
public class ErrorObj {
    private int errorCode;
    private String errorId;
    private String errorMessage;
}
