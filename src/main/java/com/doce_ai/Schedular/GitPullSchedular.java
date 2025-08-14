package com.doce_ai.Schedular;

import com.doce_ai.Repository.DocumentRepository;
import com.doce_ai.model.Documents;
import com.doce_ai.service.GitHubService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//       logic:-
//         1. Get the repo URL from database
//         2. Clone the repo to a temporary directory
//         3. Pull the latest changes from the remote repository
//         4. Save the changes to database
//         5. Delete the temporary directory
//         6. Return the response
//         7. Handle any errors that occur during the process
//         8. Log the success or failure of the process
//
//       Code:-

@Component
public class GitPullSchedular {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private GitHubService gitHubService;

    // Track cloned repositories <DocumentID, RepoPath>
    private final Map<String, String> repoPathMap = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(GitPullSchedular.class);


    @Scheduled(cron = "0 * * * * *") // Every minute
    public void scheduledPullTask() {
        log.info("Starting Git Pull Scheduler");

        List<Documents> allDocuments = documentRepository.findAll();
        log.info("Processing {} documents", allDocuments.size());

        for (Documents doc : allDocuments) {
            try {
                // Use repositoryUrl instead of title
                String repoUrl = doc.getRepositoryUrl();
                if (repoUrl == null || repoUrl.isBlank()) {
                    log.warn("Document {} has no repository URL", doc.getId());
                    continue;
                }

                String repoPath = getOrCloneRepo(doc.getId(), repoUrl);
                boolean hasChanges = gitHubService.pullRepo(new File(repoPath));

                if (hasChanges) {
                    log.info("Changes detected in {}", repoUrl);
                    processDocument(doc, repoPath);
                } else {
                    log.info("No changes in {}", repoUrl);
                }
            } catch (Exception e) {
                log.error("Error processing document {}: {}", doc.getId(), e.getMessage(), e);
            }
        }
        log.info("Git Pull Scheduler completed");
    }


    private String getOrCloneRepo(String docId, String repoUrl)
            throws GitAPIException {
        // Check if already cloned
        if(repoPathMap.containsKey(docId)) {
            return repoPathMap.get(docId);
        }

        // Clone new repo
        String repoPath = gitHubService.cloneRepo(repoUrl);
        repoPathMap.put(docId, repoPath);
        return repoPath;
    }

    private void processDocument(Documents doc, String repoPath) {
        // Call Python API to generate documentation
        Map<String, Object> response = gitHubService.generateDocumentation(new File(repoPath));

        if(response == null || response.isEmpty()) {
            log.error("Failed to generate documentation for {}", doc.getId());
            return;
        }

        // Update document with new data
        List<String> documentationData = (List<String>) response.get("documentation");
        doc.setData(documentationData);
        doc.setUpdatedAt(new java.util.Date());

        documentRepository.save(doc);
        log.info("Updated document {} with new data", doc.getId());
    }

}
