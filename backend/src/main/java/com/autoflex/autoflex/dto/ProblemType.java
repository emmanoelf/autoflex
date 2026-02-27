package com.autoflex.autoflex.dto;

import lombok.Getter;

@Getter
public enum ProblemType {
    RESOURCE_NOT_FOUND("/resource-not-found", "Resource not found"),
    INVALID_DATA("/invalid-data", "Invalid data");

    private String title;
    private String uri;

    ProblemType(String path, String title){
        this.uri = "https://autoflex.com.br" + path;
        this.title = title;
    }
}
