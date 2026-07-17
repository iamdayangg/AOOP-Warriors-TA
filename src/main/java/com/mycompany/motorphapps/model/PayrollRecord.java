package com.mycompany.motorphapps.model;

import java.sql.Timestamp;

/**
 * Represents one finalized payroll run for a single employee and pay period.
 * Maps to the existing `payroll` table plus its related `deduction` rows.
 *
 * @author DAYANG GWAPA
 */
public class PayrollRecord {

    private int payrollId;
    private int employeeId;
    private String employeeName;
    private String payrollPeriod;

    private double regularHoursWorked;
    private double overtimeHoursWorked;

    private double grossPay;
    private double sssDeduction;
    private double philhealthDeduction;
    private double pagibigDeduction;
    private double withholdingTax;
    private double totalDeductions;
    private double netPay;

    private Timestamp dateProcessed;
    private String remarks;

    public PayrollRecord() {
    }

    public PayrollRecord(int employeeId, String employeeName, String payrollPeriod,
                          double regularHoursWorked, double overtimeHoursWorked) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.payrollPeriod = payrollPeriod;
        this.regularHoursWorked = regularHoursWorked;
        this.overtimeHoursWorked = overtimeHoursWorked;
    }

    public int getPayrollId() { return payrollId; }
    public void setPayrollId(int payrollId) { this.payrollId = payrollId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    
    public String getFullName() {
        return employeeName;
}

    public void setFullName(String fullName) {
        this.employeeName = fullName;
}

    public String getPayrollPeriod() { return payrollPeriod; }
    public void setPayrollPeriod(String payrollPeriod) { this.payrollPeriod = payrollPeriod; }

    public double getRegularHoursWorked() { return regularHoursWorked; }
    public void setRegularHoursWorked(double regularHoursWorked) { this.regularHoursWorked = regularHoursWorked; }

    public double getOvertimeHoursWorked() { return overtimeHoursWorked; }
    public void setOvertimeHoursWorked(double overtimeHoursWorked) { this.overtimeHoursWorked = overtimeHoursWorked; }

    public double getGrossPay() { return grossPay; }
    public void setGrossPay(double grossPay) { this.grossPay = grossPay; }

    public double getSssDeduction() { return sssDeduction; }
    public void setSssDeduction(double sssDeduction) { this.sssDeduction = sssDeduction; }

    public double getPhilhealthDeduction() { return philhealthDeduction; }
    public void setPhilhealthDeduction(double philhealthDeduction) { this.philhealthDeduction = philhealthDeduction; }

    public double getPagibigDeduction() { return pagibigDeduction; }
    public void setPagibigDeduction(double pagibigDeduction) { this.pagibigDeduction = pagibigDeduction; }

    public double getWithholdingTax() { return withholdingTax; }
    public void setWithholdingTax(double withholdingTax) { this.withholdingTax = withholdingTax; }

    public double getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(double totalDeductions) { this.totalDeductions = totalDeductions; }

    public double getNetPay() { return netPay; }
    public void setNetPay(double netPay) { this.netPay = netPay; }

    public Timestamp getDateProcessed() { return dateProcessed; }
    public void setDateProcessed(Timestamp dateProcessed) { this.dateProcessed = dateProcessed; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    @Override
    public String toString() {
        return "PayrollRecord{" +
                "payrollId=" + payrollId +
                ", employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", payrollPeriod='" + payrollPeriod + '\'' +
                ", netPay=" + netPay +
                '}';
    }
}