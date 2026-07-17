package com.mycompany.motorphapps.model;

import java.util.List;

/**
 * Everything the GUI needs to render the summary section.
 * Built entirely by the service layer — the panel just reads fields.
 */
public class AttendanceSummaryResult {
    private final String  summaryLabel;       // section heading
    private final String  myTotalHours;       // current user's formatted total
    private final boolean showAllTotal;       // whether to show company-wide total
    private final String  allEmployeesTotal;  // company-wide formatted total (or "")
    private final boolean showCalculateBtn;   // whether Finance/Admin button appears
    private final List<EmployeeHoursRow> employeeBreakdown; // for the dialog table

    public AttendanceSummaryResult(String summaryLabel, String myTotalHours,
                                   boolean showAllTotal, String allEmployeesTotal,
                                   boolean showCalculateBtn,
                                   List<EmployeeHoursRow> employeeBreakdown) {
        this.summaryLabel        = summaryLabel;
        this.myTotalHours        = myTotalHours;
        this.showAllTotal        = showAllTotal;
        this.allEmployeesTotal   = allEmployeesTotal;
        this.showCalculateBtn    = showCalculateBtn;
        this.employeeBreakdown   = employeeBreakdown;
    }

    public String  getSummaryLabel()        { return summaryLabel; }
    public String  getMyTotalHours()        { return myTotalHours; }
    public boolean isShowAllTotal()         { return showAllTotal; }
    public String  getAllEmployeesTotal()   { return allEmployeesTotal; }
    public boolean isShowCalculateBtn()     { return showCalculateBtn; }
    public List<EmployeeHoursRow> getEmployeeBreakdown() { return employeeBreakdown; }

    // ── Inner DTO for each row in the breakdown dialog ──────────────────────
    public static class EmployeeHoursRow {
        private final String employeeId;
        private final String employeeName;
        private final String totalHours;

        public EmployeeHoursRow(String employeeId, String employeeName, String totalHours) {
            this.employeeId   = employeeId;
            this.employeeName = employeeName;
            this.totalHours   = totalHours;
        }

        public String getEmployeeId()   { return employeeId; }
        public String getEmployeeName() { return employeeName; }
        public String getTotalHours()   { return totalHours; }
    }
}
