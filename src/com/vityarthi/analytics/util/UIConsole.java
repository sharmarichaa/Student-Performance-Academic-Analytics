package com.vityarthi.analytics.util;

/**
 * UI Console helper for ASCII banners, table formatting, and clean status tags.
 */
public class UIConsole {

    public static void printHeader(String title) {
        String border = "==========================================================================================";
        System.out.println("\n" + border);
        int padding = (88 - title.length()) / 2;
        String spaces = " ".repeat(Math.max(0, padding));
        System.out.println(spaces + title.toUpperCase());
        System.out.println(border);
    }

    public static void printSubHeader(String subTitle) {
        System.out.println("\n--- " + subTitle + " ---");
    }

    public static void printBanner() {
        System.out.println("==========================================================================================");
        System.out.println("     __   _____ _____                       _   _                                         ");
        System.out.println("     \\ \\ / /_  |_   _|   _  __ _ _ __| |_| |__ (_)                                ");
        System.out.println("      \\ V /  | | | |  | | | |/ _` | '__| __| '_ \\| |                                ");
        System.out.println("       | |  _| |_| |  | |_| | (_| | |  | |_| | | | |                                ");
        System.out.println("       |_| |_____|_|   \\__, |\\__,_|_|   \\__|_| |_|_|                                ");
        System.out.println("                       |___/                                                        ");
        System.out.println("               STUDENT PERFORMANCE & ACADEMIC ANALYTICS SYSTEM                             ");
        System.out.println("==========================================================================================");
    }

    public static void printSuccess(String msg) {
        System.out.println("  [SUCCESS] " + msg);
    }

    public static void printError(String msg) {
        System.out.println("  [ERROR] " + msg);
    }

    public static void printWarning(String msg) {
        System.out.println("  [WARNING] " + msg);
    }

    public static void printInfo(String msg) {
        System.out.println("  [INFO] " + msg);
    }

    public static void printDivider() {
        System.out.println("------------------------------------------------------------------------------------------");
    }
}
