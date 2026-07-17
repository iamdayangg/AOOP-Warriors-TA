package com.mycompany.motorphapps.model;

/**
 * Holds the overtime detection result for one attendance record.
 * Built entirely by AttendanceService — the GUI just reads these fields.
 */
public class OvertimeResult {

    public static final double REGULAR_HOURS = 8.0;

    private final String employeeId;
    private final String date;
    private final double totalHours;
    private final double regularHours;
    private final double overtimeHours;
    private final boolean hasOvertime;

    public OvertimeResult(String employeeId, String date, double totalHours) {
        this.employeeId    = employeeId;
        this.date          = date;
        this.totalHours    = totalHours;
        this.regularHours  = Math.min(totalHours, REGULAR_HOURS);
        this.overtimeHours = Math.max(totalHours - REGULAR_HOURS, 0);
        this.hasOvertime   = overtimeHours > 0;
    }

    public String  getEmployeeId()   { return employeeId; }
    public String  getDate()         { return date; }
    public double  getTotalHours()   { return totalHours; }
    public double  getRegularHours() { return regularHours; }
    public double  getOvertimeHours(){ return overtimeHours; }
    public boolean hasOvertime()     { return hasOvertime; }

    /** Ready-to-display status label. */
    public String getOvertimeStatus() {
        if (totalHours == 0)    return "No Over Time";
        if (!hasOvertime)       return "Regular";
        return String.format("OT +%.2fh", overtimeHours);
    }

    /**
     * Row background color for the OT Status cell.
     * GUI reads this directly — no color decisions in the panel.
     * Returns int[]{r, g, b}.
     */
    public int[] getStatusBackgroundRGB() {
        if (totalHours == 0)  return new int[]{255, 235, 235}; // red tint   — No Time Out
        if (hasOvertime)      return new int[]{255, 243, 220}; // orange tint — OT
        return new int[]{240, 255, 240};                        // green tint  — Regular
    }

    
    public int[] getStatusForegroundRGB() {
        if (totalHours == 0)  return new int[]{180, 0,   0};   // dark red
        if (hasOvertime)      return new int[]{200, 100, 0};   // dark orange
        return new int[]{46,  160, 67};                         // dark green
    }

   
    public boolean isStatusBold() {
        return hasOvertime || totalHours == 0;
    }
}
