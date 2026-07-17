package com.mycompany.motorphapps.model;

public class AttendanceSummaryFactory {

    public static AttendanceSummary forRole(String role) {
        if (role == null) return new EmployeeSummary();
        switch (role.trim().toUpperCase()) {
            case "ADMIN":
            case "FINANCE":
                return new FinanceAdminSummary();
            case "HR":
            case "IT":
                return new ManagerSummary();
            default:
                return new EmployeeSummary();
        }
    }
}