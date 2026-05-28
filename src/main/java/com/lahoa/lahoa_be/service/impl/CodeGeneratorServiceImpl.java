package com.lahoa.lahoa_be.service.impl;

import com.lahoa.lahoa_be.common.enums.CodePrefix;
import com.lahoa.lahoa_be.entity.CodeSequenceEntity;
import com.lahoa.lahoa_be.repository.CodeSequenceRepository;
import com.lahoa.lahoa_be.service.CodeGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CodeGeneratorServiceImpl implements CodeGeneratorService {

    private final CodeSequenceRepository repository;

    @Transactional
    public synchronized String next(CodePrefix prefix) {

        CodeSequenceEntity seq =
                repository.findById(prefix.name())
                        .orElseThrow();

        seq.setValue(seq.getValue() + 1);

        repository.save(seq);

        return String.format(
                "%s_%04d",
                prefix.name(),
                seq.getValue()
        );
    }

    @Transactional
    public synchronized String nextWithDate(CodePrefix prefix) {

        CodeSequenceEntity seq =
                repository.findById(prefix.name())
                        .orElseThrow();

        seq.setValue(seq.getValue() + 1);

        repository.save(seq);

        return String.format(
                "%s_%s_%04d",
                prefix.name(),
                LocalDate.now()
                        .format(DateTimeFormatter.BASIC_ISO_DATE),
                seq.getValue()
        );
    }
}
