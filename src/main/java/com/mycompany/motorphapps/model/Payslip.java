package com.mycompany.motorphapps.model;
/**
 *
 * @author DAYANG GWAPA
 */
public class Payslip {

    private String employeeId;
    private String period;
    private double gross;
    private double deductions;
    private double net;

    // Detailed earnings breakdown fields
    private double basicSalary;
    private double overtimePay;
    private double riceAllowance;
    private double phoneAllowance;
    private double clothingAllowance;

    private String employeeName   = "";
    private String displayPeriod  = "";
    private double tax;
    private double sss;
    private double philHealth;
    private double pagibig;

    public String getEmployeeName()  { return employeeName; }
    public String getDisplayPeriod() { return displayPeriod; }
    public double getTax()           { return tax; }
    public double getSss()           { return sss; }
    public double getPhilHealth()    { return philHealth; }
    public double getPagibig()       { return pagibig; }

    public void setEmployeeName(String v)  { this.employeeName = v; }
    public void setDisplayPeriod(String v) { this.displayPeriod = v; }
    public void setTax(double v)           { this.tax = v; }
    public void setSss(double v)           { this.sss = v; }
    public void setPhilHealth(double v)    { this.philHealth = v; }
    public void setPagibig(double v)       { this.pagibig = v; }

    public Payslip(String employeeId, String period) {
        this.employeeId = employeeId;
        this.period = period;
    }

    public String getEmployeeId() { return employeeId; }
    public String getPeriod()     { return period; }
    public double getGross()      { return gross; }
    public double getDeductions() { return deductions; }
    public double getNet()        { return net; }
    public double getBasicSalary()      { return basicSalary; }
    public double getOvertimePay()      { return overtimePay; }
    public double getRiceAllowance()    { return riceAllowance; }
    public double getPhoneAllowance()   { return phoneAllowance; }
    public double getClothingAllowance(){ return clothingAllowance; }
    public double getTotalAllowances()  { return riceAllowance + phoneAllowance + clothingAllowance; }

    // ── Overtime display fields (populated by PayrollService.buildPayslipDetail) ──
    private double regularHours;
    private double overtimeHours;
    private int    daysWorked;
    private String overtimeSummary = "";
    private double hourlyRate;

    public double getRegularHours()     { return regularHours; }
    public double getOvertimeHours()    { return overtimeHours; }
    public int    getDaysWorked()       { return daysWorked; }
    public String getOvertimeSummary()  { return overtimeSummary; }
    public double getHourlyRate()       { return hourlyRate; }
    public boolean hasOvertime()        { return overtimeHours > 0; }

    public void setRegularHours(double v)    { this.regularHours = v; }
    public void setOvertimeHours(double v)   { this.overtimeHours = v; }
    public void setDaysWorked(int v)         { this.daysWorked = v; }
    public void setOvertimeSummary(String v) { this.overtimeSummary = v; }
    public void setHourlyRate(double v)      { this.hourlyRate = v; }

    // Overload 1: Gross only (no deductions)
    public void compute(double gross) {
        this.basicSalary      = gross;
        this.overtimePay      = 0;
        this.riceAllowance    = 0;
        this.phoneAllowance   = 0;
        this.clothingAllowance= 0;
        this.gross      = gross;
        this.deductions = 0;
        this.net        = gross;
    }

    // Overload 2: Gross + Deductions
    public void compute(double gross, double deductions) {
        this.basicSalary      = gross;
        this.overtimePay      = 0;
        this.riceAllowance    = 0;
        this.phoneAllowance   = 0;
        this.clothingAllowance= 0;
        this.gross      = gross;
        this.deductions = deductions;
        this.net        = gross - deductions;
    }

    // Overload 3: Basic Salary + Overtime Pay + Allowances (combined) + Deductions
    public void compute(double basicSalary, double overtimePay, double allowances, double deductions) {
        this.basicSalary      = basicSalary;
        this.overtimePay      = overtimePay;
        this.riceAllowance    = allowances;
        this.phoneAllowance   = 0;
        this.clothingAllowance= 0;
        this.gross      = basicSalary + overtimePay + allowances;
        this.deductions = deductions;
        this.net        = this.gross - deductions;
    }

    // Overload 4: Basic Salary + Overtime Pay + Rice + Phone + Clothing Allowances + Deductions
    public void compute(double basicSalary, double overtimePay,
                        double riceAllowance, double phoneAllowance,
                        double clothingAllowance, double deductions) {
        this.basicSalary       = basicSalary;
        this.overtimePay       = overtimePay;
        this.riceAllowance     = riceAllowance;
        this.phoneAllowance    = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.gross      = basicSalary + overtimePay + riceAllowance + phoneAllowance + clothingAllowance;
        this.deductions = deductions;
        this.net        = this.gross - deductions;
    }
}
