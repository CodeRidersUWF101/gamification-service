package com.coderiders.gamificationservice.utilities;

public class Constants {
    public static final String BADGE_EARNED = "Badge Earned";
    public static final int PAGES_MIN = 200;
    public static final int PAGES_MAX = 20000;
    public static final int FRIENDS_MIN = 1;
    public static final int STREAK_MIN = 7;
    public static final int BOOKS_READ_MIN = 5;
    public static final int BOOKS_COLLECTED_MIN = 10;
    public static final int BADGES_MIN = 1;
    public static final int CHALLENGES_MIN = 1;
    public static final int MAX_TIER = 5;

    public static final int[] pageThresholds = new int[]{ 200, 1000, 5000, 10000, 20000 };
    public static final int[] friendsThresholds = new int[]{ 1, 5, 10, 15, 20 };
    public static final int[] streakThresholds = new int[]{ 7, 14, 30, 60, 90 };
    public static final int[] booksReadThresholds = new int[]{ 5, 10, 25, 50, 75 };
    public static final int[] reviewThresholds = new int[]{ 1, 5, 10, 15, 20 };
    public static final int[] booksCollectedThresholds = new int[]{ 10, 25, 50, 75, 100 };
    public static final int[] badgesThresholds = new int[]{ 1, 5, 10, 15, 20 };

    public static final int[] challengeThresholds = new int[]{ 1, 5, 10, 15, 20 };

}
