package com.mycompany.motorphapps.dao;

import java.util.List;

public interface ICompensationDAO {

    List<String[]> getBenefitsForEmployee(String employeeId);

    List<String[]> getAllCompensation();

   void syncFromEmployee(String employeeId,
                      double basicSalary,
                      double semiMonthly,
                      double hourlyRate,
                      double rice,
                      double phone,
                      double clothing);
    /** Looks up the SSS employee contribution from the bracket table. */
    double getSSSContribution(double monthlySalary);

    /** Looks up the PhilHealth employee share from the bracket table. */
    double getPhilHealthContribution(double monthlySalary);

    /** Looks up the Pag-IBIG employee contribution from the bracket table. */
    double getPagibigContribution(double monthlySalary);
}
