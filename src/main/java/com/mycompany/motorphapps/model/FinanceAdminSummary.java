package com.mycompany.motorphapps.model;

/**
 * Summary for FINANCE and ADMIN roles.
 * Demonstrates: INHERITANCE + OVERRIDE.
 *
 * - Can VIEW all employees' total hours.
 * - CAN calculate all-employee total hours worked (for payroll use).
 * - Overrides getSummaryLabel() to show a more detailed heading.
 *
 * @author DAYANG GWAPA
 */
public class FinanceAdminSummary extends AttendanceSummary {

    @Override
    public String getSummaryLabel() {
        return "Total Hours Worked — All Employees";
    }

    @Override
    public boolean canViewSummary(String role) {
        String r = role == null ? "" : role.trim().toUpperCase();
        return r.equals("FINANCE") || r.equals("ADMIN");
    }

    @Override
    public boolean canCalculateAll(String role) {
        // Finance and Admin are allowed to calculate all-employee totals
        String r = role == null ? "" : role.trim().toUpperCase();
        return r.equals("FINANCE") || r.equals("ADMIN");
    }
}
