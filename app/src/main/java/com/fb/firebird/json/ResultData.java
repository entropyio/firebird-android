package com.fb.firebird.json;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ResultData<T> implements Serializable {
    private int retCode;
    private String message;

    private T data;
    private List<T> dataList;

    private int pageNumber;
    private int totalCount;
}
