package com.lahoa.lahoa_be.service;

import com.lahoa.lahoa_be.common.enums.CodePrefix;

public interface CodeGeneratorService {

    String next(CodePrefix prefix);

    String nextWithDate(CodePrefix prefix);
}
