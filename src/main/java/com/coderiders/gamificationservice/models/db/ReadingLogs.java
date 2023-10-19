package com.coderiders.gamificationservice.models.db;

import com.coderiders.commonutils.models.enums.ActivityAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingLogs {
    private long id;
    private String clerkId;
    private LocalDateTime date;
    private int pagesRead;
    private String bookId;
    private ActivityAction action;
}
