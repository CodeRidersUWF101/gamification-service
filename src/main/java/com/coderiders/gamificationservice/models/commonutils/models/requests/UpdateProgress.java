package com.coderiders.gamificationservice.models.commonutils.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProgress implements Serializable {
    private String clerkId;
    private String bookId;
    private int currentPage;
    private int pagesRead;
    private String action;
}
