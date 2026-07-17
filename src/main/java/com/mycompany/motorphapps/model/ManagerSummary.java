package com.mycompany.motorphapps.model;

/**
 * Summary for HR and IT roles.
 * Demonstrates: INHERITANCE + OVERRIDE.
 *
 * - Can VIEW all employees' attendance and total hours.
 * - CANNOT trigger the "Calculate All" payroll function.
 *
 * @author DAYANG GWAPA
 */
public class ManagerSummary extends AttendanceSummary {

    @Override
    public String getSummaryLabel() {
        return "All Employee Attendance Summary";
    }

    @Override
    public boolean canViewSummary(String role) {
        String r = role == null ? "" : role.trim().toUpperCase();
        return r.equals("HR") || r.equals("IT") || r.equals("ADMIN") || r.equals("FINANCE");
    }

    @Override
    public boolean canCalculateAll(String role) {
        // HR and IT can view totals but NOT calculate/process
        String r = role == null ? "" : role.trim().toUpperCase();
        return false;
    }
}
