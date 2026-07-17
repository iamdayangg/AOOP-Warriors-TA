package com.mycompany.motorphapps.report;

import com.mycompany.motorphapps.dao.PayrollDAO;
import com.mycompany.motorphapps.service.EmployeeService;
import com.mycompany.motorphapps.service.PayrollService;
import com.mycompany.motorphapps.model.TimecardRow;
import com.mycompany.motorphapps.service.AttendanceService;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final PayrollDAO      payrollDAO      = new PayrollDAO();
    private final EmployeeService employeeService = new EmployeeService();
    private final PayrollService  payrollService  = new PayrollService();

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMMM d, yyyy");

    public void generatePayslip(String requestingEmpId,
                                String role,
                                String targetEmpId,
                                String period,
                                File outputFile) throws ReportException {

        if (!payrollService.canViewAllPayroll(role) &&
                !requestingEmpId.equals(targetEmpId)) {
            throw new ReportException("Access denied.");
        }

        String[] payRow = payrollDAO.findPayrollRow(targetEmpId, period);
        if (payRow == null) {
            throw new ReportException(
                "Payroll record not found for employee " + targetEmpId +
                " and period \"" + period + "\".");
        }

        String[] empRow = findEmployeeRow(targetEmpId);
        if (empRow == null) {
            throw new ReportException("Employee not found: " + targetEmpId);
        }

        // ── Pull salary from compensation table ──
        double[] comp          = payrollDAO.getCompensation(targetEmpId);
        double semiMonthlyRate = comp[1];
        double hourlyRate      = comp[2];
        double dailyRate       = hourlyRate * 8;

        double overtimeHours = parseDouble(payRow[5]);
        double overtimePay   = overtimeHours * hourlyRate;

        // ── Benefits from compensation_details ──
        double[] benefits    = payrollDAO.getBenefits(targetEmpId);
        double riceSubsidy   = benefits[0];
        double phoneAllow    = benefits[1];
        double clothingAllow = benefits[2];
        double totalBenefits = riceSubsidy + phoneAllow + clothingAllow;

        double gross = parseDouble(payRow[6]);
        double net   = parseDouble(payRow[8]);

        // ── Deductions ──
        int payrollId;
        try { payrollId = Integer.parseInt(payRow[0].trim()); }
        catch (Exception e) { payrollId = -1; }

        List<String[]> deductionRows = (payrollId != -1)
                ? payrollDAO.getDeductionsByPayrollId(payrollId)
                : Collections.emptyList();
        Map<String, Double> ded = extractDeductions(deductionRows);

        // ── Gov IDs ──
        String[] govIds = payrollDAO.getGovernmentIds(targetEmpId);

        // ── Days worked — attendance first, fall back to regular_hours / 8 ──
        AttendanceService attendanceService = new AttendanceService();
        List<TimecardRow> timecardRows = attendanceService.getTimecardRows(targetEmpId, period);
        int daysWorked = (int) timecardRows.stream()
                .filter(r -> r.getTotalHours() > 0)
                .count();
        if (daysWorked == 0) {
            double regularHours = parseDouble(payRow[4]);
            daysWorked = regularHours > 0 ? (int)(regularHours / 8) : 0;
        }

        // ── Build params map ──
        Map<String, Object> params = new HashMap<>();

        params.put("EMPLOYEE_ID",   payRow[1]);
        params.put("EMPLOYEE_NAME", payRow[2]);
        params.put("POSITION",      safeCol(empRow, 11));
        params.put("DEPARTMENT",    deriveEmployeeDepartment(empRow));
        params.put("PERIOD",        period);
        params.put("PAYSLIP_NO",    "PS-" + payRow[0]);

        String[] periodParts = period.split("\\s+to\\s+");
        if (periodParts.length == 2) {
            params.put("PERIOD_START", periodParts[0].trim());
            params.put("PERIOD_END",   periodParts[1].trim());
        } else {
            params.put("PERIOD_START", period);
            params.put("PERIOD_END",   period);
        }

        params.put("BASIC_SALARY",   semiMonthlyRate);
        params.put("DAILY_RATE",     dailyRate);
        params.put("DAYS_WORKED",    (double) daysWorked);
        params.put("OVERTIME",       overtimePay);

        params.put("RICE_SUBSIDY",   riceSubsidy);
        params.put("PHONE_ALLOW",    phoneAllow);
        params.put("CLOTHING_ALLOW", clothingAllow);
        params.put("TOTAL_BENEFITS", totalBenefits);

        params.put("GROSS_PAY",      gross);
        params.put("GROSS_INCOME",   gross);
        params.put("NET_PAY",        net);

        params.put("TAX",            ded.get("tax"));
        params.put("SSS",            ded.get("sss"));
        params.put("PHILHEALTH",     ded.get("philhealth"));
        params.put("PAGIBIG",        ded.get("pagibig"));
        params.put("TOTAL_DEDUCT",   parseDouble(payRow[7]));

        params.put("SSS_NO",         govIds[0]);
        params.put("PHILHEALTH_NO",  govIds[1]);
        params.put("TIN_NO",         govIds[2]);
        params.put("PAGIBIG_NO",     govIds[3]);

        fillAndExport("/reports/payslip.jrxml",
                new JREmptyDataSource(),
                params,
                outputFile);
    }

    public void generateDepartmentReport(String role,
                                     String generatedBy,
                                     String departmentFilter,
                                     String periodFilter,
                                     File outputFile) throws ReportException {

    if (!payrollService.canViewAllPayroll(role)) {
        throw new ReportException("Access denied.");
    }

    List<String[]> allRows = payrollDAO.readAllPayroll();
    Map<String, String> deptMap = buildDepartmentMap();

    // Build employee lookup maps
    Map<String, String> nameMap = new LinkedHashMap<>();
    Map<String, String> positionMap = new LinkedHashMap<>();

    for (String[] emp : employeeService.getAllEmployees()) {
        String empId = safeCol(emp, 0);

        nameMap.put(
                empId,
                safeCol(emp, 2) + " " + safeCol(emp, 1)
        );

        // Position is column 11
        positionMap.put(empId, safeCol(emp, 11));
    }

    String deptLabel = (departmentFilter == null || departmentFilter.isBlank())
            ? "All Departments"
            : departmentFilter;

    String periodLabel = (periodFilter == null || periodFilter.isBlank())
            ? "All Periods"
            : periodFilter;

    List<String[]> filtered = allRows.stream()
            .filter(row -> {
                boolean deptOk = deptLabel.equals("All Departments")
                        || deptMap.getOrDefault(row[1], "")
                        .equalsIgnoreCase(deptLabel);

                boolean periodOk = periodLabel.equals("All Periods")
                        || row[3].equals(periodFilter);

                return deptOk && periodOk;
            })
            // Keep the newest payroll record per employee
            .collect(Collectors.toMap(
                    row -> row[1],
                    row -> row,
                    (existing, replacement) -> replacement
            ))
            .values()
            .stream()
            .sorted(Comparator.comparing(row -> row[1]))
            .collect(Collectors.toList());

    if (filtered.isEmpty()) {
        throw new ReportException("No records found.");
    }

    Map<String, Object> params = new HashMap<>();
    params.put("REPORT_TITLE", "Payroll Summary");
    params.put("DEPARTMENT", deptLabel);
    params.put("PERIOD", periodLabel);
    params.put("GENERATED_BY", generatedBy);
    params.put("GENERATED_DATE", LocalDate.now().format(DATE_FMT));

    ReportDataSource ds = new ReportDataSource(
            filtered,
            deptMap,
            nameMap,
            positionMap
    );

    fillAndExport(
            "/reports/payroll_department.jrxml",
            ds,
            params,
            outputFile
    );
}

    public void generateTimecard(String requestingEmpId,
                                 String role,
                                 String targetEmpId,
                                 String period,
                                 File outputFile) throws ReportException {

        if (!payrollService.canViewAllPayroll(role) &&
                !requestingEmpId.equals(targetEmpId)) {
            throw new ReportException("Access denied.");
        }

        String[] empRow = findEmployeeRow(targetEmpId);
        if (empRow == null) {
            throw new ReportException("Employee not found: " + targetEmpId);
        }

        AttendanceService attendanceService = new AttendanceService();
        List<TimecardRow> rows = attendanceService.getTimecardRows(targetEmpId, period);

        if (rows.isEmpty()) {
            throw new ReportException(
                "No attendance records found for employee " + targetEmpId +
                " in period \"" + period + "\".");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("EMPLOYEE_ID",   targetEmpId);
        params.put("EMPLOYEE_NAME", safeCol(empRow, 2) + " " + safeCol(empRow, 1));
        params.put("POSITION",      safeCol(empRow, 11));
        params.put("DEPARTMENT",    deriveEmployeeDepartment(empRow));

        String[] periodParts = period.split("\\s+to\\s+");
        params.put("PERIOD_END", periodParts.length == 2 ? periodParts[1].trim() : period);

        fillAndExport("/reports/timecard.jrxml",
                new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(rows),
                params,
                outputFile);
    }

    private String[] findEmployeeRow(String empId) {
        return employeeService.getAllEmployees().stream()
                .filter(e -> e[0].trim().equals(empId.trim()))
                .findFirst()
                .orElse(null);
    }

    public Map<String, String> buildDepartmentMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (String[] emp : employeeService.getAllEmployees()) {
            map.put(emp[0], deriveEmployeeDepartment(emp));
        }
        return map;
    }

    public List<String> getAllDepartments() {
        return buildDepartmentMap().values().stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public String deriveEmployeeDepartment(String[] empRow) {
        String pos = safeCol(empRow, 11).toLowerCase();

        if (pos.contains("hr") || pos.contains("human resource"))
            return "Human Resources";
        if (pos.contains("payroll") || pos.contains("finance") ||
            pos.contains("accounting") || pos.contains("chief finance"))
            return "Finance & Accounting";
        if (pos.contains("it") || pos.contains("technical"))
            return "IT";
        if (pos.contains("account manager") || pos.contains("account team") ||
            pos.contains("account rank") || pos.contains("sales"))
            return "Sales & Marketing";
        if (pos.contains("marketing") || pos.contains("chief marketing"))
            return "Sales & Marketing";
        if (pos.contains("chief") || pos.contains("master") ||
            pos.contains("co-founder") || pos.contains("operating") ||
            pos.contains("executive"))
            return "Executive";
        return "General";
    }

    static Map<String, Double> extractDeductions(List<String[]> deductions) {
        Map<String, Double> result = new HashMap<>();
        result.put("sss",        0.0);
        result.put("philhealth", 0.0);
        result.put("pagibig",    0.0);
        result.put("tax",        0.0);

        if (deductions == null) return result;

        for (String[] d : deductions) {
            if (d == null || d.length < 4) continue;

            String name = d[2] == null ? "" : d[2].toLowerCase().trim();
            double amount;
            try {
                amount = Double.parseDouble(
                        d[3].replace(",", "").replace("\u20b1", "").trim());
            } catch (Exception e) {
                amount = 0.0;
            }

            if (name.contains("sss") || name.contains("social security")) {
                result.put("sss", amount);
            } else if (name.contains("philhealth") || name.contains("phil health")) {
                result.put("philhealth", amount);
            } else if (name.contains("pag-ibig") || name.contains("pagibig")
                    || name.contains("pag ibig")) {
                result.put("pagibig", amount);
            } else if (name.contains("tax")) {
                result.put("tax", amount);
            }
        }
        return result;
    }

    private void fillAndExport(String resource,
                               JRDataSource ds,
                               Map<String, Object> params,
                               File outputFile) throws ReportException {
        try (InputStream is = getClass().getResourceAsStream(resource)) {

            if (is == null) {
                throw new ReportException(
                    "JRXML not found on classpath: " + resource +
                    ". Make sure it is in src/main/resources/reports/");
            }

            JasperReport report = JasperCompileManager.compileReport(is);
            JasperPrint  print  = JasperFillManager.fillReport(report, params, ds);

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(
                    new SimpleOutputStreamExporterOutput(outputFile));
            exporter.setConfiguration(new SimplePdfExporterConfiguration());
            exporter.exportReport();

        } catch (ReportException re) {
            throw re;
        } catch (Exception e) {
            throw new ReportException("Report generation failed: " + e.getMessage(), e);
        }
    }

    private String safeCol(String[] arr, int idx) {
        return (arr != null && idx < arr.length && arr[idx] != null)
                ? arr[idx].trim() : "";
    }

    private double parseDouble(String s) {
        try {
            return Double.parseDouble(
                    s.replace(",", "").replace("\u20b1", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}