package com.project.casualtalkchat.register_page;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAlreadyExistExceptionTest {

    public static final String EXCEPTION_MESSAGE = "User with name Anna already exists.";

    @Test
    void shouldThrowExceptionWithGivenMessage() {
        //Given
        UserAlreadyExistException userAlreadyExistException = new UserAlreadyExistException(EXCEPTION_MESSAGE);

        //When

        //Then
        assertEquals(EXCEPTION_MESSAGE, userAlreadyExistException.getMessage());
    }
}