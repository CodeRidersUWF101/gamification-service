package com.coderiders.gamificationservice.models.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingLogs {
    private long id;
    private String clerkId;
    private String title;
    private String message;
    private Timestamp dateSent;
    private boolean isRead;
    private boolean isSent;
}
