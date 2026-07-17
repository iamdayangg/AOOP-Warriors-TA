package com.mycompany.motorphapps.dao;

import com.mycompany.motorphapps.config.DBConnection;
import java.sql.*;
import java.util.*;


public class LeaveBalanceDAO implements ILeaveBalanceDAO {


    public List<String[]> getAllLeaveBalances() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT lb.leave_balance_id, lb.employee_id, " +
            "       CONCAT(e.first_name, ' ', e.last_name) AS full_name, " +
            "       lb.leave_type_id, lt.leave_name, lb.remaining_balance " +
            "FROM leave_balances lb " +
            "JOIN employees e   ON lb.employee_id   = e.employee_id " +  
            "JOIN leave_types lt ON lb.leave_type_id = lt.leave_type_id " +
            "ORDER BY lb.employee_id, lb.leave_type_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                    nullSafe(rs.getString("leave_balance_id")),  
                    nullSafe(rs.getString("employee_id")),
                    nullSafe(rs.getString("full_name")),
                    nullSafe(rs.getString("leave_type_id")),
                    nullSafe(rs.getString("leave_name")),
                    nullSafe(rs.getString("remaining_balance"))
                });
            }

        } catch (Exception e) {
            System.err.println("[LeaveBalanceDAO] getAllLeaveBalances failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public double getBalance(String employeeId, int leaveTypeId) {
        String sql =
            "SELECT remaining_balance FROM leave_balances " +
            "WHERE employee_id = ? AND leave_type_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ps.setInt(2, leaveTypeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("remaining_balance");
            }

        } catch (Exception e) {
            System.err.println("[LeaveBalanceDAO] getBalance failed: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }


    public List<String[]> getAllLeaveTypes() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT leave_type_id, leave_name " +
            "FROM leave_types " +
            "ORDER BY leave_type_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("leave_type_id"),
                    rs.getString("leave_name")
                });
            }

        } catch (Exception e) {
            System.err.println("[LeaveBalanceDAO] getAllLeaveTypes failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }


    public List<String[]> getBalancesForEmployee(String employeeId) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT lb.leave_type_id, lt.leave_name, lb.remaining_balance " +
            "FROM leave_balances lb " +
            "JOIN leave_types lt ON lb.leave_type_id = lt.leave_type_id " +
            "WHERE lb.employee_id = ? " +
            "ORDER BY lb.leave_type_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                        rs.getString("leave_type_id"),
                        rs.getString("leave_name"),
                        rs.getString("remaining_balance")
                    });
                }
            }

        } catch (Exception e) {
            System.err.println("[LeaveBalanceDAO] getBalancesForEmployee failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }


    public boolean deductLeave(String employeeId, int leaveTypeId, double days) {
        double current = getBalance(employeeId, leaveTypeId);

        if (current < days) {
            System.err.println("[LeaveBalanceDAO] deductLeave — insufficient balance " +
                "empId=" + employeeId + " type=" + leaveTypeId +
                " (has=" + current + " needs=" + days + ")");
            return false;
        }

        String sql =
            "UPDATE leave_balances " +
            "SET remaining_balance = remaining_balance - ? " +
            "WHERE employee_id = ? AND leave_type_id = ? AND remaining_balance >= ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, days);
            ps.setString(2, employeeId);
            ps.setInt(3, leaveTypeId);
            ps.setDouble(4, days);

            int rows = ps.executeUpdate();
            System.out.println("[LeaveBalanceDAO] deductLeave rows affected: " + rows
                    + " | empId=" + employeeId + " days=" + days);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("[LeaveBalanceDAO] deductLeave failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void restoreLeave(String employeeId, int leaveTypeId, double days) {
        String sql =
            "UPDATE leave_balances " +
            "SET remaining_balance = remaining_balance + ? " +
            "WHERE employee_id = ? AND leave_type_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, days);
            ps.setString(2, employeeId);
            ps.setInt(3, leaveTypeId);

            int rows = ps.executeUpdate();
            System.out.println("[LeaveBalanceDAO] restoreLeave rows affected: " + rows
                    + " | empId=" + employeeId + " days=" + days);

        } catch (Exception e) {
            System.err.println("[LeaveBalanceDAO] restoreLeave failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void seedBalancesForNewEmployee(String employeeId) {
        String sql =
            "INSERT IGNORE INTO leave_balances " +
            "(employee_id, leave_type_id, remaining_balance) " +
            "VALUES (?, ?, 15.00)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ps.setInt(2, 1);
            ps.executeUpdate();


            ps.setString(1, employeeId);
            ps.setInt(2, 2);
            ps.executeUpdate();

            System.out.println("[LeaveBalanceDAO] seedBalancesForNewEmployee — id=" + employeeId);

        } catch (Exception e) {
            System.err.println("[LeaveBalanceDAO] seedBalancesForNewEmployee failed: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void resetAllBalances() {
        String sql =
            "UPDATE leave_balances SET remaining_balance = 15.00 " +
            "WHERE leave_type_id IN (1, 2)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int rows = ps.executeUpdate();
            System.out.println("[LeaveBalanceDAO] resetAllBalances — " + rows + " rows reset.");

        } catch (Exception e) {
            System.err.println("[LeaveBalanceDAO] resetAllBalances failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}