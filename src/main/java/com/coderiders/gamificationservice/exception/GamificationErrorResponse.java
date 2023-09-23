package com.coderiders.gamificationservice.exception;

import lombok.Data;

import java.util.List;

@Data
public class GamificationErrorResponse {
    private int errorCode;
    private String errorId;
    private String errorMessage;
    private List<ErrorObj> additionalErrors;
}
