package com.mycompany.motorphapps.dao;

import com.mycompany.motorphapps.config.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class LeaveDAO implements ILeaveDAO {


    public List<String[]> getAllLeaves() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT lr.leave_request_id, lr.employee_id, " +
            "       lt.leave_name, lr.start_date, lr.end_date, " +
            "       lr.status, lr.leave_type_id " +
            "FROM leave_requests lr " +
            "JOIN leave_types lt ON lr.leave_type_id = lt.leave_type_id " +
            "ORDER BY lr.start_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(buildRow(rs));
            }
            System.out.println("[LeaveDAO] getAllLeaves loaded: " + list.size() + " rows");

        } catch (Exception e) {
            System.err.println("[LeaveDAO] getAllLeaves failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }


    public List<String[]> getLeavesByEmployee(String employeeId) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT lr.leave_request_id, lr.employee_id, " +
            "       lt.leave_name, lr.start_date, lr.end_date, " +
            "       lr.status, lr.leave_type_id " +
            "FROM leave_requests lr " +
            "JOIN leave_types lt ON lr.leave_type_id = lt.leave_type_id " +
            "WHERE lr.employee_id = ? " +
            "ORDER BY lr.start_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildRow(rs));
                }
            }
            System.out.println("[LeaveDAO] getLeavesByEmployee(" + employeeId + ") loaded: " + list.size() + " rows");

        } catch (Exception e) {
            System.err.println("[LeaveDAO] getLeavesByEmployee failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public String[] getLeaveById(String leaveRequestId) {
        String sql =
            "SELECT lr.leave_request_id, lr.employee_id, " +
            "       lt.leave_name, lr.start_date, lr.end_date, " +
            "       lr.status, lr.leave_type_id " +
            "FROM leave_requests lr " +
            "JOIN leave_types lt ON lr.leave_type_id = lt.leave_type_id " +
            "WHERE lr.leave_request_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, leaveRequestId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return buildRow(rs);
            }
            System.out.println("[LeaveDAO] getLeaveById(" + leaveRequestId + ") — not found");

        } catch (Exception e) {
            System.err.println("[LeaveDAO] getLeaveById failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean addLeave(
            String employeeId,
            int leaveTypeId,
            String startDate,
            String endDate
    ) {
        String sql =
            "INSERT INTO leave_requests " +
            "(employee_id, leave_type_id, start_date, end_date, status) " +
            "VALUES (?, ?, ?, ?, 'Pending')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ps.setInt(2, leaveTypeId);
            ps.setString(3, startDate);
            ps.setString(4, endDate);

            int rows = ps.executeUpdate();
            System.out.println("[LeaveDAO] addLeave rows affected: " + rows);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("[LeaveDAO] addLeave failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    public String[] updateStatus(String leaveRequestId, String status) {

        String[] existing = getLeaveById(leaveRequestId);
        if (existing == null) {
            System.err.println("[LeaveDAO] updateStatus — request not found: " + leaveRequestId);
            return null;
        }

        String sql =
            "UPDATE leave_requests SET status = ? " +
            "WHERE leave_request_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, leaveRequestId);

            int rows = ps.executeUpdate();
            System.out.println("[LeaveDAO] updateStatus rows affected: " + rows
                    + " | id=" + leaveRequestId + " status=" + status);

            if (rows > 0) {
                existing[5] = status;   
                return existing;
            }

        } catch (Exception e) {
            System.err.println("[LeaveDAO] updateStatus failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public boolean deleteLeave(String leaveRequestId) {
        String sql =
            "DELETE FROM leave_requests WHERE leave_request_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, leaveRequestId);

            int rows = ps.executeUpdate();
            System.out.println("[LeaveDAO] deleteLeave rows affected: " + rows);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("[LeaveDAO] deleteLeave failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    private String[] buildRow(ResultSet rs) throws SQLException {
        String start  = nullSafe(rs.getString("start_date"));
        String end    = nullSafe(rs.getString("end_date"));
        String days   = computeDays(start, end);

        return new String[]{
            nullSafe(rs.getString("leave_request_id")), 
            nullSafe(rs.getString("employee_id")),      
            nullSafe(rs.getString("leave_name")),       
            start,                                      
            end,                                        
            nullSafe(rs.getString("status")),           
            nullSafe(rs.getString("leave_type_id")),    
            days                                        
        };
    }


    private String computeDays(String start, String end) {
        try {
            long days = ChronoUnit.DAYS.between(
                LocalDate.parse(start),
                LocalDate.parse(end)
            ) + 1;
            return String.valueOf(days);
        } catch (Exception e) {
            return "0";
        }
    }


    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}