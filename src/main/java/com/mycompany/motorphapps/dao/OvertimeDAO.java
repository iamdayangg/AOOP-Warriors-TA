package com.mycompany.motorphapps.dao;

import com.mycompany.motorphapps.config.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OvertimeDAO implements IOvertimeDAO {

    public List<String[]> getAllOvertimeLogs() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT o.overtime_id, o.employee_id, " +
            "       CONCAT(e.first_name, ' ', e.last_name) AS full_name, " +
            "       o.payroll_id, o.ot_date, o.hours_worked, " +
            "       o.overtime_type, o.status " +
            "FROM overtime_logs o " +
            "JOIN employees e ON o.employee_id = e.employee_id " +
            "ORDER BY o.ot_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(buildRow(rs));
            }

        } catch (Exception e) {
            System.err.println("[OvertimeDAO] getAllOvertimeLogs failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<String[]> getOvertimeByEmployee(String employeeId) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT o.overtime_id, o.employee_id, " +
            "       CONCAT(e.first_name, ' ', e.last_name) AS full_name, " +
            "       o.payroll_id, o.ot_date, o.hours_worked, " +
            "       o.overtime_type, o.status " +
            "FROM overtime_logs o " +
            "JOIN employees e ON o.employee_id = e.employee_id " +
            "WHERE o.employee_id = ? " +
            "ORDER BY o.ot_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildRow(rs));
                }
            }

        } catch (Exception e) {
            System.err.println("[OvertimeDAO] getOvertimeByEmployee failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }


    public String[] getOvertimeById(int overtimeId) {
        String sql =
            "SELECT o.overtime_id, o.employee_id, " +
            "       CONCAT(e.first_name, ' ', e.last_name) AS full_name, " +
            "       o.payroll_id, o.ot_date, o.hours_worked, " +
            "       o.overtime_type, o.status " +
            "FROM overtime_logs o " +
            "JOIN employees e ON o.employee_id = e.employee_id " +
            "WHERE o.overtime_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, overtimeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return buildRow(rs);
            }

        } catch (Exception e) {
            System.err.println("[OvertimeDAO] getOvertimeById failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public List<String[]> getOvertimeByPayrollId(int payrollId) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT o.overtime_id, o.employee_id, " +
            "       CONCAT(e.first_name, ' ', e.last_name) AS full_name, " +
            "       o.payroll_id, o.ot_date, o.hours_worked, " +
            "       o.overtime_type, o.status " +
            "FROM overtime_logs o " +
            "JOIN employees e ON o.employee_id = e.employee_id " +
            "WHERE o.payroll_id = ? " +
            "ORDER BY o.ot_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payrollId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildRow(rs));
                }
            }

        } catch (Exception e) {
            System.err.println("[OvertimeDAO] getOvertimeByPayrollId failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean addOvertimeLog(
            String employeeId,
            Integer payrollId,
            String otDate,
            double hoursWorked,
            String overtimeType
    ) {
        String sql =
            "INSERT INTO overtime_logs " +
            "(employee_id, payroll_id, ot_date, hours_worked, overtime_type, status) " +
            "VALUES (?, ?, ?, ?, ?, 'Pending')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);

            if (payrollId == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, payrollId);
            }

            ps.setString(3, otDate);
            ps.setDouble(4, hoursWorked);
            ps.setString(5, overtimeType);

            int rows = ps.executeUpdate();
            System.out.println("[OvertimeDAO] addOvertimeLog rows affected: " + rows
                    + " | empId=" + employeeId);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("[OvertimeDAO] addOvertimeLog failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to add overtime log.", e);
        }
    }

    public String[] updateStatus(int overtimeId, String status) {
        String[] existing = getOvertimeById(overtimeId);
        if (existing == null) {
            System.err.println("[OvertimeDAO] updateStatus — overtime_id not found: " + overtimeId);
            return null;
        }

        String sql =
            "UPDATE overtime_logs SET status = ? " +
            "WHERE overtime_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, overtimeId);

            int rows = ps.executeUpdate();
            System.out.println("[OvertimeDAO] updateStatus rows affected: " + rows
                    + " | id=" + overtimeId + " status=" + status);

            if (rows > 0) {
                existing[7] = status;   
                return existing;
            }

            if (rows == 0) {
                System.err.println("[OvertimeDAO] WARNING: No rows updated for overtime_id: "
                        + overtimeId);
            }

        } catch (Exception e) {
            System.err.println("[OvertimeDAO] updateStatus failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update overtime status.", e);
        }
        return null;
    }

    public void linkToPayroll(String employeeId, String otDate, int payrollId) {
        String sql =
            "UPDATE overtime_logs SET payroll_id = ? " +
            "WHERE employee_id = ? AND ot_date = ? AND payroll_id IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payrollId);
            ps.setString(2, employeeId);
            ps.setString(3, otDate);

            int rows = ps.executeUpdate();
            System.out.println("[OvertimeDAO] linkToPayroll rows affected: " + rows
                    + " | empId=" + employeeId + " payrollId=" + payrollId);

        } catch (Exception e) {
            System.err.println("[OvertimeDAO] linkToPayroll failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean deleteOvertimeLog(int overtimeId) {
        String sql = "DELETE FROM overtime_logs WHERE overtime_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, overtimeId);
            int rows = ps.executeUpdate();
            System.out.println("[OvertimeDAO] deleteOvertimeLog rows affected: " + rows);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("[OvertimeDAO] deleteOvertimeLog failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete overtime log.", e);
        }
    }

    public double getTotalApprovedOTHours(
            String employeeId,
            String startDate,
            String endDate
    ) {
        String sql =
            "SELECT COALESCE(SUM(hours_worked), 0) AS total " +
            "FROM overtime_logs " +
            "WHERE employee_id = ? " +
            "AND status = 'Approved' " +
            "AND ot_date BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ps.setString(2, startDate);
            ps.setString(3, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
            }

        } catch (Exception e) {
            System.err.println("[OvertimeDAO] getTotalApprovedOTHours failed: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    private String[] buildRow(ResultSet rs) throws SQLException {
        return new String[]{
            nullSafe(rs.getString("overtime_id")),   // [0]
            nullSafe(rs.getString("employee_id")),   // [1]
            nullSafe(rs.getString("full_name")),     // [2]
            nullSafe(rs.getString("payroll_id")),    // [3]
            nullSafe(rs.getString("ot_date")),       // [4]
            nullSafe(rs.getString("hours_worked")),  // [5]
            nullSafe(rs.getString("overtime_type")), // [6]
            nullSafe(rs.getString("status"))         // [7]
        };
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}