package com.mycompany.motorphapps.model;
/**
 *
 * @author DAYANG GWAPA
 */
public class LeaveRequest {

    private String id;
    private String employeeId;
    private String leaveType;
    private String startDate;
    private String endDate;
    private String status;

    public LeaveRequest(String id, String employeeId, String leaveType,
                        String startDate, String endDate, String status) {

        this.id = id;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}