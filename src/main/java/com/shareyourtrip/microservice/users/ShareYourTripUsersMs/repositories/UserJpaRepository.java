package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"roles"})
    List<User> findAll();

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findById(Long id);

    @Query("Select u.id from User u where u.language = ?1")
    List<Long> getUserIdsByLanguage(String language);

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
