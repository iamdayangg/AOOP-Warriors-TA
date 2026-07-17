package com.mycompany.motorphapps.dao;

import com.mycompany.motorphapps.config.DBConnection;
import com.mycompany.motorphapps.model.Attendance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO implements IAttendanceDAO {

    public List<String[]> getAllAttendance() {

        List<String[]> list = new ArrayList<>();

        String sql =
                "SELECT a.employee_id, e.first_name, e.last_name, " +
                "a.attendance_date, a.log_in_time, a.log_out_time " +
                "FROM attendance a " +
                "JOIN employees e ON a.employee_id = e.employee_id " +
                "ORDER BY a.attendance_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String employeeId = String.valueOf(rs.getInt("employee_id"));

                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");

                String date = rs.getDate("attendance_date").toString();

                String timeIn = rs.getTime("log_in_time") != null
                        ? rs.getTime("log_in_time").toString()
                        : "";

                String timeOut = rs.getTime("log_out_time") != null
                        ? rs.getTime("log_out_time").toString()
                        : "";

                list.add(new String[]{
                        employeeId,
                        fullName,
                        date,
                        timeIn,
                        timeOut
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean logTimeIn(String employeeId, String date, String timeIn) {

        String sql =
                "INSERT INTO attendance (employee_id, attendance_date, log_in_time) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE log_in_time = VALUES(log_in_time)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(employeeId));
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(timeIn));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean logTimeOut(String employeeId, String date, String timeOut) {

        String sql =
                "UPDATE attendance " +
                "SET log_out_time = ? " +
                "WHERE employee_id = ? AND attendance_date = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTime(1, Time.valueOf(timeOut));
            ps.setInt(2, Integer.parseInt(employeeId));
            ps.setDate(3, Date.valueOf(date));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void saveOrUpdateAll(List<Attendance> logs) {

        String sql =
                "INSERT INTO attendance (employee_id, attendance_date, log_in_time, log_out_time) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "log_in_time = VALUES(log_in_time), " +
                "log_out_time = VALUES(log_out_time)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (Attendance a : logs) {

                ps.setInt(1, Integer.parseInt(a.getEmployeeId()));
                ps.setDate(2, Date.valueOf(a.getDate()));

                // TIME IN
                if (!a.getTimeIn().isEmpty()) {
                    ps.setTime(3, Time.valueOf(a.getTimeIn()));
                } else {
                    ps.setNull(3, Types.TIME);
                }

                // TIME OUT
                if (!a.getTimeOut().isEmpty()) {
                    ps.setTime(4, Time.valueOf(a.getTimeOut()));
                } else {
                    ps.setNull(4, Types.TIME);
                }

                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean deleteAttendance(String employeeId, String date) {

    String sql =
        "DELETE FROM attendance " +
        "WHERE employee_id = ? AND attendance_date = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, Integer.parseInt(employeeId));
        ps.setDate(2, Date.valueOf(date));

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}

    public Attendance getAttendance(String employeeId, String date) {

        String sql =
                "SELECT * FROM attendance WHERE employee_id = ? AND attendance_date = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(employeeId));
            ps.setDate(2, Date.valueOf(date));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String timeIn = rs.getTime("log_in_time") != null
                        ? rs.getTime("log_in_time").toString()
                        : "";

                String timeOut = rs.getTime("log_out_time") != null
                        ? rs.getTime("log_out_time").toString()
                        : "";

                return new Attendance(employeeId, date, timeIn, timeOut);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}