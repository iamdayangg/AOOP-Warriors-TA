package com.mycompany.motorphapps.model;

/**
 *
 * @author DAYANG GWAPA
 */
public class Employee extends Person {

    private double basicSalary;
    private double riceAllowance;
    private double phoneAllowance;
    private double clothingAllowance;


    public Employee(String employeeId, String firstName, String lastName) {
        super(employeeId, firstName, lastName);
    }

    public Employee(String employeeId, String firstName, String lastName, double basicSalary) {
        super(employeeId, firstName, lastName);
        this.basicSalary = basicSalary;
    }


    @Override
    public String getRole() { return "EMPLOYEE"; }

    @Override
    public Role getPermissions() { return Role.EMPLOYEE; }


    public double getBasicSalary() { return basicSalary; }
    public void   setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    public double getRiceAllowance() { return riceAllowance; }
    public void   setRiceAllowance(double riceAllowance) { this.riceAllowance = riceAllowance; }

    public double getPhoneAllowance() { return phoneAllowance; }
    public void   setPhoneAllowance(double phoneAllowance) { this.phoneAllowance = phoneAllowance; }

    public double getClothingAllowance() { return clothingAllowance; }
    public void   setClothingAllowance(double clothingAllowance) { this.clothingAllowance = clothingAllowance; }

    public double getTotalAllowances() {
        return riceAllowance + phoneAllowance + clothingAllowance;
    }
}
