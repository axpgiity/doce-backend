package com.doce_ai.Repository;

import com.doce_ai.model.Documents;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<Documents, String> {

    // Find all documents by user ID
    List<Documents> findByUserId(String userId);

    // Find document by title and user ID
    Documents findByTitleAndUserId(String title, String userId);

    // Find documents by title containing keyword
    List<Documents> findByTitleContaining(String keyword);

    // Find documents by title containing keyword for a specific user
    List<Documents> findByTitleContainingAndUserId(String keyword, String userId);

    // Find documents created after a certain date
    List<Documents> findByCreatedAtAfter(Date date);

    // Find documents updated after a certain date
    List<Documents> findByUpdatedAtAfter(Date date);

    // New method to find by repository URL
    List<Documents> findByRepositoryUrl(String repositoryUrl);

    // Find by user ID and repository URL
    Documents findByUserIdAndRepositoryUrl(String userId, String repositoryUrl);

    // Find by repository URL containing keyword
    List<Documents> findByRepositoryUrlContaining(String keyword);

    // Count documents by user ID
    long countByUserId(String userId);

    // Delete all documents by user ID
    void deleteByUserId(String userId);

    //Find all
    List<Documents> findAll();

    @Query("{'userId': ?0, 'createdAt': {$gt: ?1}}")
    List<Documents> findUserDocumentsCreatedAfter(String userId, Date date);
}
