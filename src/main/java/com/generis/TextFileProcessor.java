package com.generis;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class TextFileProcessor implements FileProcessorInterface {
    private String filePath;
    private String fileName;
    private String fileSize;
    private FileTime lastModified;
    private FileTime createdAt;
    private long wordCount;
    private long characterCount;
    private String comment;
    private String firstWord;
    private int firstWordSize;
    private String lastWord;
    private int lastWordSize;
    private String medianWord;
    private String medianWordSize;
    private String longestWord;
    private int longestWordSize;
    private String shortestWord;
    private int shortestWordSize;
    private String leastUsedWord;
    private int leastUsedWordCount;
    private String mostUsedWord;
    private int mostUsedWordCount;

    private TreeMap<String, Integer> wordOccurence = new TreeMap<>();

    private ZipEntry fileEntry;
    private ZipFile zipFile;

    public TextFileProcessor(ZipEntry fileEntry, ZipFile zipFile) {
        this.fileEntry = fileEntry;
        this.zipFile = zipFile;
    }

    private void processMatadata() {
        filePath = fileEntry.getName();
        fileName = FileInfoExtractor.extractFilename(filePath);
        fileSize = FileSizeConverter.convert(fileEntry.getSize());
        createdAt = fileEntry.getCreationTime();
        lastModified = fileEntry.getLastModifiedTime();
        comment = fileEntry.getComment();

        longestWordSize = 0;
        shortestWordSize = Integer.MAX_VALUE;

        mostUsedWordCount = 0;
        leastUsedWordCount = Integer.MAX_VALUE;
    }

    @Override
    public void process() {

        processMatadata();

        try (InputStream inputStream = zipFile.getInputStream(fileEntry)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            reader.lines().forEach(line -> {

                String[] words = line.split("\\s+");

                characterCount += line.length();

                Arrays.stream(words)
                        .filter(word -> word.trim().length() > 0)
                        .map(word -> word.replaceAll("[^a-zA-Z0-9-]", "")) // remove punctuations & special chars
                        .forEach(word -> {

                            if (firstWord == null) {
                                firstWord = word;
                                firstWordSize = word.length();
                            }

                            lastWord = word;
                            lastWordSize = word.length();

                            if (word.length() > longestWordSize) {
                                longestWord = word;
                                longestWordSize = word.length();
                            }

                            if (word.length() < shortestWordSize) {
                                shortestWord = word;
                                shortestWordSize = word.length();
                            }

                            if (wordOccurence.containsKey(word)) {
                                wordOccurence.put(word, wordOccurence.get(word) + 1);
                            } else {
                                wordOccurence.put(word, 1);
                            }

                        });

            });

            ArrayList<String> midWords = new ArrayList<>();
            int primaryMidpoint = wordOccurence.size() / 2;
            int secondaryMidpoint = (wordOccurence.size() % 2 == 0) ? primaryMidpoint - 1 : -1;
            int index = 0;

            for (var mapEntry : wordOccurence.entrySet()) {

                String word = mapEntry.getKey();
                int count = mapEntry.getValue();

                wordCount += count;

                if (index == primaryMidpoint || index == secondaryMidpoint) {
                    midWords.add(word);
                }
                index++;

                if (count > mostUsedWordCount) {
                    mostUsedWord = word;
                    mostUsedWordCount = count;
                }

                if (count < leastUsedWordCount) {
                    leastUsedWord = word;
                    leastUsedWordCount = count;
                }

            }

            if (midWords.size() == 2) {
                medianWord = midWords.get(0) + ", " + midWords.get(1);
                medianWordSize = midWords.get(0).length() + ", " + midWords.get(1).length();
            } else if (midWords.size() == 1) {
                medianWord = midWords.get(0);
                medianWordSize = String.valueOf(midWords.get(0).length());
            }

        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("An error occurred while processing the file: "
                    + e.getMessage());
        } finally {
            wordOccurence.clear();
        }

    }

    @Override
    public String getCellData(int cellIndex) {
        switch (cellIndex) {
            case 0:
                return filePath;
            case 1:
                return fileName;
            case 2:
                return String.valueOf(fileSize);
            case 3:
                return String.valueOf(lastModified);
            case 4:
                return String.valueOf(createdAt);
            case 5:
                return String.valueOf(wordCount);
            case 6:
                return String.valueOf(characterCount);
            case 7:
                return comment;
            case 8:
                return firstWord;
            case 9:
                return String.valueOf(firstWordSize);
            case 10:
                return lastWord;
            case 11:
                return String.valueOf(lastWordSize);
            case 12:
                return medianWord;
            case 13:
                return String.valueOf(medianWordSize);
            case 14:
                return longestWord;
            case 15:
                return String.valueOf(longestWordSize);
            case 16:
                return shortestWord;
            case 17:
                return String.valueOf(shortestWordSize);
            case 18:
                return leastUsedWord;
            case 19:
                return String.valueOf(leastUsedWordCount);
            case 20:
                return mostUsedWord;
            case 21:
                return String.valueOf(mostUsedWordCount);
            default:
                return "";
        }
    }

    @Override
    public String getCellData(String attribute) {
        try {
            Field field = getClass().getDeclaredField(attribute);
            Object fieldValue = field.get(this);
            String value = fieldValue != null ? fieldValue.toString() : "";
            return value;
        } catch (Exception e) {
            // e.printStackTrace();
            return "";
        }

    }

    @Override
    public String toString() {
        return "FileProcessor{" + "filePath:'" + filePath + '\'' + ", fileName:'" + fileName + '\'' + ", fileSize:"
                + fileSize + ", lastModified:" + lastModified + ", createdAt:" + createdAt + ", wordCount:" + wordCount
                + ", characterCount:" + characterCount + ", comment:'" + comment + '\'' + ", firstWord:'" + firstWord
                + '\'' + ", firstWordSize:" + firstWordSize + ", lastWord:'"
                + lastWord + '\'' + ", lastWordSize:" + lastWordSize + ", medianWord:'" + medianWord + '\''
                + ", medianWordSize:" + medianWordSize + ", longestWord:'" + longestWord + '\'' + ", longestWordSize:"
                + longestWordSize + ", shortestWord:'" + shortestWord + '\'' + ", shortestWordSize:" + shortestWordSize
                + ", leastUsedWord:'" + leastUsedWord + '\'' + ", leastUsedWordCount:" + leastUsedWordCount
                + ", mostUsedWord:'" + mostUsedWord + '\'' + ", mostUsedWordCount:" + mostUsedWordCount + '}';
    }
}
