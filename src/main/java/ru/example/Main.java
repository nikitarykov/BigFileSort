package ru.example;

import java.io.File;

public class Main {

    private static final String INPUT_FILE_NAME = "test_data.txt";
    private static final String OUTPUT_FILE_NAME = "sorted_data.txt";
    private static final String TEMP_DIR_NAME = File.separator + "temp";
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    public static void main(String[] args) {
        FileGenerator generator = new FileGenerator();
        generator.generateFile(INPUT_FILE_NAME, 50_000_000, 256, ALPHABET);
        FileSorter sorter = new FileSorter();
        sorter.createAndSortChunks(INPUT_FILE_NAME, TEMP_DIR_NAME);
        sorter.mergeChunks(TEMP_DIR_NAME, OUTPUT_FILE_NAME);
    }
}

