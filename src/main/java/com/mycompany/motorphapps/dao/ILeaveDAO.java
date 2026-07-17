package com.mycompany.motorphapps.dao;

import java.util.List;

/**
 * Contract for leave request data access operations.
 */
public interface ILeaveDAO {

    List<String[]> getAllLeaves();

    List<String[]> getLeavesByEmployee(String employeeId);

    String[] getLeaveById(String leaveRequestId);

  boolean addLeave(String employeeId,
                 int leaveTypeId,
                 String startDate,
                 String endDate);

    String[] updateStatus(String leaveRequestId, String status);

    boolean deleteLeave(String leaveRequestId);
}
