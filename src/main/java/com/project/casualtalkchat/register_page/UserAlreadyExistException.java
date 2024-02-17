package com.project.casualtalkchat.register_page;

public class UserAlreadyExistException extends ServiceException {

    UserAlreadyExistException(String message) {
        super(message);
    }
}
