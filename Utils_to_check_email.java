package com.example.carati;

public class Utils_to_check_email {
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    // Method to validate email
    public static boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_PATTERN);
    }
}
