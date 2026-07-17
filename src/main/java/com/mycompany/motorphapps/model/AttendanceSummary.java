package com.mycompany.motorphapps.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class AttendanceSummary {

    protected static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public abstract String getSummaryLabel();
    public abstract boolean canViewSummary(String role);
    public abstract boolean canCalculateAll(String role);

    // Overload 1: LocalTime → LocalTime
    public double computeHours(LocalTime timeIn, LocalTime timeOut) {
        if (timeIn == null || timeOut == null) return 0.0;
        long minutes = java.time.Duration.between(timeIn, timeOut).toMinutes();
        return minutes < 0 ? 0.0 : minutes / 60.0;
    }

    // Overload 2: String → String  (handles both HH:mm and HH:mm:ss)
    public double computeHours(String timeInStr, String timeOutStr) {
        try {
            if (timeInStr == null || timeInStr.isBlank()) return 0.0;
            if (timeOutStr == null || timeOutStr.isBlank()) return 0.0;
            LocalTime in  = LocalTime.parse(normalizeTime(timeInStr), TIME_FMT);
            LocalTime out = LocalTime.parse(normalizeTime(timeOutStr), TIME_FMT);
            return computeHours(in, out);
        } catch (Exception e) {
            return 0.0;
        }
    }

    // Overload 3: single employee across all rows
    public double computeHours(String employeeId, List<String[]> attendanceRows) {
        double total = 0.0;
        for (String[] row : attendanceRows) {
            if (row.length < 5) continue;
            if (!row[0].trim().equals(employeeId)) continue;
            total += computeHours(row[3], row[4]);
        }
        return total;
    }

    // Overload 4: all employees across all rows
    public double computeHours(List<String[]> attendanceRows) {
        double total = 0.0;
        for (String[] row : attendanceRows) {
            if (row.length < 5) continue;
            total += computeHours(row[3], row[4]);
        }
        return total;
    }

    public String formatHours(double totalHours) {
        int h = (int) totalHours;
        int m = (int) Math.round((totalHours - h) * 60);
        return h + "h " + m + "m";
    }

    // ── Strips seconds so HH:mm:ss → HH:mm before parsing ──────────────────
    private String normalizeTime(String raw) {
        String t = raw.trim();
        // HH:mm:ss  →  HH:mm
        if (t.matches("\\d{2}:\\d{2}:\\d{2}")) return t.substring(0, 5);
        return t;
    }
}
