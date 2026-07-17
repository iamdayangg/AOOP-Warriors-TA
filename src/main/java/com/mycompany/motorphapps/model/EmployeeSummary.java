package com.mycompany.motorphapps.model;

/**
 * Summary for regular EMPLOYEE role.
 * Demonstrates: INHERITANCE (extends AttendanceSummary) + OVERRIDE.
 *
 * - Can VIEW their own total hours only.
 * - CANNOT calculate all-employee totals.
 *
 * @author DAYANG GWAPA
 */
public class EmployeeSummary extends AttendanceSummary {

    @Override
    public String getSummaryLabel() {
        return "My Attendance Summary";
    }

    @Override
    public boolean canViewSummary(String role) {
        // Every role can see the summary panel
        return true;
    }

    @Override
    public boolean canCalculateAll(String role) {
        // Regular employees cannot calculate all-employee totals
        return false;
    }
}
