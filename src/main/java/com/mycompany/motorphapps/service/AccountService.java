package com.mycompany.motorphapps.service;

import com.mycompany.motorphapps.dao.AccountDAO;
import com.mycompany.motorphapps.model.Role;

import java.util.List;

public class AccountService {

    private final AccountDAO dao;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public AccountService() {
        this.dao = new AccountDAO();
    }

    // =========================================================
    // GET ALL ACCOUNTS
    // =========================================================
    public List<String[]> getAllAccounts() {
        return dao.getAllAccounts();
    }

    // =========================================================
    // CHECK IF ACCOUNT EXISTS
    // =========================================================
    public boolean accountExists(String empId) {

        for (String[] acc : dao.getAllAccounts()) {

            if (acc[0].equals(empId)) {
                return true;
            }
        }

        return false;
    }

    // =========================================================
    // VALIDATION
    // =========================================================
    public boolean isValidPassword(String password) {

        return password != null
                && !password.trim().isEmpty();
    }

    public boolean isValidRole(String role) {

        return role != null
                && !role.trim().isEmpty();
    }

    // =========================================================
    // ROLE NAME -> ROLE ID
    // =========================================================
    private int getRoleId(String role) {

        switch (role.trim().toUpperCase()) {

            case "SYSTEM ADMINISTRATOR":
            case "ADMIN":
                return 1;

            case "HR PERSONNEL":
            case "HR":
                return 2;

            case "FINANCE PERSONNEL":
            case "FINANCE":
                return 3;

            case "EMPLOYEE":
            case "USER":
                return 4;

            case "IT PERSONNEL":
            case "IT":
                return 5;

            default:
                throw new IllegalArgumentException(
                        "Invalid role: " + role
                );
        }
    }

    // =========================================================
    // ROLE ID -> ROLE NAME
    // =========================================================
    public String getRoleName(int roleId) {

        switch (roleId) {

            case 1:
                return "System Administrator";

            case 2:
                return "HR Personnel";

            case 3:
                return "Finance Personnel";

            case 4:
                return "Employee";

            case 5:
                return "IT Personnel";

            default:
                return "Unknown";
        }
    }

    // =========================================================
    // ADD ACCOUNT
    // =========================================================
    public void addAccount(
            String empId,
            String password,
            String role
    ) {

        // Validate password
        if (!isValidPassword(password)) {

            throw new IllegalArgumentException(
                    "Password cannot be empty."
            );
        }

        // Validate role
        if (!isValidRole(role)) {

            throw new IllegalArgumentException(
                    "Role cannot be empty."
            );
        }

        // Check duplicate account
        if (accountExists(empId)) {

            throw new IllegalArgumentException(
                    "Account already exists for Employee ID: " + empId
            );
        }

        // Convert role -> role_id
        int roleId = getRoleId(role);

        // Save to DB
        dao.addAccount(empId, password, roleId);
    }

    // =========================================================
    // UPDATE ACCOUNT
    // =========================================================
    public void updateAccount(
            String empId,
            String newPassword,
            String newRole
    ) {

        // Validate password
        if (!isValidPassword(newPassword)) {

            throw new IllegalArgumentException(
                    "Password cannot be empty."
            );
        }

        // Validate role
        if (!isValidRole(newRole)) {

            throw new IllegalArgumentException(
                    "Role cannot be empty."
            );
        }

        // Convert role -> role_id
        int roleId = getRoleId(newRole);

        // Update DB
        dao.updateAccount(
                empId,
                newPassword,
                roleId
        );
    }

    // =========================================================
    // DELETE ACCOUNT
    // =========================================================
    public void deleteAccount(String empId) {
        dao.deleteAccount(empId);
    }

    // =========================================================
    // PERMISSION CHECK
    // =========================================================
    public boolean canManageAccounts(String role) {

        return Role
                .fromString(role)
                .canManageAccounts();
    }
}