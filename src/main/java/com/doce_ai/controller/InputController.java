package com.doce_ai.controller;

import com.doce_ai.service.FileProcessorService;
import com.doce_ai.service.GitHubService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InputController {

    @Autowired
    private FileProcessorService fileProcessorService;
    @Autowired
    private GitHubService gitHubService;

    @PostMapping("/upload")
    public Map<String, String> processInput(
            @RequestParam(required = false) String githubLink,
            @RequestParam(required = false) String localPath) {

        Map<String, String> response = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        if (githubLink == null && localPath == null) {
            response.put("status", "error");
            response.put("message", "Please provide either a GitHub link or a local path.");
            return response;
        }

        try {
            String repoPath = null;
            if (githubLink != null) {
                if (!gitHubService.isValidURL(githubLink)) {
                    response.put("status", "error");
                    response.put("message", "Invalid GitHub URL.");
                    return response;
                }
                repoPath = gitHubService.cloneRepo(githubLink);
                /*response.put("status", "success");
                response.put("message", "Repository cloned successfully to: " + repoPath);
                response.put("analysis",  objectMapper.writeValueAsString(runPythonScript(clonedRepoPath)));*/
            } else {
                if (gitHubService.isValidLocalPath(localPath)) {
                    repoPath = localPath;
                    /*response.put("status", "success");
                    response.put("message", "Local repository found and ready for processing: " + repoPath);
                    response.put("analysis", objectMapper.writeValueAsString(runPythonScript(repoPath)));*/
                } else {
                    response.put("status", "error");
                    response.put("message", "Invalid local path or directory does not exist: " + localPath);
                }
            }

            // Now, instead of making an HTTP call, directly call processRepository
            return processRepositoryInternal(repoPath);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred: " + e.getMessage());
            return response;
        }
    }

    public Map<String,String> processRepositoryInternal(String repoPath) {
        Map<String, String> response = new HashMap<>();
        try {
            // Call the file processor service to handle the processing of the repo
            String jsonResponse = fileProcessorService.processRepository(repoPath);

            // Put the response in the map to match the expected return type
            response.put("message", "Repository processed successfully.");
            response.put("base_path", repoPath); // Adding the base path for clarity
            response.put("status", "success");
            response.put("data", jsonResponse);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred: " + e.getMessage());
        }
        return response;
    }



    /*private HashMap<String, Object> runPythonScript(String directoryPath) throws IOException {
        HashMap<String, Object>analysisResult = new HashMap<>();
        ProcessBuilder processBuilder = new ProcessBuilder(
                "python",
                "C:\\My_Projects\\CoDoc_Application\\doce_scripts_py\\json_parser.py",
                directoryPath);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        try(BufferedReader reader=new BufferedReader(new java.io.InputStreamReader(process.getInputStream()))){
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line.trim());
            }
            analysisResult.put("status", "success");
            analysisResult.put("message", "Analysis completed successfully");
            analysisResult.put("result", output.toString());
        } catch (Exception e) {
            analysisResult.put("status", "error");
            analysisResult.put("message", e.getMessage());
        }

        return analysisResult;
    }*/

}
