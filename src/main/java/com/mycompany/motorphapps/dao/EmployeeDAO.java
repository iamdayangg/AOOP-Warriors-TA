package com.mycompany.motorphapps.dao;

import com.mycompany.motorphapps.config.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class EmployeeDAO implements IEmployeeDAO {

    private static final String BASE_SQL =
        "SELECT " +
        "  e.employee_id, " +
        "  e.last_name, " +
        "  e.first_name, " +
        "  e.birthday, " +
        "  e.address, " +
        "  e.phone_number, " +
        "  g.sss_number, " +
        "  g.philhealth_number, " +
        "  g.tin_number, " +
        "  g.pagibig_number, " +
        "  e.emp_status, " +
        "  e.position, " +
        "  CASE WHEN sup.employee_id IS NOT NULL " +
        "       THEN CONCAT(sup.first_name, ' ', sup.last_name) " +
        "       ELSE NULL END AS supervisor_name, " +
        "  c.basic_salary, " +
        "  COALESCE(MAX(CASE WHEN cd.benefit_id = 1 THEN cd.amount END), 0) AS rice_subsidy, " +
        "  COALESCE(MAX(CASE WHEN cd.benefit_id = 2 THEN cd.amount END), 0) AS phone_allowance, " +
        "  COALESCE(MAX(CASE WHEN cd.benefit_id = 3 THEN cd.amount END), 0) AS clothing_allowance, " +
        "  c.semi_monthly_rate, " +
        "  c.hourly_rate, " +
        "  e.gender " +
        "FROM employees e " +
        "LEFT JOIN employee_government_ids g  ON e.employee_id   = g.employee_id " +
        "LEFT JOIN employees sup              ON e.supervisor_id = sup.employee_id " +
        "LEFT JOIN compensation c             ON e.employee_id   = c.employee_id " +
        "LEFT JOIN compensation_details cd    ON c.compensation_id = cd.compensation_id ";

    private static final String GROUP_CLAUSE =
        "GROUP BY " +
        "  e.employee_id, e.last_name, e.first_name, e.birthday, e.address, " +
        "  e.phone_number, g.sss_number, g.philhealth_number, g.tin_number, " +
        "  g.pagibig_number, e.emp_status, e.position, supervisor_name, " +
        "  c.basic_salary, c.semi_monthly_rate, c.hourly_rate, e.gender ";

    public List<String[]> getAllEmployees() {
        List<String[]> list = new ArrayList<>();
        String sql = BASE_SQL + GROUP_CLAUSE + "ORDER BY e.employee_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (Exception e) {
            System.err.println("[EmployeeDAO] getAllEmployees failed: " + e.getMessage());
            throw new RuntimeException("Error loading employees.", e);
        }
        return list;
    }

    public String[] getEmployeeById(String employeeId) {
        String sql = BASE_SQL +
            "WHERE e.employee_id = ? " +
            GROUP_CLAUSE +
            "LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            System.err.println("[EmployeeDAO] getEmployeeById failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private String[] mapRow(ResultSet rs) throws SQLException {
        return new String[]{
            nullSafe(rs.getString("employee_id")),        // [0]
            nullSafe(rs.getString("last_name")),          // [1]
            nullSafe(rs.getString("first_name")),         // [2]
            nullSafe(rs.getString("birthday")),           // [3]
            nullSafe(rs.getString("address")),            // [4]
            nullSafe(rs.getString("phone_number")),       // [5]
            nullSafe(rs.getString("sss_number")),         // [6]
            nullSafe(rs.getString("philhealth_number")),  // [7]
            nullSafe(rs.getString("tin_number")),         // [8]
            nullSafe(rs.getString("pagibig_number")),     // [9]
            nullSafe(rs.getString("emp_status")),         // [10]
            nullSafe(rs.getString("position")),           // [11]
            nullSafe(rs.getString("supervisor_name")),    // [12]
            nullSafe(rs.getString("basic_salary")),       // [13]
            nullSafe(rs.getString("rice_subsidy")),       // [14]
            nullSafe(rs.getString("phone_allowance")),    // [15]
            nullSafe(rs.getString("clothing_allowance")), // [16]
            nullSafe(rs.getString("semi_monthly_rate")),  // [17]
            nullSafe(rs.getString("hourly_rate")),        // [18]
            nullSafe(rs.getString("gender"))              // [19]
        };
    }


    public void addEmployee(String[] d) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);


            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO employees " +
                    "(employee_id, last_name, first_name, gender, birthday, address, " +
                    " phone_number, emp_status, position, department, supervisor_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                ps.setString(1,  safe(d, 0));
                ps.setString(2,  safe(d, 1));
                ps.setString(3,  safe(d, 2));
                ps.setString(4,  safe(d, 19));
                ps.setString(5,  safe(d, 3));
                ps.setString(6,  safe(d, 4));
                ps.setString(7,  safe(d, 5));
                ps.setString(8,  safe(d, 10));
                ps.setString(9,  safe(d, 11));
                ps.setString(10, resolveDepartment(safe(d, 11)));
                setNullableInt(ps, 11, safe(d, 12), conn);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO employee_government_ids " +
                    "(employee_id, sss_number, philhealth_number, tin_number, pagibig_number) " +
                    "VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, safe(d, 0));
                ps.setString(2, safe(d, 6));
                ps.setString(3, safe(d, 7));
                ps.setString(4, safe(d, 8));
                ps.setString(5, safe(d, 9));
                ps.executeUpdate();
            }


            int compensationId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO compensation " +
                    "(employee_id, basic_salary, semi_monthly_rate, hourly_rate) " +
                    "VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, safe(d, 0));
                ps.setDouble(2, safeDouble(d, 13));
                ps.setDouble(3, safeDouble(d, 17));
                ps.setDouble(4, safeDouble(d, 18));
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    compensationId = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO compensation_details " +
                    "(compensation_id, benefit_id, amount) VALUES (?, ?, ?)")) {
                int[][] benefits = {{1,14},{2,15},{3,16}};
                for (int[] b : benefits) {
                    ps.setInt(1, compensationId);
                    ps.setInt(2, b[0]);
                    ps.setDouble(3, safeDouble(d, b[1]));
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            System.out.println("[EmployeeDAO] addEmployee success: " + safe(d, 0));

        } catch (SQLIntegrityConstraintViolationException e) {
            rollback(conn);
            throw new RuntimeException("Employee ID already exists or invalid reference.", e);
        } catch (Exception e) {
            rollback(conn);
            System.err.println("[EmployeeDAO] addEmployee failed: " + e.getMessage());
            throw new RuntimeException("Error adding employee.", e);
        } finally {
            closeConn(conn);
        }
    }

    public void updateEmployee(String[] d) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE employees SET " +
                    "last_name=?, first_name=?, gender=?, birthday=?, address=?, " +
                    "phone_number=?, emp_status=?, position=?, department=?, supervisor_id=? " +
                    "WHERE employee_id=?")) {
                ps.setString(1,  safe(d, 1));
                ps.setString(2,  safe(d, 2));
                ps.setString(3,  safe(d, 19));
                ps.setString(4,  safe(d, 3));
                ps.setString(5,  safe(d, 4));
                ps.setString(6,  safe(d, 5));
                ps.setString(7,  safe(d, 10));
                ps.setString(8,  safe(d, 11));
                ps.setString(9,  resolveDepartment(safe(d, 11)));
                setNullableInt(ps, 10, safe(d, 12), conn);
                ps.setString(11, safe(d, 0));
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO employee_government_ids " +
                    "(employee_id, sss_number, philhealth_number, tin_number, pagibig_number) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "sss_number=VALUES(sss_number), philhealth_number=VALUES(philhealth_number), " +
                    "tin_number=VALUES(tin_number), pagibig_number=VALUES(pagibig_number)")) {
                ps.setString(1, safe(d, 0));
                ps.setString(2, safe(d, 6));
                ps.setString(3, safe(d, 7));
                ps.setString(4, safe(d, 8));
                ps.setString(5, safe(d, 9));
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO compensation " +
                    "(employee_id, basic_salary, semi_monthly_rate, hourly_rate) " +
                    "VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "basic_salary=VALUES(basic_salary), " +
                    "semi_monthly_rate=VALUES(semi_monthly_rate), " +
                    "hourly_rate=VALUES(hourly_rate)")) {
                ps.setString(1, safe(d, 0));
                ps.setDouble(2, safeDouble(d, 13));
                ps.setDouble(3, safeDouble(d, 17));
                ps.setDouble(4, safeDouble(d, 18));
                ps.executeUpdate();
            }

            int compensationId = -1;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT compensation_id FROM compensation WHERE employee_id=?")) {
                ps.setString(1, safe(d, 0));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) compensationId = rs.getInt(1);
                }
            }

            if (compensationId != -1) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO compensation_details (compensation_id, benefit_id, amount) " +
                        "VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE amount=VALUES(amount)")) {
                    int[][] benefits = {{1,14},{2,15},{3,16}};
                    for (int[] b : benefits) {
                        ps.setInt(1, compensationId);
                        ps.setInt(2, b[0]);
                        ps.setDouble(3, safeDouble(d, b[1]));
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            conn.commit();
            System.out.println("[EmployeeDAO] updateEmployee success: " + safe(d, 0));

        } catch (Exception e) {
            rollback(conn);
            System.err.println("[EmployeeDAO] updateEmployee failed: " + e.getMessage());
            throw new RuntimeException("Error updating employee.", e);
        } finally {
            closeConn(conn);
        }
    }

    public void deleteEmployee(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM employees WHERE employee_id=?")) {
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            System.out.println("[EmployeeDAO] deleteEmployee rows: " + rows);
        } catch (Exception e) {
            System.err.println("[EmployeeDAO] deleteEmployee failed for ID " + id + ": " + e.getMessage());
            e.printStackTrace();  
            throw new RuntimeException("Error deleting employee.", e);
        }
    }


    public boolean employeeExists(String id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT 1 FROM employees WHERE employee_id=? LIMIT 1")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) {
            throw new RuntimeException("Error checking employee.", e);
        }
    }


    private Integer resolveSuperId(String supervisorName, Connection conn) throws SQLException {
        if (supervisorName == null || supervisorName.isBlank()) return null;
        String[] parts = supervisorName.trim().split("\\s+", 2);
        if (parts.length < 2) return null;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT employee_id FROM employees WHERE first_name=? AND last_name=? LIMIT 1")) {
            ps.setString(1, parts[0]);
            ps.setString(2, parts[1]);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("employee_id") : null;
            }
        }
    }

    private void setNullableInt(PreparedStatement ps, int idx,
                                 String supervisorName, Connection conn) throws SQLException {
        Integer id = resolveSuperId(supervisorName, conn);
        if (id == null) ps.setNull(idx, Types.INTEGER);
        else            ps.setInt(idx, id);
    }

    private String resolveDepartment(String position) {
        if (position == null) return "Operations";
        String p = position.toLowerCase();
        if (p.contains("hr") || p.contains("human"))                            return "HR";
        if (p.contains("payroll") || p.contains("accounting")
                || p.contains("finance"))                                        return "Finance";
        if (p.contains("it") || p.contains("system"))                           return "IT";
        if (p.contains("marketing") || p.contains("sales")
                || p.contains("customer"))                                       return "Marketing";
        if (p.contains("chief") || p.contains("ceo") || p.contains("coo")
                || p.contains("cfo") || p.contains("cmo")
                || p.contains("co-founder"))                                     return "Executive";
        return "Operations";
    }

    private String safe(String[] d, int i) {
        return (d != null && i < d.length && d[i] != null) ? d[i].trim() : "";
    }

    private String nullSafe(String v) { return v == null ? "" : v; }

    private double safeDouble(String[] d, int i) {
        try {
            String v = safe(d, i);
            return v.isEmpty() ? 0.0 : Double.parseDouble(v);
        } catch (NumberFormatException e) { return 0.0; }
    }

    private void rollback(Connection conn) {
        if (conn != null) try { conn.rollback(); } catch (SQLException e) { /* ignore */ }
    }

    private void closeConn(Connection conn) {
        if (conn != null) try { conn.setAutoCommit(true); conn.close(); }
                          catch (SQLException e) { /* ignore */ }
    }
}