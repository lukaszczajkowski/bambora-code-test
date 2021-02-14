package com.bambora.code.test.domain;

import io.norberg.automatter.AutoMatter;

@AutoMatter
public interface Result {

    String signature();
    String uuid();
    String method();
    Object data();
}
