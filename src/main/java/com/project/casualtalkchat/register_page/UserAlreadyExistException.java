package com.project.casualtalkchat.register_page;

public class UserAlreadyExistException extends ServiceException {

    public UserAlreadyExistException(String message) {
        super(message);
    }
}
