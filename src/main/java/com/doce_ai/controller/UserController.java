package com.doce_ai.controller;


import com.doce_ai.Repository.DocumentRepository;
import com.doce_ai.Repository.UserRepository;
import com.doce_ai.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;

    // 1. Get all document IDs by user ID
    @GetMapping("/{userId}/documents")
    public ResponseEntity<?> getAllDocumentIdsByUser(@PathVariable String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<String> documentIds = userOptional.get().getDocuments();
        return ResponseEntity.ok(Collections.singletonMap("documentIds", documentIds));
    }

    // 2. Delete user by user ID
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable String userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(userId);
        return ResponseEntity.ok().body("User deleted successfully");
    }

    // 3. Delete all documents by user ID
    @DeleteMapping("/{userId}/documents")
    public ResponseEntity<?> deleteAllDocumentsByUser(@PathVariable String userId) {
        // Delete it from a Documents collection
        documentRepository.deleteByUserId(userId);

        // Clear documents list in User
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.getDocuments().clear();
            userRepository.save(user);
        }
        return ResponseEntity.ok().body("All documents deleted for user");
    }

    // 4. Delete document by document ID
    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<?> deleteDocumentById(@PathVariable String documentId) {
        // Remove from a Documents collection
        documentRepository.deleteById(documentId);

        // Remove from all users' document lists
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getDocuments().contains(documentId)) {
                user.getDocuments().remove(documentId);
                userRepository.save(user);
            }
        }
        return ResponseEntity.ok().body("Document deleted successfully");
    }
}
