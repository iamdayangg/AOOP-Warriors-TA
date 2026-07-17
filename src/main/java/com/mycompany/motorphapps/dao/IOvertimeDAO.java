package com.mycompany.motorphapps.dao;

import java.util.List;

/**
 * Contract for overtime log data access operations.
 */
public interface IOvertimeDAO {

    List<String[]> getAllOvertimeLogs();

    List<String[]> getOvertimeByEmployee(String employeeId);

    String[] getOvertimeById(int overtimeId);

    List<String[]> getOvertimeByPayrollId(int payrollId);

    boolean addOvertimeLog(String employeeId, Integer payrollId,
                           String otDate, double hoursWorked,
                           String overtimeType);

    String[] updateStatus(int overtimeId, String status);

    void linkToPayroll(String employeeId, String otDate, int payrollId);

    boolean deleteOvertimeLog(int overtimeId);

    double getTotalApprovedOTHours(String employeeId,
                                   String startDate,
                                   String endDate);
}
