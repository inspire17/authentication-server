package com.inspire17.auth.model;

import lombok.Getter;

@Getter
public enum UserRole {
    USERS("USERS"),
    ADMIN("ADMIN");

    private final String user;

    UserRole(String user) {
        this.user = user;
    }

}
