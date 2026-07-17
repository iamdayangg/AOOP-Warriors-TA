package com.mycompany.motorphapps.model;

/**
 * A single attendance record ready for display.
 * Contains pre-computed style values — the GUI reads and applies them, makes no decisions.
 */
public class AttendanceRow {
    private final String  employeeId;
    private final String  employeeName;
    private final String  date;
    private final String  timeIn;
    private final String  timeOut;
    private final String  totalHours;
    private final String  overtimeStatus;
    private final boolean hasOvertime;
    private final int[]   statusBackgroundRGB;  // pre-computed by service
    private final int[]   statusForegroundRGB;  // pre-computed by service
    private final boolean statusBold;           // pre-computed by service

    public AttendanceRow(String employeeId, String employeeName, String date,
                         String timeIn, String timeOut, String totalHours,
                         OvertimeResult ot) {
        this.employeeId          = employeeId;
        this.employeeName        = employeeName;
        this.date                = date;
        this.timeIn              = timeIn;
        this.timeOut             = timeOut;
        this.totalHours          = totalHours;
        this.overtimeStatus      = ot.getOvertimeStatus();
        this.hasOvertime         = ot.hasOvertime();
        this.statusBackgroundRGB = ot.getStatusBackgroundRGB();
        this.statusForegroundRGB = ot.getStatusForegroundRGB();
        this.statusBold          = ot.isStatusBold();
    }

    public String  getEmployeeId()          { return employeeId; }
    public String  getEmployeeName()        { return employeeName; }
    public String  getDate()                { return date; }
    public String  getTimeIn()              { return timeIn; }
    public String  getTimeOut()             { return timeOut; }
    public String  getTotalHours()          { return totalHours; }
    public String  getOvertimeStatus()      { return overtimeStatus; }
    public boolean hasOvertime()            { return hasOvertime; }
    public int[]   getStatusBackgroundRGB() { return statusBackgroundRGB; }
    public int[]   getStatusForegroundRGB() { return statusForegroundRGB; }
    public boolean isStatusBold()           { return statusBold; }
}
