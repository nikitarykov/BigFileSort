package ru.example;

import com.google.common.collect.TreeMultimap;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FileSorter {

    private static final int MAX_CHUNK_SIZE = 100000;

    /**
     * Метод разбивает входной файл на части размера {@value #MAX_CHUNK_SIZE},
     * сортирует их и записывает во временные файлы во временную директорию
     *
     * @param fileName - название файла со строками для сортировки
     * @param tempDir - название директории, в которую будут складываться отсортированные части исходного файла
     */
    public void createAndSortChunks(String fileName, String tempDir) {
        File file = new File(tempDir);
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int chunkIndex = 0;
        List<String> chunk = new ArrayList<>();
        try (FileInputStream inputStream = new FileInputStream(fileName);
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                chunk.add(line);
                if (chunk.size() == MAX_CHUNK_SIZE) {
                    sortAndWriteToFile(chunk, tempDir, chunkIndex);
                    chunkIndex++;
                    chunk = new ArrayList<>();
                }
            }
            if (!chunk.isEmpty()) {
                sortAndWriteToFile(chunk, tempDir, chunkIndex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sortAndWriteToFile(List<String> chunk, String tempDir, int index) {
        StringBuilder builder = new StringBuilder()
                .append(tempDir)
                .append(File.separator)
                .append("chunk_")
                .append(index)
                .append(".txt");
        String fileName = builder.toString();
        File file = new File(fileName);
        chunk.sort(String::compareTo);
        try {
            FileUtils.writeLines(file, chunk);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Метод мерджит отсортированные файлы из временной директории в один результирующий файл
     *
     * @param tempDir - название временной директории, которая содержит
     * @param outputFileName
     */
    public void mergeChunks(String tempDir, String outputFileName) {
        File file = new File(outputFileName);
        try {
            if (file.exists()) {
                FileUtils.forceDelete(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Scanner> scanners = new ArrayList<>();
        TreeMultimap<String, Integer> tree = TreeMultimap.create();
        try {
            Files.list(Paths.get(tempDir))
                    .forEach(filePath -> {
                        try {
                            Scanner scanner = new Scanner(filePath, StandardCharsets.UTF_8);
                            scanners.add(scanner);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            for (int i = 0; i < scanners.size(); i++) {
                Scanner scanner = scanners.get(i);
                if (scanner.hasNextLine()) {
                    tree.put(scanner.nextLine(), i);
                }
            }
            List<String> chunk = new ArrayList<>();
            while (!tree.isEmpty()) {
                Map.Entry<String, Integer> minEntry = tree.entries().iterator().next();
                chunk.add(minEntry.getKey());
                tree.remove(minEntry.getKey(), minEntry.getValue());
                Scanner scanner = scanners.get(minEntry.getValue());
                if (scanner.hasNextLine()) {
                    tree.put(scanner.nextLine(), minEntry.getValue());
                }
                if (chunk.size() == MAX_CHUNK_SIZE) {
                    FileUtils.writeLines(file, chunk, true);
                    chunk = new ArrayList<>();
                }
            }
            if (!chunk.isEmpty()) {
                FileUtils.writeLines(file, chunk, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanners.forEach(Scanner::close);
        }
        try {
            FileUtils.deleteDirectory(new File(tempDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
