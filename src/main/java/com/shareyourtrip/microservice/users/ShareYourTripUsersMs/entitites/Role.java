package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "roles",
        schema = "public",
        uniqueConstraints = {
                @UniqueConstraint(name = "roles_name_key", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "\"name\"", length = 50, nullable = false, unique = true)
    private String name;

    @Builder.Default
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonIgnore // evita recursión infinita en APIs REST
    private Set<User> users = new HashSet<>();
}

