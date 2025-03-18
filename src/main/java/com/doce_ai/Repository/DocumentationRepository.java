package com.doce_ai.Repository;

import com.doce_ai.model.Documentation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentationRepository extends MongoRepository<Documentation, String> {
    //Documentation findByRepoPath(String repoPath);
}
