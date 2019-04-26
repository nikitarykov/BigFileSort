package ru.example;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileGenerator {

    private static final int CHUNK_SIZE = 100000;
    private static final Random random = new Random();

    /**
     * Метод генерирует файл из произвольных строк
     * @param fileName - название генерируемого файла
     * @param linesCount - необходимое количество строк в сгенерированном файле
     * @param maxLineLength - наибольшая длина строки
     * @param alphabet - строка, содержащая символы, которые могут использоваться в строках файла
     */
    public void generateFile(String fileName, int linesCount, int maxLineLength, String alphabet) {
        File file = new File(fileName);
        try {
            if (file.exists()) {
                FileUtils.forceDelete(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int chunksCount = (linesCount + CHUNK_SIZE - 1) / CHUNK_SIZE;
        for (int i = 0; i < chunksCount; i++) {
            int chunkSize = Math.min(linesCount - i * CHUNK_SIZE, CHUNK_SIZE);
            List<String> chunk = new ArrayList<>();
            for (int j = 0; j < chunkSize; j++) {
                chunk.add(generateRandomString(maxLineLength, alphabet));
            }
            try {
                FileUtils.writeLines(file, chunk, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateRandomString(int maxLineLength, String alphabet) {
        int length = random.nextInt(maxLineLength) + 1;
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(alphabet.length());
            builder.append(alphabet.charAt(randomIndex));
        }
        return builder.toString();
    }
}
