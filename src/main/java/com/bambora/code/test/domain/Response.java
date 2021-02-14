package com.bambora.code.test.domain;

import io.norberg.automatter.AutoMatter;

@AutoMatter
public interface Response {
    String version();
    Result result();
    Error error();
}
