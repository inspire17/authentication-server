package com.inspire17.auth.exceptions;

import lombok.Getter;

@Getter
public class ServerRequestFailed  extends RuntimeException  {
    private final String message;
    private final int code;
    public ServerRequestFailed(String message, int code) {
        this.message = message;
        this.code = code;
    }
}