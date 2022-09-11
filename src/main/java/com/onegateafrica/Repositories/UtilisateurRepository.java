package com.onegateafrica.Repositories;

import com.onegateafrica.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String username);
    Optional<User> findByEmail(String email);

    Boolean existsByUserName(String username);

    Boolean existsByEmail(String email);
}
