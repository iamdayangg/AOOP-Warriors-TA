package com.mycompany.motorphapps.dao;

import com.mycompany.motorphapps.model.PayrollRecord;
import java.util.List;

/**
 * Contract for payroll data access operations.
 */
public interface IPayrollDAO {

    List<String[]> readAllPayroll();

    List<String[]> readPayrollByEmployee(String employeeId);

    String[] findPayrollRow(String employeeId, String period);

    int savePayslip(String employeeId, String period,
                    double regularHours, double overtimeHours,
                    double grossPay, double sss, double philHealth,
                    double pagibig, double tax, double netPay);

    void deletePayroll(String employeeId, String period);

    List<String[]> getDeductionsByPayrollId(int payrollId);

    List<PayrollRecord> getAllPayrollHistory();

    List<PayrollRecord> getPayrollHistoryByEmployee(int employeeId);

    PayrollRecord getPayrollById(int payrollId);

    boolean payrollRecordExists(int employeeId, String payrollPeriod);

    void insertPayrollRecord(PayrollRecord record);

    double[] getCompensation(String employeeId);

    double[] getBenefits(String employeeId);

    String[] getGovernmentIds(String employeeId);

    void deletePayrollRecord(int payrollId);
}
