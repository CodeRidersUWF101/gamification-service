package com.coderiders.gamificationservice.models.db;

import com.coderiders.gamificationservice.models.enums.ElementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPoints {
    private long id;
    private String clerkId;
    private int points;
    private Timestamp dateEarned;
    private ElementType elementType;
    private short tier;
    private int elementId;
}
