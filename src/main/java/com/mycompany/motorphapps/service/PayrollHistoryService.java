/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.service;

import com.mycompany.motorphapps.dao.EmployeeDAO;
import com.mycompany.motorphapps.dao.PayrollDAO;
import com.mycompany.motorphapps.model.PayrollRecord;
import com.mycompany.motorphapps.payroll.PayrollCalculator;
import com.mycompany.motorphapps.payroll.PayrollProcessor;
import com.mycompany.motorphapps.payroll.StandardPayrollCalculator;

import java.util.List;

/**
 * Orchestrates the payroll history feature: pulls employee compensation
 * via EmployeeDAO, runs it through a PayrollCalculator/PayrollProcessor,
 * and persists/retrieves the result via PayrollDAO.
 *
 * @author DAYANG GWAPA
 */
public class PayrollHistoryService {

    private final PayrollDAO payrollDAO;
    private final EmployeeDAO employeeDAO;
    private final PayrollProcessor processor;

    public PayrollHistoryService() {
        this(new StandardPayrollCalculator());
    }

    public PayrollHistoryService(PayrollCalculator calculator) {
        this.payrollDAO  = new PayrollDAO();
        this.employeeDAO = new EmployeeDAO();
        this.processor    = new PayrollProcessor(calculator);
    }

    public List<PayrollRecord> getAllHistory() {
        return payrollDAO.getAllPayrollHistory();
    }

    public List<PayrollRecord> getHistoryForEmployee(int employeeId) {
        return payrollDAO.getPayrollHistoryByEmployee(employeeId);
    }

    public PayrollRecord getRecordById(int payrollId) {
        return payrollDAO.getPayrollById(payrollId);
    }

    /**
     * Computes and saves a new payroll run for an employee using their
     * stored basic salary + allowances (rice/phone/clothing) from EmployeeDAO.
     */
    public PayrollRecord processNewPayroll(int employeeId, String payrollPeriod,
                                            double regularHoursWorked, double overtimeHoursWorked,
                                            String remarks) {

        if (payrollDAO.payrollRecordExists(employeeId, payrollPeriod)) {
            throw new IllegalStateException(
                "A payroll record for this employee and period already exists.");
        }

        String[] emp = employeeDAO.getEmployeeById(String.valueOf(employeeId));
        if (emp == null) {
            throw new IllegalArgumentException("Employee ID " + employeeId + " was not found.");
        }

        String employeeName  = emp[2] + " " + emp[1];           // first_name + last_name
        double basicSalary    = parseDouble(emp[13]);            // basic_salary
        double riceSubsidy    = parseDouble(emp[14]);
        double phoneAllowance = parseDouble(emp[15]);
        double clothingAllow  = parseDouble(emp[16]);
        double totalAllowance = riceSubsidy + phoneAllowance + clothingAllow;

        PayrollRecord record = processor.buildRecord(
                employeeId, employeeName, payrollPeriod,
                basicSalary, totalAllowance,
                regularHoursWorked, overtimeHoursWorked);
        record.setRemarks(remarks);

        payrollDAO.insertPayrollRecord(record);
        return record;
    }

    public void deleteRecord(int payrollId) {
        payrollDAO.deletePayrollRecord(payrollId);
    }

    private double parseDouble(String v) {
        try { return (v == null || v.isBlank()) ? 0.0 : Double.parseDouble(v); }
        catch (NumberFormatException e) { return 0.0; }
    }
}