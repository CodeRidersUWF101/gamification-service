package com.coderiders.gamificationservice.utilities;

import com.coderiders.gamificationservice.models.db.ReadingLogs;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReadingStreak {

    public static class StreakResult {
        public int length;
        public boolean justLost;

        public StreakResult(int length, boolean justLost) {
            this.length = length;
            this.justLost = justLost;
        }
    }

    public static StreakResult calculateReadingStreak(List<ReadingLogs> readingLogs) {
        if (readingLogs == null || readingLogs.isEmpty()) {
            return new StreakResult(-1, false);
        }

        int streak = 1;
        boolean justLost = false;
        LocalDateTime previousDate = readingLogs.get(readingLogs.size() - 1).getDate();

        for (int i = readingLogs.size() - 2; i >= 0; i--) {
            LocalDateTime currentDate = readingLogs.get(i).getDate();

            long daysBetween = ChronoUnit.DAYS.between(currentDate.toLocalDate(), previousDate.toLocalDate());

            if (daysBetween == 1) {
                streak++;
            } else if (daysBetween > 1) {
                if (streak == 1) {
                    justLost = true;
                }
                break;
            }

            previousDate = currentDate;
        }

        return new StreakResult(streak >= 2 ? streak : -1, justLost);
    }
}
