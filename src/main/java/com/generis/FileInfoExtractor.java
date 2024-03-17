package com.generis;

import java.io.File;

public class FileInfoExtractor {

    public static String extractFilename(String fullPath) {
        String separator = File.separator; // OS dependent file separator
        return fullPath.substring(fullPath.lastIndexOf(separator) + 1)
                .replaceAll(".zip", "");
    }


    public static String extractPath(String fullPath) {
        String separator = File.separator; // OS dependent file separator
        return fullPath.substring(0, fullPath.lastIndexOf(separator));
    }

    
    public static String makeOutputFilename(String fullPath) {
        String separator = File.separator; // OS dependent file separator
        String outFileName = FileInfoExtractor.extractFilename(fullPath);
        String outFilePath = FileInfoExtractor.extractPath(fullPath);

        return outFilePath + separator + outFileName + ".xlsx";
    }

}
