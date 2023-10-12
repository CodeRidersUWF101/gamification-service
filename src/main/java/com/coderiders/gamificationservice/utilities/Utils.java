package com.coderiders.gamificationservice.utilities;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Utils {
    public static LocalDateTime convertToLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

}
