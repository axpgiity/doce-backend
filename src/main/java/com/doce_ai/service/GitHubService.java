package com.doce_ai.service;

import com.doce_ai.constants.FileConstants;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
public class GitHubService {

    private static final Logger log = LoggerFactory.getLogger(GitHubService.class);

    private final RestTemplate restTemplate;

    @Autowired
    public GitHubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Method to clone a GitHub repository
    public String cloneRepo(String repoUrl) throws GitAPIException {
        String tempDir = System.getProperty("java.io.tmpdir"); // Temp directory
        String uniqueRepoDir = tempDir + "cloned-repo-" + UUID.randomUUID();//Change this to repo-name

        Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(new File(uniqueRepoDir))
                .call();

        return uniqueRepoDir;
    }

    // Method to pull the latest changes from a GitHub repository
    public boolean pullRepo(File repoDir) throws GitAPIException, IOException {
        try (Git git = Git.open(repoDir)) {
            ObjectId oldHead = git.getRepository().resolve("HEAD");

            // Pull changes
            git.pull().setRebase(true).call();

            ObjectId newHead = git.getRepository().resolve("HEAD");
            return !oldHead.equals(newHead);
        }
    }

    // Method to generate documentation using a REST API
    public Map<String, Object> generateDocumentation(File repoDir) {
       //injected rest templated dependency.

        return restTemplate.postForObject(
                FileConstants.SCRIPT_LINK,
                Map.of("directory", repoDir.getAbsolutePath()),
                Map.class
        );
    }

    public boolean deleteLocalRepository(String path) {
        try {
            Path repoDir = Paths.get(path);
            if (Files.exists(repoDir)) {
                deleteDirectoryRecursively(repoDir.toFile());
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("Error deleting repository: {}", e.getMessage());
            return false;
        }
    }

    private void deleteDirectoryRecursively(File directory) throws IOException {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectoryRecursively(file);
                }
            }
        }
        Files.delete(directory.toPath());
    }

    // Method to validate if a local path is valid
    public boolean isValidLocalPath(String localPath) {
        File directory = new File(localPath);
        return directory.exists() && directory.isDirectory();
    }

    public boolean isValidURL(String url) {
        return url.startsWith("https://github.com/");
    }

    public Map<String, Object> errorResponse(String message) {
        return Map.of("error", message);
    }
}
