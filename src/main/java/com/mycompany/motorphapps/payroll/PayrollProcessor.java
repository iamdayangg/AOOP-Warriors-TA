/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.payroll;

import com.mycompany.motorphapps.model.PayrollRecord;

/**
 * Runs an employee's salary inputs through a PayrollCalculator strategy
 * (StandardPayrollCalculator, OvertimePayrollCalculator, etc.) and
 * assembles a ready-to-save PayrollRecord.
 *
 * @author DAYANG GWAPA
 */
public class PayrollProcessor {

    private final PayrollCalculator calculator;

    public PayrollProcessor() {
        this(new StandardPayrollCalculator());
    }

    public PayrollProcessor(PayrollCalculator calculator) {
        this.calculator = calculator;
    }

    /**
     * @param grossInput1 basic salary (StandardPayrollCalculator) or hours worked (OvertimePayrollCalculator)
     * @param grossInput2 total allowance (StandardPayrollCalculator) or rate per hour (OvertimePayrollCalculator)
     */
    public PayrollRecord buildRecord(int employeeId, String employeeName, String payrollPeriod,
                                      double grossInput1, double grossInput2,
                                      double regularHoursWorked, double overtimeHoursWorked) {

        double gross = round(calculator.calculateGross(grossInput1, grossInput2));

        double sss        = round(calculator.calculateSSS(gross));
        double philHealth = round(calculator.calculatePhilHealth(gross));
        double pagibig     = round(calculator.calculatePagibig(gross));
        double tax          = round(calculator.calculateTax(gross));

        double totalDeductions = round(sss + philHealth + pagibig + tax);
        double netPay = round(gross - totalDeductions);

        PayrollRecord record = new PayrollRecord();
        record.setEmployeeId(employeeId);
        record.setEmployeeName(employeeName);
        record.setPayrollPeriod(payrollPeriod);
        record.setRegularHoursWorked(regularHoursWorked);
        record.setOvertimeHoursWorked(overtimeHoursWorked);
        record.setGrossPay(gross);
        record.setSssDeduction(sss);
        record.setPhilhealthDeduction(philHealth);
        record.setPagibigDeduction(pagibig);
        record.setWithholdingTax(tax);
        record.setTotalDeductions(totalDeductions);
        record.setNetPay(netPay);
        return record;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}