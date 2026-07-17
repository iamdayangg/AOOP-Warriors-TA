package com.mycompany.motorphapps.dao;

import com.mycompany.motorphapps.config.DBConnection;
import com.mycompany.motorphapps.model.PayrollRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO implements IPayrollDAO {

    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }

    public List<String[]> readAllPayroll() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT p.payroll_id, p.employee_id, " +
            "       CONCAT(e.first_name, ' ', e.last_name) AS full_name, " +
            "       p.payroll_period, p.regular_hours_worked, p.overtime_hours_worked, " +
            "       p.gross_pay, p.total_deductions, p.net_pay " +
            "FROM payroll p " +
            "JOIN employees e ON p.employee_id = e.employee_id " +
            "ORDER BY p.payroll_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(buildPayrollRow(rs));
            }
        } catch (Exception e) {
            System.err.println("[PayrollDAO] readAllPayroll failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<String[]> readPayrollByEmployee(String employeeId) {
        requireNonBlank(employeeId, "employeeId");

        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT p.payroll_id, p.employee_id, " +
            "       CONCAT(e.first_name, ' ', e.last_name) AS full_name, " +
            "       p.payroll_period, p.regular_hours_worked, p.overtime_hours_worked, " +
            "       p.gross_pay, p.total_deductions, p.net_pay " +
            "FROM payroll p " +
            "JOIN employees e ON p.employee_id = e.employee_id " +
            "WHERE p.employee_id = ? " +
            "ORDER BY p.payroll_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildPayrollRow(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("[PayrollDAO] readPayrollByEmployee failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public String[] findPayrollRow(String employeeId, String period) {
        requireNonBlank(employeeId, "employeeId");
        requireNonBlank(period, "period");

        String sql =
            "SELECT p.payroll_id, p.employee_id, " +
            "       CONCAT(e.first_name, ' ', e.last_name) AS full_name, " +
            "       p.payroll_period, p.regular_hours_worked, p.overtime_hours_worked, " +
            "       p.gross_pay, p.total_deductions, p.net_pay " +
            "FROM payroll p " +
            "JOIN employees e ON p.employee_id = e.employee_id " +
            "WHERE p.employee_id = ? AND p.payroll_period = ? " +
            "LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId.trim());
            ps.setString(2, period.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildPayrollRow(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("[PayrollDAO] findPayrollRow failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int savePayslip(String employeeId,
                           String period,
                           double regularHours,
                           double overtimeHours,
                           double grossPay,
                           double sss,
                           double philHealth,
                           double pagibig,
                           double tax,
                           double netPay) {

        requireNonBlank(employeeId, "employeeId");
        requireNonBlank(period, "period");

        double totalDeductions = sss + philHealth + pagibig + tax;

        String insertPayroll =
            "INSERT INTO payroll " +
            "  (employee_id, payroll_period, regular_hours_worked, overtime_hours_worked, " +
            "   gross_pay, total_deductions, net_pay) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            int payrollId;
            try (PreparedStatement ps = conn.prepareStatement(
                    insertPayroll, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, employeeId.trim());
                ps.setString(2, period.trim());
                ps.setDouble(3, regularHours);
                ps.setDouble(4, overtimeHours);
                ps.setDouble(5, grossPay);
                ps.setDouble(6, totalDeductions);
                ps.setDouble(7, netPay);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conn.rollback();
                        System.err.println("[PayrollDAO] savePayslip: no generated key, rolled back");
                        return -1;
                    }
                    payrollId = keys.getInt(1);
                }
            }

            insertDeduction(conn, payrollId, "SSS",             sss);
            insertDeduction(conn, payrollId, "PhilHealth",      philHealth);
            insertDeduction(conn, payrollId, "Pag-ibig",        pagibig);
            insertDeduction(conn, payrollId, "Withholding Tax", tax);

            conn.commit();
            System.out.println("[PayrollDAO] savePayslip success - payrollId=" + payrollId
                    + " | empId=" + employeeId + " | period=" + period);
            return payrollId;

        } catch (Exception e) {
            System.err.println("[PayrollDAO] savePayslip failed: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public void deletePayroll(String employeeId, String period) {
        requireNonBlank(employeeId, "employeeId");
        requireNonBlank(period, "period");

        String findSql    = "SELECT payroll_id FROM payroll " +
                            "WHERE employee_id = ? AND payroll_period = ?";
        String delDed     = "DELETE FROM deduction WHERE payroll_id = ?";
        String delOvertime= "DELETE FROM overtime_logs WHERE payroll_id = ?";
        String delPayroll = "DELETE FROM payroll WHERE payroll_id = ?";

        int deletedCount = 0;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement find = conn.prepareStatement(findSql)) {
                find.setString(1, employeeId.trim());
                find.setString(2, period.trim());

                try (ResultSet rs = find.executeQuery()) {
                    while (rs.next()) {
                        int pid = rs.getInt("payroll_id");

                        try (PreparedStatement dd = conn.prepareStatement(delDed)) {
                            dd.setInt(1, pid);
                            dd.executeUpdate();
                        }
                        try (PreparedStatement dot = conn.prepareStatement(delOvertime)) {
                            dot.setInt(1, pid);
                            dot.executeUpdate();
                        }
                        try (PreparedStatement dp = conn.prepareStatement(delPayroll)) {
                            dp.setInt(1, pid);
                            dp.executeUpdate();
                        }
                        deletedCount++;
                    }
                }
            }
            conn.commit();
            System.out.println("[PayrollDAO] deletePayroll success - empId=" + employeeId
                    + " | period=" + period + " | rows deleted=" + deletedCount);
        } catch (Exception e) {
            System.err.println("[PayrollDAO] deletePayroll failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String[]> getDeductionsByPayrollId(int payrollId) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT d.deduction_id, d.payroll_id, " +
            "       dt.deduction_name, d.amount " +
            "FROM deduction d " +
            "JOIN deduction_types dt ON d.deduction_type_id = dt.deduction_type_id " +
            "WHERE d.payroll_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payrollId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                        rs.getString("deduction_id"),
                        rs.getString("payroll_id"),
                        rs.getString("deduction_name"),
                        fmt(rs.getDouble("amount"))
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("[PayrollDAO] getDeductionsByPayrollId failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<PayrollRecord> getAllPayrollHistory() {
        List<PayrollRecord> list = new ArrayList<>();
        String sql =
            "SELECT p.payroll_id, p.employee_id, " +
            "       CONCAT(e.first_name, ' ', e.last_name) AS full_name, " +
            "       p.payroll_period, p.regular_hours_worked, p.overtime_hours_worked, " +
            "       p.gross_pay, p.total_deductions, p.net_pay, " +
            "       p.date_processed, p.remarks " +
            "FROM payroll p " +
            "JOIN employees e ON p.employee_id = e.employee_id " +
            "ORDER BY p.payroll_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(buildPayrollRecord(rs));
            }
        } catch (Exception e) {
            System.err.println("[PayrollDAO] getAllPayrollHistory failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<PayrollRecord> getPayrollHistoryByEmployee(int employeeId) {
        List<PayrollRecord> list = new ArrayList<>();
        String sql =
            "SELECT p.payroll_id, p.employee_id, " +
            "       CONCAT(e.first_name, ' ', e.last_name) AS full_name, " +
            "       p.payroll_period, p.regular_hours_worked, p.overtime_hours_worked, " +
            "       p.gross_pay, p.total_deductions, p.net_pay, " +
            "       p.date_processed, p.remarks " +
            "FROM payroll p " +
            "JOIN employees e ON p.employee_id = e.employee_id " +
            "WHERE p.employee_id = ? " +
            "ORDER BY p.payroll_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildPayrollRecord(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("[PayrollDAO] getPayrollHistoryByEmployee failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public PayrollRecord getPayrollById(int payrollId) {
        String sql =
            "SELECT p.payroll_id, p.employee_id, " +
            "       CONCAT(e.first_name, ' ', e.last_name) AS full_name, " +
            "       p.payroll_period, p.regular_hours_worked, p.overtime_hours_worked, " +
            "       p.gross_pay, p.total_deductions, p.net_pay, " +
            "       p.date_processed, p.remarks " +
            "FROM payroll p " +
            "JOIN employees e ON p.employee_id = e.employee_id " +
            "WHERE p.payroll_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payrollId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return buildPayrollRecord(rs);
            }
        } catch (Exception e) {
            System.err.println("[PayrollDAO] getPayrollById failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean payrollRecordExists(int employeeId, String payrollPeriod) {
        requireNonBlank(payrollPeriod, "payrollPeriod");

        String sql =
            "SELECT 1 FROM payroll " +
            "WHERE employee_id = ? AND payroll_period = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ps.setString(2, payrollPeriod.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("[PayrollDAO] payrollRecordExists failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    public void insertPayrollRecord(PayrollRecord record) {
        if (record == null) throw new IllegalArgumentException("PayrollRecord must not be null");

        String sql =
            "INSERT INTO payroll " +
            "  (employee_id, payroll_period, regular_hours_worked, overtime_hours_worked, " +
            "   gross_pay, total_deductions, net_pay, date_processed, remarks) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            int payrollId;
            try (PreparedStatement ps = conn.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1,    record.getEmployeeId());
                ps.setString(2, record.getPayrollPeriod());
                ps.setDouble(3, record.getRegularHoursWorked());
                ps.setDouble(4, record.getOvertimeHoursWorked());
                ps.setDouble(5, record.getGrossPay());
                ps.setDouble(6, record.getTotalDeductions());
                ps.setDouble(7, record.getNetPay());
                ps.setString(8, record.getRemarks());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conn.rollback();
                        System.err.println("[PayrollDAO] insertPayrollRecord: no generated key, rolled back");
                        return;
                    }
                    payrollId = keys.getInt(1);
                    record.setPayrollId(payrollId);
                }
            }

            insertDeduction(conn, payrollId, "SSS",             record.getSssDeduction());
            insertDeduction(conn, payrollId, "PhilHealth",      record.getPhilhealthDeduction());
            insertDeduction(conn, payrollId, "Pag-ibig",        record.getPagibigDeduction());
            insertDeduction(conn, payrollId, "Withholding Tax", record.getWithholdingTax());

            conn.commit();
            System.out.println("[PayrollDAO] insertPayrollRecord success - payrollId=" + payrollId
                    + " | empId=" + record.getEmployeeId() + " | period=" + record.getPayrollPeriod());
        } catch (Exception e) {
            System.err.println("[PayrollDAO] insertPayrollRecord failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public double[] getCompensation(String employeeId) {
        requireNonBlank(employeeId, "employeeId");

        String sql = "SELECT basic_salary, semi_monthly_rate, hourly_rate " +
                     "FROM compensation WHERE employee_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new double[]{
                        rs.getDouble("basic_salary"),
                        rs.getDouble("semi_monthly_rate"),
                        rs.getDouble("hourly_rate")
                    };
                }
            }
        } catch (Exception e) {
            System.err.println("[PayrollDAO] getCompensation failed: " + e.getMessage());
            e.printStackTrace();
        }
        return new double[]{0, 0, 0};
    }

    public double[] getBenefits(String employeeId) {
        requireNonBlank(employeeId, "employeeId");

        String sql = "SELECT cd.benefit_id, cd.amount " +
                     "FROM compensation_details cd " +
                     "JOIN compensation c ON cd.compensation_id = c.compensation_id " +
                     "WHERE c.employee_id = ?";

        double rice = 0, phone = 0, clothing = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int    benefitId = rs.getInt("benefit_id");
                    double amount    = rs.getDouble("amount");
                    switch (benefitId) {
                        case 1 -> rice     = amount; // Rice Subsidy
                        case 2 -> phone    = amount; // Phone Allowance
                        case 3 -> clothing = amount; // Clothing Allowance
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[PayrollDAO] getBenefits failed: " + e.getMessage());
            e.printStackTrace();
        }
        return new double[]{rice, phone, clothing};
    }

    public String[] getGovernmentIds(String employeeId) {
        requireNonBlank(employeeId, "employeeId");

        String sql = "SELECT sss_number, philhealth_number, tin_number, pagibig_number " +
                     "FROM employee_government_ids WHERE employee_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        nullSafe(rs.getString("sss_number")),
                        nullSafe(rs.getString("philhealth_number")),
                        nullSafe(rs.getString("tin_number")),
                        nullSafe(rs.getString("pagibig_number"))
                    };
                }
            }
        } catch (Exception e) {
            System.err.println("[PayrollDAO] getGovernmentIds failed: " + e.getMessage());
            e.printStackTrace();
        }
        return new String[]{"", "", "", ""};
    }

    public void deletePayrollRecord(int payrollId) {
        String delDed      = "DELETE FROM deduction     WHERE payroll_id = ?";
        String delOvertime = "DELETE FROM overtime_logs WHERE payroll_id = ?";
        String delPayroll  = "DELETE FROM payroll        WHERE payroll_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement dd = conn.prepareStatement(delDed)) {
                dd.setInt(1, payrollId);
                dd.executeUpdate();
            }
            try (PreparedStatement dot = conn.prepareStatement(delOvertime)) {
                dot.setInt(1, payrollId);
                dot.executeUpdate();
            }
            int rows;
            try (PreparedStatement dp = conn.prepareStatement(delPayroll)) {
                dp.setInt(1, payrollId);
                rows = dp.executeUpdate();
            }
            conn.commit();
            System.out.println("[PayrollDAO] deletePayrollRecord success - payrollId=" + payrollId
                    + " | rows deleted=" + rows);
        } catch (Exception e) {
            System.err.println("[PayrollDAO] deletePayrollRecord failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String[] buildPayrollRow(ResultSet rs) throws SQLException {
        return new String[]{
            rs.getString("payroll_id"),
            rs.getString("employee_id"),
            rs.getString("full_name"),
            rs.getString("payroll_period"),
            fmt(rs.getDouble("regular_hours_worked")),
            fmt(rs.getDouble("overtime_hours_worked")),
            fmt(rs.getDouble("gross_pay")),
            fmt(rs.getDouble("total_deductions")),
            fmt(rs.getDouble("net_pay"))
        };
    }

    private PayrollRecord buildPayrollRecord(ResultSet rs) throws SQLException {
        PayrollRecord record = new PayrollRecord();

        record.setPayrollId(rs.getInt("payroll_id"));
        record.setEmployeeId(rs.getInt("employee_id"));
        record.setEmployeeName(rs.getString("full_name"));
        record.setPayrollPeriod(rs.getString("payroll_period"));
        record.setRegularHoursWorked(rs.getDouble("regular_hours_worked"));
        record.setOvertimeHoursWorked(rs.getDouble("overtime_hours_worked"));
        record.setGrossPay(rs.getDouble("gross_pay"));
        record.setTotalDeductions(rs.getDouble("total_deductions"));
        record.setNetPay(rs.getDouble("net_pay"));

        try { record.setDateProcessed(rs.getTimestamp("date_processed")); }
        catch (Exception ignored) {}

        try { record.setRemarks(rs.getString("remarks")); }
        catch (Exception ignored) {}

        applyDeductionBreakdown(record);
        return record;
    }

    /** Fills in sss/philhealth/pagibig/tax deduction fields on the record. */
    private void applyDeductionBreakdown(PayrollRecord record) {
        for (String[] d : getDeductionsByPayrollId(record.getPayrollId())) {
            String name = d[2] == null ? "" : d[2].trim();
            double amount;
            try { amount = Double.parseDouble(d[3]); } catch (Exception e) { amount = 0.0; }

            if (name.equalsIgnoreCase("SSS")) {
                record.setSssDeduction(amount);
            } else if (name.equalsIgnoreCase("PhilHealth")) {
                record.setPhilhealthDeduction(amount);
            } else if (name.equalsIgnoreCase("Pag-ibig")) {
                record.setPagibigDeduction(amount);
            } else if (name.equalsIgnoreCase("Withholding Tax")) {
                record.setWithholdingTax(amount);
            }
        }
    }

    private void insertDeduction(Connection conn,
                                 int payrollId,
                                 String deductionName,
                                 double amount) throws SQLException {
        int typeId = -1;
        String lookupSql =
            "SELECT deduction_type_id FROM deduction_types " +
            "WHERE deduction_name = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(lookupSql)) {
            ps.setString(1, deductionName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) typeId = rs.getInt("deduction_type_id");
            }
        }

        if (typeId == -1) {
            System.err.println("[PayrollDAO] insertDeduction: unknown type '" +
                    deductionName + "' — skipping.");
            return;
        }

        String sql =
            "INSERT INTO deduction (payroll_id, deduction_type_id, amount) " +
            "VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payrollId);
            ps.setInt(2, typeId);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        }
    }

    private String fmt(double v) {
        return String.format("%.2f", v);
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}