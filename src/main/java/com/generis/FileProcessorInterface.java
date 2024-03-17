package com.generis;

public interface FileProcessorInterface {
    void process();
    String getCellData(int cellIndex);
    String getCellData(String attribute);   // using Reflection
}
