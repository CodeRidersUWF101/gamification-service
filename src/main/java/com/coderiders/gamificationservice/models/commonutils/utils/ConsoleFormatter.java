package com.coderiders.gamificationservice.models.commonutils.utils;


public class ConsoleFormatter {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";

    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public enum Color {
        BLACK, RED, GREEN, YELLOW, BLUE, PURPLE, CYAN, WHITE
    }

    public static void printColored(String text, Color color) {
        String colorCode = getColorCode(color);

        System.out.println(colorCode + ANSI_BOLD + text + ANSI_RESET);
    }

    public static void printColored(String text, Color color, boolean isBold) {
        String colorCode = getColorCode(color);
        String boldCode = isBold ? ANSI_BOLD : "";

        System.out.println(colorCode + boldCode + text + ANSI_RESET);
    }

    private static String getColorCode(Color color) {
        return switch (color) {
            case BLACK -> ANSI_BLACK;
            case RED -> ANSI_RED;
            case GREEN -> ANSI_GREEN;
            case YELLOW -> ANSI_YELLOW;
            case BLUE -> ANSI_BLUE;
            case PURPLE -> ANSI_PURPLE;
            case CYAN -> ANSI_CYAN;
            case WHITE -> ANSI_WHITE;
        };
    }

}

