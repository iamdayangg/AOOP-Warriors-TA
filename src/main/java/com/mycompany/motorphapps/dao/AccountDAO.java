package com.mycompany.motorphapps.dao;

import com.mycompany.motorphapps.config.DBConnection;
import com.mycompany.motorphapps.security.PasswordHashService;
import java.sql.*;
import java.util.*;

public class AccountDAO implements IAccountDAO {

    private final PasswordHashService passwordService = new PasswordHashService();

    public List<String[]> getAllAccounts() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT c.employee_id, c.password, r.role_name " +
            "FROM credentials c " +
            "JOIN roles r ON c.role_id = r.role_id ";

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
            System.err.println("[AccountDAO] getAllAccounts failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public void addAccount(String employeeId, String password, int roleId) {
        String sql =
            "INSERT INTO credentials (employee_id, password, role_id) " +
            "VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ps.setString(2, passwordService.hash(password));
            ps.setInt(3, roleId);

            int rows = ps.executeUpdate();
            System.out.println("[AccountDAO] addAccount rows affected: " + rows);

        } catch (Exception e) {
            System.err.println("[AccountDAO] addAccount failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateAccount(String employeeId, String newPassword, int newRoleId) {
        String sql =
            "UPDATE credentials SET password = ?, role_id = ? " +
            "WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, passwordService.hash(newPassword));
            ps.setInt(2, newRoleId);
            ps.setString(3, employeeId);

            int rows = ps.executeUpdate();
            System.out.println("[AccountDAO] updateAccount rows affected: " + rows);

            if (rows == 0) {
                System.err.println("[AccountDAO] WARNING: employee_id not found: " + employeeId);
            }

        } catch (Exception e) {
            System.err.println("[AccountDAO] updateAccount failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteAccount(String employeeId) {
        String sql = "DELETE FROM credentials WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);

            int rows = ps.executeUpdate();
            System.out.println("[AccountDAO] deleteAccount rows affected: " + rows);

        } catch (Exception e) {
            System.err.println("[AccountDAO] deleteAccount failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}