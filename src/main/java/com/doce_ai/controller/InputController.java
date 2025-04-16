package com.doce_ai.controller;

import com.doce_ai.Repository.DocumentRepository;
import com.doce_ai.Repository.UserRepository;
import com.doce_ai.Security.Services.UserDetailsImpl;
import com.doce_ai.constants.FileConstants;
import com.doce_ai.model.Documents;
import com.doce_ai.model.User;
import com.doce_ai.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/upload")
    public Map<String, Object> processInput(
            @RequestParam(required = false) String githubLink,
            @RequestParam(required = false) String localPath,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        try {
            // Validate input
            if (githubLink == null && localPath == null) {
                return errorResponse("Please provide either a GitHub link or a local path.");
            }

            // 2. Verify user authentication BEFORE any processing
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String userId = userDetails.getId();

            // 3. Check if user exists in database
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 4. Validate repository path
            String repoPath = validateAndGetRepoPath(githubLink, localPath);
            if (repoPath == null) {
                return errorResponse("Invalid input parameters");
            }

            // 5. Call Python API AFTER verification
            Map<String, Object> pythonResponse = callPythonAPI(repoPath);
            List<String> documentationData = extractDocumentationData(pythonResponse);

            // 6. Create document with Python data
            Documents doc = createDocument(userId, documentationData);
            Documents savedDoc = documentRepository.save(doc);

            // 7. Update user's document lis
            user.getDocuments().add(savedDoc.getId());
            userRepository.save(user);

            // 8. Build success response
            response.put("status", "success");
            response.put("documentId", savedDoc.getId());
            response.put("data", documentationData);

            return response;

            // Get authenticated user
            //UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            //String userId = userDetails.getId(); // Ensure User model has getId()

            // 1. Create Document entry
            //Documents doc = new Documents();
            //doc.setUserId(userId);
            //doc.setTitle("Processed Repository"); // Customize title

            //doc.setCreatedAt(new Date());
            //doc.setUpdatedAt(new Date());
            //Documents savedDoc = documentRepository.save(doc);

            // 2. Update User's documents array
            //User user = userRepository.findById(userId).orElseThrow();
            //user.getDocuments().add(savedDoc.getId());
            //userRepository.save(user);

            // Call the Python API to process the repository
            //String pythonApiUrl = FileConstants.SCRIPT_LINK; // Flask server endpoint
            //RestTemplate restTemplate = new RestTemplate();

            //Map<String, Object> requestBody = new HashMap<>();
            //requestBody.put("directory_path", repoPath);

            // Send a POST request to the Flask API
            //Map<String, Object> pythonResponse = restTemplate.postForObject(pythonApiUrl, requestBody, Map.class);

            // Prepare final response
            // response.put("repoPath", repoPath);
            //response.put("message", "Repository Processed Successfully!");

            // Add data from Python API
            //if (pythonResponse != null && pythonResponse.containsKey("files")) {
            //    response.put("data", pythonResponse.get("files"));
            //} else {
            //    response.put("data", new ArrayList<>()); // Default to empty array if no files found
            //}

        } catch (RuntimeException e) {
            return errorResponse("Access denied: " + e.getMessage());
        } catch (Exception e) {
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
                    return null;
                }
                return gitHubService.cloneRepo(githubLink);
            } else {
                return gitHubService.isValidLocalPath(localPath) ? localPath : null;
            }
        } catch (Exception e) {
            return errorResponse("An error occurred: " + e.getMessage()).toString();
        }
    }

    private Map<String, Object> callPythonAPI(String repoPath) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(
                FileConstants.SCRIPT_LINK,
                Map.of("directory_path", repoPath),
                Map.class
        );
    }

    private List<String> extractDocumentationData(Map<String, Object> pythonResponse) {
        return pythonResponse != null && pythonResponse.containsKey("files")
                ? (List<String>) pythonResponse.get("files")
                : new ArrayList<>();
    }

    private Documents createDocument(String userId, List<String> data) {
        Documents doc = new Documents();
        doc.setUserId(userId);
        doc.setTitle("Doc_" + System.currentTimeMillis());
        doc.setCreatedAt(new Date());
        doc.setUpdatedAt(new Date());
        doc.setData(data);
        return doc;
    }
    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", message);
        return errorResponse;
    }
}
