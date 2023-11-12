package com.coderiders.gamificationservice.models.commonutils.exceptions;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
  private int errorCode;
  private String errorId;
  private String errorMessage;
  private List<ErrorObj> additionalErrors;
}