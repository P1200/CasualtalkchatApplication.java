package com.project.casualtalkchat.register_page;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceExceptionTest {

    public static final String EXCEPTION_MESSAGE = "Something went wrong.";

    @Test
    void shouldThrowExceptionWithGivenMessage() {
        //Given
        ServiceException serviceException = new ServiceException(EXCEPTION_MESSAGE);

        //When

        //Then
        assertEquals(EXCEPTION_MESSAGE, serviceException.getMessage());
    }
}