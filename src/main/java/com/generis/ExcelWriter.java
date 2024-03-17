package com.generis;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;
import java.io.OutputStream;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

public class ExcelWriter implements WriterInterface {

    private String filePath;

    private String[] headers = { "Full Path", "File Name", "File Size", "Created At", "Last Modified",
            "Word Count", "Character Count", "Comment", "First Word", "First Word Size", "Last Word",
            "Last Word Size", "Median Word", "Median Word Size", "Longest Word", "Longest Word Size",
            "Shortest Word", "Shortest Word Size", "Least Used Word", "Least Used Word Count",
            "Most Used Word", "Most Used Word Count" };

    /*
     * static factory method
     */
    public static WriterInterface createWriter(String filePath) {
        return new ExcelWriter(filePath);
    }

    public ExcelWriter(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void write(ArrayList<FileProcessorInterface> fileProcessors,
            Optional<LinkedHashMap<String, String>> zipMetadata) {

        try (
                OutputStream outputStream = new FileOutputStream(new File(filePath));
                Workbook wb = new Workbook(outputStream, "Zip Stats", "1.0")) {

            Worksheet ws = wb.newWorksheet("Sheet 1");

            // Write headers with style
            ws.range(0, 0, 0, headers.length).style()
                    .fillColor("cccccc").bold().set();
            for (int i = 0; i < headers.length; i++) {
                ws.value(0, i, headers[i]);
            }

            // Write the file data
            AtomicInteger rowCounter = new AtomicInteger(1);
            fileProcessors.forEach(fileProcessor -> {
                int row = rowCounter.getAndIncrement();
                for (int i = 0; i < headers.length; i++) {
                    ws.value(row, i, fileProcessor.getCellData(i));
                }
            });

            // Write the zip file metadata with 5 rows padding
            if (zipMetadata.isPresent()) {
                LinkedHashMap<String, String> metadata = zipMetadata.get();
                rowCounter.set(rowCounter.get() + 5);

                // merge 2 cells and center-bold the text
                int mergeRow = rowCounter.getAndIncrement();
                ws.range(mergeRow, 0, mergeRow, 1).merge();
                ws.range(mergeRow, 0, mergeRow, 1).style()
                        .fillColor("cccccc")
                        .horizontalAlignment("center")
                        .bold().set();
                ws.value(mergeRow, 0, "Zip File Metadata");

                metadata.forEach((key, value) -> {
                    int row = rowCounter.getAndIncrement();
                    ws.value(row, 0, key);
                    ws.value(row, 1, value);
                });

            }

        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("An error occurred while writing the Excel file: "
                    + e.getMessage());
        }

    }

}
