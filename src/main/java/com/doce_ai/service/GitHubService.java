package com.doce_ai.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

@Service
public class GitHubService {

    public String cloneRepo(String repoUrl) throws GitAPIException {
        String tempDir = System.getProperty("java.io.tmpdir"); // Temp directory
        String uniqueRepoDir = tempDir + "cloned-repo-" + UUID.randomUUID();//Change this to repo-name

        Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(new File(uniqueRepoDir))
                .call();

        return uniqueRepoDir;
    }

    public boolean isValidURL(String url) {
        return url.startsWith("https://github.com/");
    }

    public boolean isValidLocalPath(String localPath) {
        File directory = new File(localPath);
        return directory.exists() && directory.isDirectory();
    }
}
