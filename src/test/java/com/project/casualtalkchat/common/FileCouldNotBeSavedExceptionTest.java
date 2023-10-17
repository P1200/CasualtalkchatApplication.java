package com.project.casualtalkchat.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileCouldNotBeSavedExceptionTest {

    private static final String FILE_PATH = "/images/users/avatars/aea2bac3c4933907e6be863efccd01b9.jpeg";
    private static final String EXCEPTION_MESSAGE = "File couldn't be saved in " + FILE_PATH + ".";

    @Test
    void shouldGetProperExceptionMessage() {
        //Given
        FileCouldNotBeSavedException userAlreadyExistException = new FileCouldNotBeSavedException(FILE_PATH);

        //When

        //Then
        assertEquals(EXCEPTION_MESSAGE, userAlreadyExistException.getMessage());
    }

}