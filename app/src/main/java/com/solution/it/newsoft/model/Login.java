package com.solution.it.newsoft.model;

public class Login {

    private String id;
    private String token;
    private Status status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public Status getStatus() {
        return status;
    }
}
