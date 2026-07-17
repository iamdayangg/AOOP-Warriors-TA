package com.mycompany.motorphapps.dao;
import com.mycompany.motorphapps.model.*;
import com.mycompany.motorphapps.config.DBConnection;
import com.mycompany.motorphapps.security.PasswordHashService;
import java.sql.*;
public class Authenticator {
    private final PasswordHashService passwordService = new PasswordHashService();
    public Person login(String id, String password) {
        String sql =
            "SELECT c.employee_id, c.password, r.role_name " +
            "FROM credentials c " +
            "JOIN roles r ON c.role_id = r.role_id " +
            "WHERE c.employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (storedPassword.startsWith("pbkdf2$")) {
                        if (!passwordService.matches(password, storedPassword)) {
                            System.out.println("[Authenticator] login failed - wrong password for id: " + id);
                            return null;
                        }
                    } else {
                        if (!storedPassword.equals(password)) {
                            System.out.println("[Authenticator] login failed - wrong password for id: " + id);
                            return null;
                        }
                        updatePassword(id, passwordService.hash(password));
                    }
                    String empId    = rs.getString("employee_id");
                    String roleName = rs.getString("role_name");
                    String[] nameParts = getEmployeeName(empId);
                    String firstName   = nameParts[0];
                    String lastName    = nameParts[1];

                    System.out.println("[Authenticator] login success - empId=" + empId
                            + " | role=" + roleName);

                    switch (roleName.toUpperCase()) {
                        case "SYSTEM ADMINISTRATOR":
                            return new Admin(empId, firstName, lastName);
                        case "HR PERSONNEL":
                            return new HR(empId, firstName, lastName);
                        case "FINANCE PERSONNEL":
                            return new Finance(empId, firstName, lastName);
                        case "IT PERSONNEL":
                            return new ITStaff(empId, firstName, lastName);
                        default:
                            return new Employee(empId, firstName, lastName);
                    }
                } else {
                    System.out.println("[Authenticator] login failed - no account found for id: " + id);
                }
            }
        } catch (Exception e) {
            System.err.println("[Authenticator] login failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    private void updatePassword(String employeeId, String newPassword) {
        String sql = "UPDATE credentials SET password = ? WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, employeeId);
            int rows = ps.executeUpdate();
            System.out.println("[Authenticator] updatePassword (legacy → hashed) rows affected: " + rows
                    + " | empId=" + employeeId);
        } catch (Exception e) {
            System.err.println("[Authenticator] updatePassword failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private String[] getEmployeeName(String empId) {
    String sql =
        "SELECT first_name, last_name FROM employees WHERE employee_id = ? LIMIT 1";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, empId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new String[]{ rs.getString("first_name"), rs.getString("last_name") };
            }
        }
    } catch (Exception e) {
        System.err.println("[Authenticator] getEmployeeName failed: " + e.getMessage());
        e.printStackTrace();
    }
    return new String[]{ "", "" };
}
}