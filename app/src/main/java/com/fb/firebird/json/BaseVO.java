package com.fb.firebird.json;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseVO implements Serializable {
    private long id;
    private String gmtCreate;
    private String gmtModified;
    private long userId;
    private long symbolId;

    private String symbolName;
    private String symbolDesc;
    private String symbolIcon;
    private String symbolGroup;
    private int status;
}
