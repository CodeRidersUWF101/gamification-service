package com.coderiders.gamificationservice.models.db;


import com.coderiders.commonutils.models.enums.ActivityAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityLog {
    private long id;
    private String clerkId;
    private ActivityAction action;
    private int actionId;
    private Timestamp date;
}
