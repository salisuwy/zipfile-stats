package com.generis;

public class FileSizeConverter {

    public static String convert(long size) {
        return convert(size, "KB");
    }
    

    public static String convert(long size, String unit) {
        if (size <= 0) {
            return "0";
        }

        switch (unit) {
            case "B":
                return String.format("%d B", size);
            case "KB":
                return String.format("%.1f KB", (double) size / 1024);
            case "MB":
                return String.format("%.1f MB", (double) size / Math.pow(1024, 2));
            case "GB":
                return String.format("%.1f GB", (double) size / Math.pow(1024, 3));
            case "TB":
                return String.format("%.1f TB", (double) size / Math.pow(1024, 4));
            default:
                return "Invalid unit";
        }
    }

}
