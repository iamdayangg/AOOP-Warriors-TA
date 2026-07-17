package com.mycompany.motorphapps.dao;

import com.mycompany.motorphapps.config.DBConnection;
import com.mycompany.motorphapps.security.PasswordHashService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CredentialDAO implements ICredentialDAO {

    private final PasswordHashService passwordService = new PasswordHashService();


    public boolean createUser(String employeeId, String password, int roleId) {
        String sql =
            "INSERT INTO credentials (employee_id, password, role_id) " +
            "VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ps.setString(2, passwordService.hash(password));
            ps.setInt(3, roleId);

            int rows = ps.executeUpdate();
            System.out.println("[CredentialDAO] createUser rows affected: " + rows);
            return rows > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("[CredentialDAO] createUser — duplicate employee_id: " + employeeId);
        } catch (Exception e) {
            System.err.println("[CredentialDAO] createUser failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    public boolean updatePassword(String employeeId, String newPassword) {
        String sql =
            "UPDATE credentials SET password = ? " +
            "WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, passwordService.hash(newPassword));
            ps.setString(2, employeeId);

            int rows = ps.executeUpdate();
            System.out.println("[CredentialDAO] updatePassword rows affected: " + rows);

            if (rows == 0) {
                System.err.println("[CredentialDAO] WARNING: employee_id not found: " + employeeId);
            }
            return rows > 0;

        } catch (Exception e) {
            System.err.println("[CredentialDAO] updatePassword failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    public boolean updateRole(String employeeId, int newRoleId) {
        String sql =
            "UPDATE credentials SET role_id = ? " +
            "WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newRoleId);
            ps.setString(2, employeeId);

            int rows = ps.executeUpdate();
            System.out.println("[CredentialDAO] updateRole rows affected: " + rows
                    + " | empId=" + employeeId + " | newRoleId=" + newRoleId);

            if (rows == 0) {
                System.err.println("[CredentialDAO] WARNING: employee_id not found: " + employeeId);
            }
            return rows > 0;

        } catch (Exception e) {
            System.err.println("[CredentialDAO] updateRole failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    public boolean deleteUser(String employeeId) {
        String sql = "DELETE FROM credentials WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);

            int rows = ps.executeUpdate();
            System.out.println("[CredentialDAO] deleteUser rows affected: " + rows);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("[CredentialDAO] deleteUser failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    public boolean exists(String employeeId) {
        String sql =
            "SELECT 1 FROM credentials " +
            "WHERE employee_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            System.err.println("[CredentialDAO] exists check failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    public List<String[]> getAllCredentials() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT c.employee_id, c.password, r.role_name " +
            "FROM credentials c " +
            "JOIN roles r ON c.role_id = r.role_id " +   
            "ORDER BY c.employee_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("employee_id"),
                    rs.getString("password"),
                    rs.getString("role_name")
                });
            }

        } catch (Exception e) {
            System.err.println("[CredentialDAO] getAllCredentials failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}