package com.project.casualtalkchat.common;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;

public class PasswordValidator {

    private boolean enablePasswordValidation;
    private String mainPasswordFieldValue;

    public ValidationResult validatePassword(String password, ValueContext ctx) {

        this.mainPasswordFieldValue = password;

        if (password == null || password.length() < 8) {
            return ValidationResult.error("Password should be at least 8 characters long");
        } else if (!password.matches(".*\\d.*")) {
            return ValidationResult.error("Password should contains at least one digit");
        } else if (!password.matches(".*[A-Z].*")) {
            return ValidationResult.error("Password should contains at least one uppercase letter");
        } else if (!password.matches(".*[a-z].*")) {
            return ValidationResult.error("Password should be at least one lowercase letter");
        } else if (!password.matches(".*[#?!@$%^&*-].*")) {
            return ValidationResult.error("Password should contains at least one special character");
        }

        if (!enablePasswordValidation) {
            enablePasswordValidation = true;
        }
        return ValidationResult.ok();
    }

    public ValidationResult validateRepeatedPassword(String repeatedPassword, ValueContext ctx) {

        String password = mainPasswordFieldValue;

        if (repeatedPassword != null && repeatedPassword.equals(password)) {
            return ValidationResult.ok();
        }

        return ValidationResult.error("Passwords do not match");
    }
}
