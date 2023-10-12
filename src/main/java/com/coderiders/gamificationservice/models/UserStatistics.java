package com.coderiders.gamificationservice.models;

public record UserStatistics(
        int pagesRead,
        int challengesCompleted,
        int badgesEarned,
        int booksRead,
        int totalFriends,
        int readingStreak,
        boolean justLostStreak
) {}
