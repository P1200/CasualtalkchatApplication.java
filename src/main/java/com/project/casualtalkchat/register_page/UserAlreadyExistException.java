package com.project.casualtalkchat.register_page;

class UserAlreadyExistException extends ServiceException {

    UserAlreadyExistException(String message) {
        super(message);
    }
}
