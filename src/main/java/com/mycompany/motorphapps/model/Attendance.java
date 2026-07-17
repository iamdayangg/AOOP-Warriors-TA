package com.mycompany.motorphapps.model;
/**
 *
 * @author DAYANG GWAPA
 */
public class Attendance {

    private String employeeId;
    private String date;
    private String timeIn;
    private String timeOut;

    public Attendance(String employeeId, String date) {
        this.employeeId = employeeId;
        this.date = date;
        this.timeIn = "";
        this.timeOut = "";
    }

    public Attendance(String employeeId, String date, String timeIn, String timeOut) {
        this.employeeId = employeeId;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getDate() {
        return date;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void recordTimeIn(String time) {
        this.timeIn = time;
    }

    public void recordTimeOut(String time) {
        this.timeOut = time;
    }

    public boolean isComplete() {
        return !timeIn.isEmpty() && !timeOut.isEmpty();
    }
}