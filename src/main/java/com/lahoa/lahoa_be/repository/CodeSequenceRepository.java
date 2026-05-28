package com.lahoa.lahoa_be.repository;

import com.lahoa.lahoa_be.entity.CodeSequenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeSequenceRepository
        extends JpaRepository<CodeSequenceEntity, String> {
}
