package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<Role, Short> {
    Optional<Role> findByName(String name);
}
