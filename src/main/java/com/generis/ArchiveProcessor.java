package com.generis;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipFile;

public class ArchiveProcessor {

    private String inputFilePath;
    private String outputFilePath;
    private LinkedHashMap<String, String> zipMetadata;

    public ArchiveProcessor(String inputFile, String outputFilePath) {
        this.inputFilePath = inputFile;
        this.outputFilePath = outputFilePath;
        zipMetadata = new LinkedHashMap<>();
    }

    public void process() {
        try (ZipFile zipFile = new ZipFile(inputFilePath)) {

            ArrayList<FileProcessorInterface> fileProcessors = new ArrayList<>();

            AtomicInteger numberOfFiles = new AtomicInteger(0);
            AtomicInteger numberOfDirectories = new AtomicInteger(0);

            zipFile.stream()
                    .filter(entry -> !entry.getName().startsWith("__MACOSX")
                            && !entry.getName().startsWith(".") && !entry.getName().endsWith(".DS_Store"))
                    .forEach(entry -> {

                        if (entry.isDirectory()) {
                            numberOfDirectories.incrementAndGet();
                            return;
                        }

                        numberOfFiles.incrementAndGet();

                        FileProcessorInterface processor = new TextFileProcessor(entry, zipFile);
                        processor.process();
                        fileProcessors.add(processor);
                    });

            extractMetadata(zipFile);
            addMetadata("File Count", String.valueOf(numberOfFiles.get()));
            addMetadata("Directory Count", String.valueOf(numberOfDirectories.get()));

            ExcelWriter.createWriter(outputFilePath)
                    .write(fileProcessors, Optional.of(zipMetadata));

        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("An error occurred while processing the file: "
                    + e.getMessage());
        }

    }

    private void extractMetadata(ZipFile zipFile) {
        zipMetadata.put("Full Path", zipFile.getName());
        zipMetadata.put("File Name", FileInfoExtractor.extractFilename(zipFile.getName()));
        zipMetadata.put("File Path", FileInfoExtractor.extractPath(zipFile.getName()));
        zipMetadata.put("File Size", FileSizeConverter.convert(new File(inputFilePath).length()));
    }

    private void addMetadata(String key, String value) {
        zipMetadata.put(key, value);
    }

}