package com.generis;

public class Main {
    public static void main(String[] args) {

        if (args.length < 1 || !args[0].endsWith(".zip")) {
            System.out.println("Please provide a path to a valid zip file");
            return;
        }

        String inputFilePath = args[0];

        // String inputFilePath = "/Users/salisuwy/Desktop/sample.zip";
        String outputFilePath = FileInfoExtractor.makeOutputFilename(inputFilePath);

        ArchiveProcessor processor = new ArchiveProcessor(inputFilePath,
                outputFilePath);
        processor.process();

    }
}
