package com.mycompany.motorphapps.model;

public class TimecardRow {

    private String date;
    private String day;
    private String timeIn;
    private String breakOut;
    private String breakIn;
    private String timeOut;
    private double totalHours;
    private String remarks;

    public TimecardRow(
            String date,
            String day,
            String timeIn,
            String timeOut,
            double totalHours,
            String remarks) {

        this.date = date;
        this.day = day;
        this.timeIn = timeIn;
        this.breakOut = "-";
        this.breakIn = "-";
        this.timeOut = timeOut;
        this.totalHours = totalHours;
        this.remarks = remarks;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getBreakOut() {
        return breakOut;
    }

    public String getBreakIn() {
        return breakIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public String getRemarks() {
        return remarks;
    }
}