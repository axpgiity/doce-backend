package com.doce_ai.controller;

import com.doce_ai.constants.FileConstants;
import com.doce_ai.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InputController {

    @Autowired
    private GitHubService gitHubService;

    @PostMapping("/upload")
    public Map<String, Object> processInput(
            @RequestParam(required = false) String githubLink,
            @RequestParam(required = false) String localPath) {

        Map<String, Object> response = new HashMap<>();
        try {
            // Validate input
            if (githubLink == null && localPath == null) {
                return errorResponse("Please provide either a GitHub link or a local path.");
            }

            String repoPath = null;
            if (githubLink != null) {
                if (!gitHubService.isValidURL(githubLink)) {
                    return errorResponse("Invalid GitHub URL.");
                }
                repoPath = gitHubService.cloneRepo(githubLink);
            } else {
                if (gitHubService.isValidLocalPath(localPath)) {
                    repoPath = localPath;
                } else {
                    return errorResponse("Invalid local path or directory does not exist: " + localPath);
                }
            }

            // Call the Python API to process the repository
            String pythonApiUrl = FileConstants.SCRIPT_LINK; // Flask server endpoint
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("directory_path", repoPath);

            // Send a POST request to the Flask API
            Map<String, Object> pythonResponse = restTemplate.postForObject(pythonApiUrl, requestBody, Map.class);

            // Prepare final response
            response.put("repoPath", repoPath);
            response.put("message", "Repository Processed Successfully!");

            // Add data from Python API
            if (pythonResponse != null && pythonResponse.containsKey("files")) {
                response.put("data", pythonResponse.get("files"));
            } else {
                response.put("data", new ArrayList<>()); // Default to empty array if no files found
            }

            return response;

        } catch (Exception e) {
            return errorResponse("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Helper method to create an error response.
     *
     * @param message Error message
     * @return Map containing error response
     */
    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", message);
        return errorResponse;
    }
}
