package com.generis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;

public interface WriterInterface {
    
    void write(ArrayList<FileProcessorInterface> fileProcessors, Optional<LinkedHashMap<String, String>> zipMetadata);
}
