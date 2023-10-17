package com.project.casualtalkchat.common;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @ParameterizedTest
    @MethodSource("providePasswordForValidation")
    void shouldReturnValidationError(String password, String validationErrorMessage) {
        //Given
        PasswordValidator validator = new PasswordValidator();

        //When
        ValidationResult validationResult = validator.validatePassword(password, new ValueContext());

        //Then
        assertTrue(validationResult.isError(), "There should be a validation error.");
        assertEquals(validationErrorMessage, validationResult.getErrorMessage());
    }

    @Test
    void shouldNotReturnValidationError() {
        //Given
        PasswordValidator validator = new PasswordValidator();

        //When
        ValidationResult validationResult = validator.validatePassword("Admin123*", new ValueContext());

        //Then
        assertFalse(validationResult.isError(), "There should not be a validation error.");
    }

    @Test
    void shouldReturnValidationErrorWhenRepeatedPasswordDoesNotMatch() {
        //Given
        PasswordValidator validator = new PasswordValidator();
        ReflectionTestUtils.setField(validator, "mainPasswordFieldValue", "Admin123*");

        //When
        ValidationResult validationResult =
                validator.validateRepeatedPassword("Admin123**", new ValueContext());

        //Then
        assertTrue(validationResult.isError(), "There should be a validation error.");
        assertEquals("Passwords do not match", validationResult.getErrorMessage());
    }

    @Test
    void shouldNotReturnValidationErrorWhenRepeatedPasswordMatches() {
        //Given
        PasswordValidator validator = new PasswordValidator();
        ReflectionTestUtils.setField(validator, "mainPasswordFieldValue", "Admin123*");

        //When
        ValidationResult validationResult =
                validator.validateRepeatedPassword("Admin123*", new ValueContext());

        //Then
        assertFalse(validationResult.isError(), "There should not be a validation error.");
    }

    private static Stream<Arguments> providePasswordForValidation() {
        return Stream.of(
                Arguments.of("Admin12", "Password should be at least 8 characters long"),
                Arguments.of("AdminAdmin", "Password should contains at least one digit"),
                Arguments.of("admin123", "Password should contains at least one uppercase letter"),
                Arguments.of("ADMIN123", "Password should be at least one lowercase letter"),
                Arguments.of("Admin123", "Password should contains at least one special character")
        );
    }
}