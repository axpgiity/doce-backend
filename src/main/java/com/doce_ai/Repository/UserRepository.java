package com.doce_ai.Repository;

import java.util.Optional;
import com.doce_ai.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/*
Repository interface for accessing User entities in the MongoDB database
It extends MongoRepository, providing CRUD operations for User Objectives
*/
@Repository
public interface UserRepository extends MongoRepository<User,String> {

    //Find a user by their username
   // Optional<User> findByUsername(String username);

    //Check if a username already exists in the database.
    Boolean existsByUsername(String username);

    // Find a user by their email
    Optional<User> findByEmail(String email);

    //Check if an email already exists in the database.
    Boolean existsByEmail(String email);

    //String getbyEmail(String email);

    //check if both username and email exists in the database.
    @Query("{ '$or': [ { 'username': ?0 }, { 'email': ?0 } ] }")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    // Find user by BOTH username and email
    @Query("{ 'username': ?0, 'email': ?1 }")
    Optional<User> findByUsernameAndEmail(String username, String email);
}
