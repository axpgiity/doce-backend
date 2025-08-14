package com.doce_ai.controller;

import com.doce_ai.Repository.DocumentRepository;
import com.doce_ai.Repository.UserRepository;
import com.doce_ai.Security.Services.UserDetailsImpl;
import com.doce_ai.constants.FileConstants;
import com.doce_ai.model.Documents;
import com.doce_ai.model.User;
import com.doce_ai.service.GitHubService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.*;

@RestController
@RequestMapping("/api")
public class InputController {

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;

    private final RestTemplate restTemplate;

    @Autowired
    public InputController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/upload")
    public Map<String, Object> processInput(
            @RequestBody Map<String, String> requestBody,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        Documents savedDoc = null;
        try {

            // Validate input
            String githubLink = requestBody.get("githubRepoLink");

            if (githubLink == null || githubLink.isBlank()) {
                return errorResponse("GitHub repository URL is required");
            }

            // Verify user authentication
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String userId = userDetails.getId();

            // Validate GitHub URL
            if (!gitHubService.isValidURL(githubLink)) {
                return errorResponse("Invalid GitHub URL format");
            }

            // Check if a document already exists for this user+repo
            Documents existingDoc = documentRepository.findByUserIdAndRepositoryUrl(userId, githubLink);
            if (existingDoc != null) {
                return errorResponse("Repository already exists for this user");
            }

            // Clone repository
            String repoPath = gitHubService.cloneRepo(githubLink);
            String repoName = extractRepoName(githubLink);

            // Create document entry
            Documents doc = createDocument(userId, repoName, githubLink, new ArrayList<>());
            savedDoc = documentRepository.save(doc);

            // Call Python API
            Map<String, Object> pythonResponse = callPythonAPI(repoPath);
            List<String> documentationData = extractDocumentationData(pythonResponse);

            // Update a document with processed data
            savedDoc.setData(documentationData);
            documentRepository.save(savedDoc);

            // Update user's document list
            User user = userRepository.findById(userId).orElseThrow();
            user.getDocuments().add(savedDoc.getId());
            userRepository.save(user);

            // Build response
            response.put("status", "success");
            response.put("document", savedDoc);
            return response;

            /*
             Get authenticated user
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String userId = userDetails.getId(); // Ensure User model has getId()
             1. Create Document entry
            Documents doc = new Documents();
            doc.setUserId(userId);
            doc.setTitle("Processed Repository"); // Customize title
            doc.setCreatedAt(new Date());
            doc.setUpdatedAt(new Date());
            Documents savedDoc = documentRepository.save(doc);
             2. Update User's documents array
            User user = userRepository.findById(userId).orElseThrow();
            user.getDocuments().add(savedDoc.getId());
            userRepository.save(user);
             Call the Python API to process the repository
            String pythonApiUrl = FileConstants.SCRIPT_LINK; // Flask server endpoint
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("directory_path", repoPath);
             Send a POST request to the Flask API
            Map<String, Object> pythonResponse = restTemplate.postForObject(pythonApiUrl, requestBody, Map.class);
             Prepare final response
             response.put("repoPath", repoPath);
            response.put("message", "Repository Processed Successfully!");
             Add data from Python API
            if (pythonResponse != null && pythonResponse.containsKey("files")) {
                response.put("data", pythonResponse.get("files"));
            } else {
                response.put("data", new ArrayList<>()); // Default to empty array if no files found
            }
            */

        }
        catch (GitAPIException e) {
            // Cleanup document if created
            if (savedDoc != null) documentRepository.delete(savedDoc);
            return errorResponse("Git clone failed: " + e.getMessage());
        } catch (Exception e) {
            // Cleanup document if created
            if (savedDoc != null) documentRepository.delete(savedDoc);
            return errorResponse("Processing error: " + e.getMessage());
        }
    }

    /**
     * Helper method to create an error response.
     *
     * @param 'message' Error message
     * @return Map containing error response
     */

    // Helper Methods:-

    private String validateAndGetRepoPath(String githubLink, String localPath) {
        try {
            if (githubLink != null) {
                if (!gitHubService.isValidURL(githubLink)) {
                    throw new IllegalArgumentException("Invalid GitHub URL");
                }
                return gitHubService.cloneRepo(githubLink);
            } else {
                if (!gitHubService.isValidLocalPath(localPath)) {
                    throw new IllegalArgumentException("Invalid local path");
                }
                return localPath;
            }
        } catch (Exception e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
    }

    private Map<String, Object> callPythonAPI(String repoPath) {

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                FileConstants.SCRIPT_LINK,
                HttpMethod.POST,
                new HttpEntity<>(Map.of("directory", repoPath)),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        return response.getBody();
    }


    private List<String> extractDocumentationData(Map<String, Object> pythonResponse) {
        List<String> documentationData = new ArrayList<>();

        if (pythonResponse == null || !pythonResponse.containsKey("files")) {
            return documentationData;
        }

        Object filesObj = pythonResponse.get("files");
        if (filesObj instanceof List<?> filesList) {
            for (Object fileObj : filesList) {
                if (fileObj instanceof Map<?, ?> fileMap) {
                    String documentation = (String) fileMap.get("documentation");
                    String filePath = (String) fileMap.get("file_path");
                    // Format as "documentation|||file_path" for storage
                    documentationData.add(documentation + "|||" + filePath);
                }
            }
        }

        return documentationData;
    }

    // Helper method to extract repository name from URL
    private String extractRepoName(String githubUrl) {
        String[] parts = githubUrl.split("/");
        String lastPart = parts[parts.length - 1];
        return lastPart.replace(".git", "");
    }

    private Documents createDocument(String userId, String title, String repositoryUrl, List<String> data) {
        Documents doc = new Documents();
        doc.setUserId(userId);
        doc.setTitle(title);
        doc.setRepositoryUrl(repositoryUrl); // Set repository URL
        doc.setData(data);
        doc.setCreatedAt(new Date());
        doc.setUpdatedAt(new Date());
        return doc;
    }
    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", message);
        return errorResponse;
    }
}
