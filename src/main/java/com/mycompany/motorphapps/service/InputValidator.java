package com.mycompany.motorphapps.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Central validation utility.
 * All validation logic lives here — services call these methods,
 * panels never touch raw input directly.
 *
 * Throws IllegalArgumentException with a clear message on failure.
 * Returns the cleaned/parsed value on success.
 */
public class InputValidator {

    private static final DateTimeFormatter DATE_FMT  =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter BDAY_FMT1 =
            DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private static final DateTimeFormatter BDAY_FMT2 =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private static final DateTimeFormatter BDAY_FMT3 =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ── String / text ────────────────────────────────────────────────────────

    public static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    public static String requireName(String value, String fieldName) {
        String v = requireNonBlank(value, fieldName);

        if (!v.matches("[\\p{L}\\s\\-.]+")) {
            throw new IllegalArgumentException(
                    fieldName + " must contain letters only.");
        }

        return v;
    }

    // ── Numbers ──────────────────────────────────────────────────────────────

    public static double requirePositiveDouble(String value, String fieldName) {
        requireNonBlank(value, fieldName);

        try {
            double d = Double.parseDouble(value.trim());

            if (d <= 0) {
                throw new IllegalArgumentException(
                        fieldName + " must be greater than 0.");
            }

            return d;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    fieldName + " must be a valid number.");
        }
    }

    public static double requireNonNegativeDouble(String value, String fieldName) {
        requireNonBlank(value, fieldName);

        try {
            double d = Double.parseDouble(value.trim());

            if (d < 0) {
                throw new IllegalArgumentException(
                        fieldName + " cannot be negative.");
            }

            return d;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    fieldName + " must be a valid number.");
        }
    }

    public static int requirePositiveInt(String value, String fieldName) {
        requireNonBlank(value, fieldName);

        try {
            int i = Integer.parseInt(value.trim());

            if (i <= 0) {
                throw new IllegalArgumentException(
                        fieldName + " must be greater than 0.");
            }

            return i;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    fieldName + " must be a whole number.");
        }
    }

    // ── Dates ────────────────────────────────────────────────────────────────

    /**
     * Accepts:
     * MM-dd-yyyy
     * MM/dd/yyyy
     * yyyy-MM-dd
     */
    public static LocalDate requireBirthday(String value, String fieldName) {

        requireNonBlank(value, fieldName);

        String v = value.trim();

        DateTimeFormatter[] formats = {
            BDAY_FMT1,
            BDAY_FMT2,
            BDAY_FMT3
        };

        for (DateTimeFormatter fmt : formats) {
            try {

                LocalDate d = LocalDate.parse(v, fmt);

                if (d.isAfter(LocalDate.now())) {
                    throw new IllegalArgumentException(
                            fieldName + " cannot be in the future.");
                }

                if (d.isBefore(LocalDate.now().minusYears(120))) {
                    throw new IllegalArgumentException(
                            fieldName + " is not a realistic date.");
                }

                return d;

            } catch (DateTimeParseException ignored) {
            }
        }

        throw new IllegalArgumentException(
                fieldName + " must be in MM-DD-YYYY or YYYY-MM-DD format.");
    }

    public static LocalDate requireDate(String value, String fieldName) {

        requireNonBlank(value, fieldName);

        try {
            return LocalDate.parse(value.trim(), DATE_FMT);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    fieldName + " must be in YYYY-MM-DD format.");
        }
    }

    public static void requireDateRange(LocalDate start, LocalDate end) {

        if (end.isBefore(start)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date.");
        }
    }

    public static void requireFutureOrToday(LocalDate date, String fieldName) {

        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be in the past.");
        }
    }

    // ── Government IDs / phone ───────────────────────────────────────────────

    public static String requirePhone(String value) {

        requireNonBlank(value, "Phone Number");

        String v = value.trim();

        // Remove spaces only
        String digits = v.replaceAll("[\\s\\-]", "");

        if (digits.length() < 9 || digits.length() > 12) {
            throw new IllegalArgumentException(
                    "Phone Number format is invalid.");
        }

        if (!digits.matches("\\d+")
                && !digits.matches("\\+\\d+")) {
            throw new IllegalArgumentException(
                    "Phone Number format is invalid.");
        }

        return v;
    }

    public static String requireSSS(String value) {

        requireNonBlank(value, "SSS Number");

        String v = value.trim();

        if (!v.matches("\\d{2}-\\d{7}-\\d")) {
            throw new IllegalArgumentException(
                    "SSS Number must be in ##-#######-# format.");
        }

        return v;
    }

    public static String requirePhilHealth(String value) {

        requireNonBlank(value, "PhilHealth Number");

        String v = value.trim().replaceAll("-", "");

        if (!v.matches("\\d{12}")) {
            throw new IllegalArgumentException(
                    "PhilHealth Number must be 12 digits.");
        }

        return value.trim();
    }

    public static String requireTIN(String value) {

        requireNonBlank(value, "TIN");

        String v = value.trim();

        if (!v.matches("\\d{3}-\\d{3}-\\d{3}(-\\d{3})?")) {
            throw new IllegalArgumentException(
                    "TIN must be in ###-###-###-### format.");
        }

        return v;
    }

    public static String requirePagIbig(String value) {

        requireNonBlank(value, "Pag-IBIG Number");

        String v = value.trim().replaceAll("-", "");

        if (!v.matches("\\d{12}")) {
            throw new IllegalArgumentException(
                    "Pag-IBIG Number must be 12 digits.");
        }

        return value.trim();
    }

    // ── Employee ID ───────────────────────────────────────────────────────────

    public static String requireEmployeeId(String value) {

        requireNonBlank(value, "Employee ID");

        String v = value.trim();

        if (!v.matches("\\d{5}")) {
            throw new IllegalArgumentException(
                    "Employee ID must be exactly 5 digits.");
        }

        return v;
    }

    // ── Hours worked ─────────────────────────────────────────────────────────

    public static double requireHoursWorked(String value) {

        double h = requirePositiveDouble(value, "Hours Worked");

        if (h > 744) {
            throw new IllegalArgumentException(
                    "Hours Worked cannot exceed 744 hours.");
        }

        return h;
    }

    public static double requireHourlyRate(String value) {

        double r = requirePositiveDouble(value, "Rate/Hour");

        if (r < 60.0) {
            throw new IllegalArgumentException(
                    "Rate/Hour must be at least 60.00 (minimum wage).");
        }

        return r;
    }
}