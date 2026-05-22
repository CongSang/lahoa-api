package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByActivationToken(String activationToken);

    boolean existsByEmail(String email);

    @Query("""
        SELECT u
        FROM UserEntity u
        WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY u.fullName
    """)
    List<UserEntity> findByKeyword(
            String keyword
    );
}
