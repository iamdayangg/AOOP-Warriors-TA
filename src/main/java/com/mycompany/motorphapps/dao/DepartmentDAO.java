package com.mycompany.motorphapps.dao;

import com.mycompany.motorphapps.config.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DepartmentDAO implements IDepartmentDAO {


    public List<String> getDepartmentNames() {
        List<String> names = new ArrayList<>();
        String sql =
            "SELECT DISTINCT department " +
            "FROM employees " +
            "WHERE department IS NOT NULL " +
            "ORDER BY department";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                names.add(rs.getString("department"));
            }

        } catch (Exception e) {
            System.err.println("[DepartmentDAO] getDepartmentNames failed: " + e.getMessage());
            e.printStackTrace();
        }
        return names;
    }

    public List<String[]> getAllDepartments() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT e.department, " +
            "       e.employee_id   AS head_id, " +
            "       CONCAT(e.first_name, ' ', e.last_name) AS head_name " +
            "FROM employees e " +
            "INNER JOIN ( " +
            "    SELECT department, MIN(employee_id) AS min_id " +
            "    FROM employees " +
            "    WHERE supervisor_id IS NULL " +
            "      AND department IS NOT NULL " +
            "    GROUP BY department " +
            ") h ON e.employee_id = h.min_id " +
            "ORDER BY e.department";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                    nullSafe(rs.getString("department")),
                    nullSafe(rs.getString("head_id")),
                    nullSafe(rs.getString("head_name")).trim()
                });
            }

        } catch (Exception e) {
            System.err.println("[DepartmentDAO] getAllDepartments failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }


    public List<String[]> getEmployeesByDepartment(String departmentName) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT employee_id, " +
            "       CONCAT(first_name, ' ', last_name) AS full_name, " +
            "       position, " +
            "       emp_status " +
            "FROM employees " +
            "WHERE department = ? " +
            "ORDER BY employee_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, departmentName);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                        nullSafe(rs.getString("employee_id")),
                        nullSafe(rs.getString("full_name")).trim(),
                        nullSafe(rs.getString("position")),
                        nullSafe(rs.getString("emp_status"))
                    });
                }
            }

        } catch (Exception e) {
            System.err.println("[DepartmentDAO] getEmployeesByDepartment failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }


    public boolean renameDepartment(String oldName, String newName) {
        String sql =
            "UPDATE employees SET department = ? " +
            "WHERE department = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newName);
            ps.setString(2, oldName);

            int rows = ps.executeUpdate();
            System.out.println("[DepartmentDAO] renameDepartment rows affected: " + rows);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("[DepartmentDAO] renameDepartment failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    public boolean deleteDepartment(String departmentName) {
        String sql =
            "UPDATE employees SET department = NULL " +
            "WHERE department = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, departmentName);

            int rows = ps.executeUpdate();
            System.out.println("[DepartmentDAO] deleteDepartment rows affected: " + rows);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("[DepartmentDAO] deleteDepartment failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}