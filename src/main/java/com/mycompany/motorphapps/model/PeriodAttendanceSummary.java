package com.mycompany.motorphapps.model;

public class PeriodAttendanceSummary {

    private final String  employeeId;
    private final String  period;
    private final double  totalHoursWorked;
    private final double  regularHours;
    private final double  overtimeHours;
    private final boolean hasOvertime;
    private final int     daysWorked;

    /** Regular hours cap per day (8h). */
    public static final double REGULAR_HOURS_PER_DAY = 8.0;
    /** Overtime multiplier (1.5x). */
    public static final double OT_MULTIPLIER = 1.5;

    public PeriodAttendanceSummary(String employeeId, String period,
                                   double totalHoursWorked, int daysWorked) {
        this.employeeId       = employeeId;
        this.period           = period;
        this.daysWorked       = daysWorked;
        this.totalHoursWorked = totalHoursWorked;

        // Regular = all hours capped at 8h/day
        double maxRegular = daysWorked * REGULAR_HOURS_PER_DAY;
        this.regularHours  = Math.min(totalHoursWorked, maxRegular);
        this.overtimeHours = Math.max(totalHoursWorked - maxRegular, 0);
        this.hasOvertime   = overtimeHours > 0;
    }

    public String  getEmployeeId()       { return employeeId; }
    public String  getPeriod()           { return period; }
    public double  getTotalHoursWorked() { return totalHoursWorked; }
    public double  getRegularHours()     { return regularHours; }
    public double  getOvertimeHours()    { return overtimeHours; }
    public boolean hasOvertime()         { return hasOvertime; }
    public int     getDaysWorked()       { return daysWorked; }

    /**
     * Compute regular pay from hourly rate.
     */
    public double computeRegularPay(double hourlyRate) {
        return regularHours * hourlyRate;
    }

    /**
     * Compute overtime pay using 1.5x multiplier.
     */
    public double computeOvertimePay(double hourlyRate) {
        return overtimeHours * hourlyRate * OT_MULTIPLIER;
    }

    /**
     * Compute total gross pay (regular + overtime).
     */
    public double computeGross(double hourlyRate) {
        return computeRegularPay(hourlyRate) + computeOvertimePay(hourlyRate);
    }

    /** Human-readable summary for display. */
    public String getOvertimeSummary() {
        if (!hasOvertime) return "No overtime";
        return String.format("%.2f OT hrs × 1.5 rate", overtimeHours);
    }
}
