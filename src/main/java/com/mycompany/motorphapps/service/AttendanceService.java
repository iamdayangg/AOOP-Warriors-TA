package com.mycompany.motorphapps.service;

import com.mycompany.motorphapps.model.AttendanceRow;
import com.mycompany.motorphapps.model.OvertimeResult;
import com.mycompany.motorphapps.model.PeriodAttendanceSummary;
import com.mycompany.motorphapps.model.AttendanceSummary;
import com.mycompany.motorphapps.model.AttendanceSummaryFactory;
import com.mycompany.motorphapps.model.AttendanceSummaryResult;
import com.mycompany.motorphapps.model.AttendanceSummaryResult.EmployeeHoursRow;
import com.mycompany.motorphapps.dao.AttendanceDAO;
import com.mycompany.motorphapps.dao.EmployeeDAO;
import com.mycompany.motorphapps.model.Role;
import com.mycompany.motorphapps.model.TimecardRow;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class AttendanceService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT =
        DateTimeFormatter.ofPattern("HH:mm:ss");

    private final AttendanceDAO attendanceDAO;
    private final EmployeeDAO   employeeDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
        this.employeeDAO   = new EmployeeDAO();
    }

   
    public List<String[]> getAllAttendance() {
        return attendanceDAO.getAllAttendance();
    }

    public boolean recordTimeIn(String employeeId, LocalTime time) {

    String today = LocalDate.now().format(DATE_FMT);

    String timeIn = time.withSecond(0).toString();

    return attendanceDAO.logTimeIn(
            employeeId,
            today,
            timeIn
    );
}

    public boolean recordTimeOut(String employeeId, LocalTime time) {

    String today = LocalDate.now().format(DATE_FMT);

    String timeOut = time.withSecond(0).toString();

    return attendanceDAO.logTimeOut(
            employeeId,
            today,
            timeOut
    );
}

    public void deleteAttendance(String employeeId, String date) {
    attendanceDAO.deleteAttendance(employeeId, date);
}

   
    public boolean canViewAllAttendance(String roleStr) {
        Role role = Role.fromString(roleStr);
        return role.canViewAttendance() && role.canEditEmployees();
    }

    public boolean canDeleteAttendance(String roleStr) {
        return Role.fromString(roleStr).canDeleteAttendance();
    }

    
    public List<AttendanceRow> getAttendanceRows(String employeeId, String role) {
        AttendanceSummary summary = AttendanceSummaryFactory.forRole(role);
        List<String[]> logs = attendanceDAO.getAllAttendance();
        List<AttendanceRow> result = new ArrayList<>();

        for (String[] row : logs) {
            if (row.length < 5) continue;
            if (row[0].equalsIgnoreCase("Employee ID")) continue;

            String empId = row[0].trim();

           
            boolean visible = canViewAllAttendance(role) || empId.equals(employeeId);
            if (!visible) continue;

            String timeIn  = formatTime(row[3]);
            String timeOut = formatTime(row[4]);

       
            double hours = summary.computeHours(row[3], row[4]);
            String hoursStr = hours > 0 ? summary.formatHours(hours) : "\u2014";

           
            OvertimeResult ot = new OvertimeResult(empId, row[2].trim(), hours);

            result.add(new AttendanceRow(
                    empId, row[1], row[2], timeIn, timeOut, hoursStr, ot
            ));
        }
        return result;
    }

    
    public AttendanceSummaryResult getSummaryResult(String employeeId, String role) {
        AttendanceSummary summary = AttendanceSummaryFactory.forRole(role);
        List<String[]> logs = attendanceDAO.getAllAttendance();

        // Overload 3: total hours for this employee only
        double myHours = summary.computeHours(employeeId, logs);

        boolean canCalcAll = summary.canCalculateAll(role);

        // Overload 4: total hours across all employees (only computed if allowed)
        double allHours = canCalcAll ? summary.computeHours(logs) : 0.0;

        List<EmployeeHoursRow> breakdown = new ArrayList<>();
        if (canCalcAll) {
            Map<String, Double> hoursMap = buildHoursPerEmployee(logs, summary);
            for (Map.Entry<String, Double> entry : hoursMap.entrySet()) {
                String id   = entry.getKey();
                String name = getEmployeeName(id);
                String hrs  = summary.formatHours(entry.getValue());
                breakdown.add(new EmployeeHoursRow(id, name, hrs));
            }
        }

        return new AttendanceSummaryResult(
                summary.getSummaryLabel(),          // overridden label per subclass
                summary.formatHours(myHours),
                canCalcAll,
                canCalcAll ? summary.formatHours(allHours) : "",
                canCalcAll,
                breakdown
        );
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    
    private Map<String, Double> buildHoursPerEmployee(List<String[]> logs, AttendanceSummary summary) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (String[] row : logs) {
            if (row.length < 5) continue;
            String id = row[0].trim();
            if (id.isEmpty()) continue;
            // Overload 2: computeHours(String, String)
            double h = summary.computeHours(row[3], row[4]);
            result.merge(id, h, Double::sum);
        }
        return result;
    }

    private String formatTime(String raw) {
    try {
        return LocalTime.parse(raw.trim(), TIME_FMT)
                        .format(TIME_FMT);
    } catch (Exception e) {
        return "-";
    }
}
    
    private String formatTime(String raw, DateTimeFormatter outFmt) {
    try {
        return LocalTime.parse(raw.trim(), TIME_FMT).format(outFmt);
    } catch (Exception e) {
        return "-";
    }
}

    public String getEmployeeName(String employeeId) {
        List<String[]> employees = employeeDAO.getAllEmployees();
        for (String[] row : employees) {
            if (row.length >= 3 && row[0].equals(employeeId)) return row[2] + " " + row[1];
        }
        return "Unknown";
    }


    // ── Period-based overtime summary (used by PayrollService) ───────────────

    /**
     * Scans attendance logs for an employee within a period (yyyy-MM-dd to yyyy-MM-dd),
     * and returns a PeriodAttendanceSummary with regular and overtime hours.
     *
     * PayrollService calls this to get accurate OT data before computing payslip.
     */
    public PeriodAttendanceSummary getPeriodSummary(String employeeId, String period) {
        // Parse period range  e.g. "2026-03-01 to 2026-03-15"
        java.time.LocalDate periodStart = null;
        java.time.LocalDate periodEnd   = null;
        try {
            String[] parts = period.trim().split("\s+to\s+");
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
            periodStart = java.time.LocalDate.parse(parts[0].trim(), fmt);
            periodEnd   = java.time.LocalDate.parse(parts[1].trim(), fmt);
        } catch (Exception e) {
            // If period can't be parsed, return zero summary
            return new PeriodAttendanceSummary(employeeId, period, 0, 0);
        }

        List<String[]> logs = attendanceDAO.getAllAttendance();
        AttendanceSummary summary = AttendanceSummaryFactory.forRole("EMPLOYEE");

        double totalHours = 0.0;
        int    daysWorked = 0;

        for (String[] row : logs) {
            if (row.length < 5) continue;
            if (!row[0].trim().equals(employeeId)) continue;

            // Parse the attendance date
            java.time.LocalDate attendDate;
            try {
                attendDate = java.time.LocalDate.parse(
                    row[2].trim(),
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
                );
            } catch (Exception e) { continue; }

            // Only count dates within the payroll period
            if (attendDate.isBefore(periodStart) || attendDate.isAfter(periodEnd)) continue;

            // Overload 2: computeHours(String, String) — per day
            double dayHours = summary.computeHours(row[3], row[4]);
            if (dayHours > 0) {
                totalHours += dayHours;
                daysWorked++;
            }
        }

        return new PeriodAttendanceSummary(employeeId, period, totalHours, daysWorked);
    }
    
    public List<TimecardRow> getTimecardRows(String employeeId, String period) {

    List<TimecardRow> result = new ArrayList<>();

    LocalDate start;
    LocalDate end;

    try {
        String[] parts = period.split("\\s+to\\s+");
        start = LocalDate.parse(parts[0].trim());
        end = LocalDate.parse(parts[1].trim());
    } catch (Exception e) {
        return result;
    }

    List<String[]> logs = attendanceDAO.getAllAttendance();

    for (String[] row : logs) {

        if (row.length < 5) continue;
        if (row[0].equalsIgnoreCase("Employee ID")) continue;
        if (!row[0].trim().equals(employeeId)) continue;

        LocalDate date;

        try {
            date = LocalDate.parse(row[2].trim());
        } catch (Exception e) {
            continue;
        }

        if (date.isBefore(start) || date.isAfter(end)) {
            continue;
        }

        String timeIn = row[3].trim();
        String timeOut = row[4].trim();

        double hoursWorked = 0.0;

        try {

            LocalTime in = LocalTime.parse(timeIn);
            LocalTime out = LocalTime.parse(timeOut);

            long minutesWorked =
                    java.time.Duration.between(in, out).toMinutes();

            // Deduct 1-hour lunch break
            if (minutesWorked > 300) {
                minutesWorked -= 60;
            }

            hoursWorked = minutesWorked / 60.0;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String remarks;

        if (hoursWorked > 8) {

            double overtime = hoursWorked - 8;

            remarks = String.format("OT +%.2fh", overtime);

        } else if (hoursWorked >= 8) {

            remarks = "Regular";

        } else if (hoursWorked > 0) {

            remarks = "Undertime";

        } else {

            remarks = "Absent";
        }

        String dayName = date.getDayOfWeek()
                .getDisplayName(
                        java.time.format.TextStyle.SHORT,
                        java.util.Locale.ENGLISH);

        result.add(new TimecardRow(
                date.toString(),
                dayName,
                timeIn,
                timeOut,
                hoursWorked,
                remarks
        ));
    }

    return result;
}

    public OvertimeResult getOvertimeResult(String employeeId, String date) {
        List<String[]> logs = attendanceDAO.getAllAttendance();
        AttendanceSummary summary = AttendanceSummaryFactory.forRole("EMPLOYEE");
        for (String[] row : logs) {
            if (row.length < 5) continue;
            if (row[0].trim().equals(employeeId) && row[2].trim().equals(date)) {
                double hours = summary.computeHours(row[3], row[4]);
                return new OvertimeResult(employeeId, date, hours);
            }
        }
        return new OvertimeResult(employeeId, date, 0);
    }

    public java.util.Map<String, OvertimeResult> getOvertimeMap(String employeeId, String role) {
        List<String[]> logs = attendanceDAO.getAllAttendance();
        AttendanceSummary summary = AttendanceSummaryFactory.forRole(role);
        java.util.Map<String, OvertimeResult> map = new java.util.LinkedHashMap<>();
        for (String[] row : logs) {
            if (row.length < 5) continue;
            if (row[0].equalsIgnoreCase("Employee ID")) continue;
            String empId = row[0].trim();
            boolean visible = canViewAllAttendance(role) || empId.equals(employeeId);
            if (!visible) continue;
            double hours = summary.computeHours(row[3], row[4]);
            String key = empId + "|" + row[2].trim();
            map.put(key, new OvertimeResult(empId, row[2].trim(), hours));
        }
        return map;
    }
}