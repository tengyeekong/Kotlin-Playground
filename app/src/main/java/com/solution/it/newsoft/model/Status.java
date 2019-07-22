package com.solution.it.newsoft.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class Status {

    private String code;
    private String message;

    public Status(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
