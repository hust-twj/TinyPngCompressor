package com.husttwj.imagecompress.model;


import java.util.List;

public class ProjectConfig {

    private String tinyUrl;

    private List<String> tinyHeadName;

    private List<String> tinyHeadValue;


    public List<String> getTinyHeadName() {
        return tinyHeadName;
    }

    public List<String> getTinyHeadValue() {
        return tinyHeadValue;
    }

    public String getTinyUrl() {
        if (tinyUrl == null || tinyUrl.isEmpty()) {
            tinyUrl = "https://tinypng.com/backend/opt/shrink";
        }
        return tinyUrl;
    }


}
