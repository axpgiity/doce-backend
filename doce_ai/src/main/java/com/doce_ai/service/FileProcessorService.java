package com.doce_ai.service;

import com.doce_ai.constants.FileConstants;
import com.doce_ai.util.JsonUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Service
public class FileProcessorService {

    public String processRepository(String repoPath) throws IOException {
        List<String> validFiles = new ArrayList<>();
        Set<String> invalidExtensions = new HashSet<>(Arrays.asList(FileConstants.INVALID_FILE_EXTENSIONS.split(",")));
        Set<String> excludedFolders = new HashSet<>(Arrays.asList(FileConstants.EXCLUDED_FOLDERS.split(",")));

        // Walk through files and skip .git folder
        try (Stream<Path> paths = Files.walk(Paths.get(repoPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(filePath -> excludedFolders.stream().noneMatch(folder -> filePath.toString().contains(folder)))
                    .filter(filePath -> invalidExtensions.stream().noneMatch(filePath.toString().toLowerCase()::endsWith))
                    .forEach(filePath -> validFiles.add(filePath.toString()));
        }

        // Map to store file content
        Map<String, String> fileContentMap = new HashMap<>();

        // Parallel processing to read files more efficiently
        validFiles.parallelStream().forEach(filePath -> {
            try {
                Path path = Paths.get(filePath);
                if (!Files.exists(path)) {
                    System.err.println("File does not exist: " + filePath);
                    return;
                }

                long fileSize = Files.size(path);
                if (fileSize > 10 * 1024 * 1024) { // Skip files larger than 10 MB
                    System.out.println("Skipping large file: " + filePath);
                    return;
                }

                String content = Files.readString(path, StandardCharsets.UTF_8);
                fileContentMap.put(filePath, content);
            } catch (IOException e) {
                System.err.println("Failed to read file: " + filePath + " - Skipping. Error: " + e.getMessage());
            }
        });

        // Return the structured response in JSON format
        Map<String, Object> response = new HashMap<>();
        response.put("analysis", fileContentMap); // File content analysis

        return JsonUtil.convertMapToJson(response);
    }
}
