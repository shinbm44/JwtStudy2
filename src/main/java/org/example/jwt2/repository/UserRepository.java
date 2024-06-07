package org.example.jwt2.repository;

import org.example.jwt2.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByUsername(String username);
    UserEntity findByUsername(String username);

}
