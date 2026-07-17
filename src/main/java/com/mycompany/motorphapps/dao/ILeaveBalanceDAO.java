package com.mycompany.motorphapps.dao;

import java.util.List;

/**
 * Contract for leave balance data access operations.
 */
public interface ILeaveBalanceDAO {

    List<String[]> getAllLeaveBalances();

    double getBalance(String employeeId, int leaveTypeId);

    List<String[]> getAllLeaveTypes();

    List<String[]> getBalancesForEmployee(String employeeId);

    boolean deductLeave(String employeeId, int leaveTypeId, double days);

    void restoreLeave(String employeeId, int leaveTypeId, double days);

    void seedBalancesForNewEmployee(String employeeId);

    void resetAllBalances();
}
