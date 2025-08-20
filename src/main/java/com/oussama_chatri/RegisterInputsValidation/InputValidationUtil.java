package com.oussama_chatri.RegisterInputsValidation;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

public class InputValidationUtil {

    // Regex for phone number (e.g., +12345678901, 123-456-7890, or (123) 456-7890)
    private static final String PHONE_REGEX = "^(\\+\\d{1,3}[- ]?)?\\d{3}[- ]?\\d{3}[- ]?\\d{4}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    // Regex for password (at least 8 characters, 1 uppercase, 1 lowercase, 1 digit, 1 special character)
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    // Minimum age for users (e.g., 13 for social media compliance)
    private static final int MINIMUM_AGE = 13;

    /**
     * Validates a RegisterRequest object and throws IllegalArgumentException for invalid inputs.
     *
     * @param request The RegisterRequest to validate
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateRegisterRequest(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("RegisterRequest cannot be null");
        }

        validatePassword(request.getPassword());
        validatePhoneNumber(request.getPhoneNumber());
        validateBirthDate(request.getBirthDate());
    }

    /**
     * Validates password strength.
     */
    private static void validatePassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long, contain at least one uppercase letter, " +
                            "one lowercase letter, one digit, and one special character (@$!%*?&)");
        }
    }

    /**
     * Validates phone number format.
     */
    private static void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isBlank() && !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("Invalid phone number format. Use formats like +12345678901 or 123-456-7890");
        }
    }

    /**
     * Validates birth date (ensures user is at least MINIMUM_AGE years old).
     */
    private static void validateBirthDate(LocalDateTime birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date is required");
        }
        long ageInYears = ChronoUnit.YEARS.between(birthDate, LocalDateTime.now());
        if (ageInYears < MINIMUM_AGE) {
            throw new IllegalArgumentException("User must be at least " + MINIMUM_AGE + " years old");
        }
    }

}