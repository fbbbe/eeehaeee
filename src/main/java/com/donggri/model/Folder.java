package com.donggri.model;

public class Folder {
    private Integer folderId;
    private String name;
    private String createdAt;

    public Folder(Integer folderId, String name, String createdAt) {
        this.folderId = folderId;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Integer getFolderId() {
        return folderId;
    }

    public String getName() {
        return name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }
}