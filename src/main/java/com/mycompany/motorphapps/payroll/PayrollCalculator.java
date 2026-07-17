/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.payroll;
/**
 *
 * @author DAYANG GWAPA
 */
public interface PayrollCalculator {

    double calculateGross(double basicSalary, double allowance);

    double calculateTax(double gross);

    double calculateSSS(double gross);

    double calculatePhilHealth(double gross);

    double calculatePagibig(double gross);

}