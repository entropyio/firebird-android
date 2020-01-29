package com.fb.firebird.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class LoginData implements Serializable {
    private long userId;
    private long ts;
    private String token;
}
