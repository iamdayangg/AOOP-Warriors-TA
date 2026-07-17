package com.mycompany.motorphapps.service;

import com.mycompany.motorphapps.config.DBConnection;
import com.mycompany.motorphapps.dao.CompensationDAO;
import com.mycompany.motorphapps.dao.EmployeeDAO;
import com.mycompany.motorphapps.dao.PayrollDAO;
import com.mycompany.motorphapps.model.PeriodAttendanceSummary;
import com.mycompany.motorphapps.model.Payslip;
import com.mycompany.motorphapps.model.Role;
import com.mycompany.motorphapps.payroll.DeductionCalculator;
import com.mycompany.motorphapps.payroll.PayrollCalculator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollService {

    private final PayrollDAO        payrollDAO;
    private final EmployeeDAO       employeeDAO;
    private final CompensationDAO   compensationDAO;
    private final AttendanceService attendanceService;

    public PayrollService() {
        this.payrollDAO        = new PayrollDAO();
        this.employeeDAO       = new EmployeeDAO();
        this.compensationDAO   = new CompensationDAO();
        this.attendanceService = new AttendanceService();
    }

    // =========================================================
    // GET ALL PAYROLL
    // Returns 10-element rows:
    //  [0] employee_id   [1] full_name      [2] payroll_period
    //  [3] gross_pay     [4] tax            [5] sss
    //  [6] philhealth    [7] pagibig        [8] net_pay
    //  [9] payroll_id    (hidden, for delete)
    // =========================================================
    public List<String[]> getAllPayroll() {
        List<String[]> list = new ArrayList<>();

        // FIX: deduction table has deduction_type_id, not deduction_type.
        // Must JOIN deduction_types to get the name, then CASE on dt.deduction_name.
        String sql =
            "SELECT " +
            "  p.employee_id, " +
            "  CONCAT(e.first_name, ' ', e.last_name) AS full_name, " +
            "  p.payroll_period, " +
            "  p.gross_pay, " +
            "  COALESCE(MAX(CASE WHEN dt.deduction_name = 'Withholding Tax' " +
            "                    THEN d.amount END), 0) AS tax, " +
            "  COALESCE(MAX(CASE WHEN dt.deduction_name = 'SSS' " +
            "                    THEN d.amount END), 0) AS sss, " +
            "  COALESCE(MAX(CASE WHEN dt.deduction_name = 'PhilHealth' " +
            "                    THEN d.amount END), 0) AS philhealth, " +
            "  COALESCE(MAX(CASE WHEN dt.deduction_name = 'Pag-ibig' " +
            "                    THEN d.amount END), 0) AS pagibig, " +
            "  p.net_pay, " +
            "  p.payroll_id " +
            "FROM payroll p " +
            "JOIN employees e ON p.employee_id = e.employee_id " +
            "LEFT JOIN deduction d ON p.payroll_id = d.payroll_id " +
            "LEFT JOIN deduction_types dt ON d.deduction_type_id = dt.deduction_type_id " +
            "GROUP BY p.payroll_id, p.employee_id, full_name, " +
            "         p.payroll_period, p.gross_pay, p.net_pay " +
            "ORDER BY p.payroll_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("employee_id"),     // [0]
                    rs.getString("full_name"),        // [1]
                    rs.getString("payroll_period"),   // [2]
                    fmt(rs.getDouble("gross_pay")),   // [3]
                    fmt(rs.getDouble("tax")),         // [4]
                    fmt(rs.getDouble("sss")),         // [5]
                    fmt(rs.getDouble("philhealth")),  // [6]
                    fmt(rs.getDouble("pagibig")),     // [7]
                    fmt(rs.getDouble("net_pay")),     // [8]
                    rs.getString("payroll_id")        // [9] hidden
                });
            }

        } catch (Exception e) {
            System.err.println("[PayrollService] getAllPayroll failed: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    // =========================================================
    // GET PAYROLL BY EMPLOYEE
    // =========================================================
    public List<String[]> getPayrollByEmployee(String employeeId) {
        return payrollDAO.readPayrollByEmployee(employeeId);
    }

    // =========================================================
    // PROCESS / SAVE PAYROLL
    // =========================================================
    public Payslip processPayroll(
            String employeeId,
            String period,
            String rawHours,
            String rawRate,
            PayrollCalculator calculator
    ) {
        String cleanId = InputValidator.requireEmployeeId(employeeId);

        if (!employeeExists(cleanId))
            throw new IllegalArgumentException("Employee ID " + cleanId + " does not exist.");

        InputValidator.requireNonBlank(period, "Payroll Period");
        if (period.equals("Select Period"))
            throw new IllegalArgumentException("Please select a payroll period.");

        double hours = InputValidator.requireHoursWorked(rawHours);
        double rate  = InputValidator.requireHourlyRate(rawRate);
        double gross = calculator.calculateGross(hours, rate);

        if (gross <= 0)
            throw new IllegalArgumentException("Calculated gross pay must be greater than 0.");

        DeductionCalculator deductions = new DeductionCalculator();
        deductions.computeDeductions(gross);

        double tax        = deductions.getTax();
        double sss        = deductions.getSss();
        double philHealth = deductions.getPhilHealth();
        double pagibig    = deductions.getPagibig();
        double totalDed   = deductions.getTotalDeductions();
        double net        = gross - totalDed;

        PeriodAttendanceSummary attendance =
                attendanceService.getPeriodSummary(cleanId, period);
        double regularHours  = attendance.getRegularHours();
        double overtimeHours = attendance.getOvertimeHours();

        int payrollId = payrollDAO.savePayslip(
                cleanId, period,
                regularHours, overtimeHours,
                gross, sss, philHealth, pagibig, tax, net
        );

        if (payrollId == -1)
            throw new RuntimeException("Failed to save payroll record for " + cleanId);

        Payslip payslip = new Payslip(cleanId, period);
        payslip.compute(gross, totalDed);
        payslip.setTax(tax);
        payslip.setSss(sss);
        payslip.setPhilHealth(philHealth);
        payslip.setPagibig(pagibig);
        return payslip;
    }

    // =========================================================
    // BUILD FULL PAYSLIP FOR DISPLAY
    // =========================================================
    public Payslip buildPayslipDetail(String employeeId, String period) {

        String sql =
            "SELECT " +
            "  p.payroll_id, p.employee_id, p.payroll_period, " +
            "  p.gross_pay, p.net_pay, p.total_deductions, " +
            "  CONCAT(e.first_name, ' ', e.last_name) AS full_name, " +
            "  COALESCE(MAX(CASE WHEN dt.deduction_name = 'Withholding Tax' " +
            "                    THEN d.amount END), 0) AS tax, " +
            "  COALESCE(MAX(CASE WHEN dt.deduction_name = 'SSS' " +
            "                    THEN d.amount END), 0) AS sss, " +
            "  COALESCE(MAX(CASE WHEN dt.deduction_name = 'PhilHealth' " +
            "                    THEN d.amount END), 0) AS philhealth, " +
            "  COALESCE(MAX(CASE WHEN dt.deduction_name = 'Pag-ibig' " +
            "                    THEN d.amount END), 0) AS pagibig " +
            "FROM payroll p " +
            "JOIN employees e ON p.employee_id = e.employee_id " +
            "LEFT JOIN deduction d ON p.payroll_id = d.payroll_id " +
            "LEFT JOIN deduction_types dt ON d.deduction_type_id = dt.deduction_type_id " +
            "WHERE p.employee_id = ? AND p.payroll_period = ? " +
            "GROUP BY p.payroll_id, p.employee_id, p.payroll_period, " +
            "         p.gross_pay, p.net_pay, p.total_deductions, full_name " +
            "LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId.trim());
            ps.setString(2, period.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                // Use gross_pay and net_pay directly from payroll table — do NOT recompute
                double gross    = rs.getDouble("gross_pay");
                double net      = rs.getDouble("net_pay");
                double totalDed = rs.getDouble("total_deductions");
                double tax      = rs.getDouble("tax");
                double sss      = rs.getDouble("sss");
                double philHealth = rs.getDouble("philhealth");
                double pagibig  = rs.getDouble("pagibig");
                String fullName = rs.getString("full_name");

                // Pull hourly rate for overtime calculation only
                double hourlyRate = 0;
                for (String[] comp : compensationDAO.getAllCompensation()) {
                    if (comp[0].trim().equals(employeeId.trim())) {
                        hourlyRate = safeDouble(comp, 4);
                        break;
                    }
                }

                PeriodAttendanceSummary attendance =
                        attendanceService.getPeriodSummary(employeeId, period);
                double overtimePay = attendance.computeOvertimePay(hourlyRate);

                Payslip payslip = new Payslip(employeeId, period);

                // Use Overload 2: gross from DB, deductions from DB — no recomputation
                payslip.compute(gross, totalDed);

                payslip.setTax(tax);
                payslip.setSss(sss);
                payslip.setPhilHealth(philHealth);
                payslip.setPagibig(pagibig);

                payslip.setRegularHours(attendance.getRegularHours());
                payslip.setOvertimeHours(attendance.getOvertimeHours());
                payslip.setDaysWorked(attendance.getDaysWorked());
                payslip.setOvertimeSummary(attendance.getOvertimeSummary());
                payslip.setHourlyRate(hourlyRate);

                payslip.setEmployeeName(fullName);
                payslip.setDisplayPeriod(formatPeriodForDisplay(period));

                return payslip;
            }

        } catch (Exception e) {
            System.err.println("[PayrollService] buildPayslipDetail failed: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // =========================================================
    // DELETE PAYROLL
    // =========================================================
    public void deletePayroll(String employeeId, String period) {
        InputValidator.requireNonBlank(employeeId, "Employee ID");
        InputValidator.requireNonBlank(period, "Period");
        payrollDAO.deletePayroll(employeeId, period);
    }

    // =========================================================
    // ROLE CHECKS
    // =========================================================
    public boolean canProcessPayroll(String roleStr) {
        return Role.fromString(roleStr).canProcessPayroll();
    }

    public boolean canViewAllPayroll(String roleStr) {
        return Role.fromString(roleStr).canProcessPayroll();
    }

    // =========================================================
    // DEPARTMENT SUMMARY  (used by dashboard chart)
    // Returns list of [department, employee_count]
    // =========================================================
    public List<String[]> getEmployeeCountByDepartment() {
        List<String[]> result = new ArrayList<>();
        String sql =
            "SELECT department, COUNT(*) AS cnt " +
            "FROM employees " +
            "GROUP BY department " +
            "ORDER BY cnt DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new String[]{
                    rs.getString("department"),
                    rs.getString("cnt")
                });
            }
        } catch (Exception e) {
            System.err.println("[PayrollService] getEmployeeCountByDepartment failed: " + e.getMessage());
        }
        return result;
    }

    // =========================================================
    // NET PAY SUMMARY  (used by dashboard chart)
    // Returns list of [payroll_period, total_net_pay]
    // =========================================================
    public List<String[]> getNetPayByPeriod() {
        List<String[]> result = new ArrayList<>();
        String sql =
            "SELECT payroll_period, SUM(net_pay) AS total_net " +
            "FROM payroll " +
            "GROUP BY payroll_period " +
            "ORDER BY MIN(payroll_id)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new String[]{
                    rs.getString("payroll_period"),
                    rs.getString("total_net")
                });
            }
        } catch (Exception e) {
            System.err.println("[PayrollService] getNetPayByPeriod failed: " + e.getMessage());
        }
        return result;
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================
    private boolean employeeExists(String employeeId) {
        return employeeDAO.employeeExists(employeeId);
    }

    private String fmt(double v) {
        return String.format("%.2f", v);
    }

    private String formatPeriodForDisplay(String rawPeriod) {
        if (rawPeriod == null || rawPeriod.isBlank()) return "";
        String cleaned = rawPeriod.trim();
        if (cleaned.contains(" to ")) {
            try {
                String[] parts = cleaned.split("\\s+to\\s+");
                java.time.LocalDate start = java.time.LocalDate.parse(
                        parts[0].trim(),
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                java.time.LocalDate end = java.time.LocalDate.parse(
                        parts[1].trim(),
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                java.time.format.DateTimeFormatter nice =
                        java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy");
                return start.format(nice) + " \u2013 " + end.format(nice);
            } catch (Exception ignored) { }
        }
        return cleaned;
    }

    private double safeDouble(String[] arr, int i) {
        try {
            return (arr != null && i < arr.length
                    && arr[i] != null && !arr[i].isBlank())
                    ? Double.parseDouble(arr[i].trim()) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}