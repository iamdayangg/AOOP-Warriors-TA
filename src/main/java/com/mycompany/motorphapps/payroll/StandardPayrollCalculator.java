package com.mycompany.motorphapps.payroll;

import com.mycompany.motorphapps.dao.CompensationDAO;

public class StandardPayrollCalculator implements PayrollCalculator {

    private final CompensationDAO compensationDAO;

    public StandardPayrollCalculator() {
        this.compensationDAO = new CompensationDAO();
    }

    public StandardPayrollCalculator(CompensationDAO compensationDAO) {
        this.compensationDAO = compensationDAO;
    }


    @Override
    public double calculateGross(double basicSalary, double allowance) {
        return basicSalary + allowance;
    }

    @Override
    public double calculateTax(double grossMonthly) {
        double annual = grossMonthly * 12.0;
        double annualTax;

        if (annual <= 250_000) {
            annualTax = 0;
        } else if (annual <= 400_000) {
            annualTax = (annual - 250_000) * 0.15;
        } else if (annual <= 800_000) {
            annualTax = 22_500 + (annual - 400_000) * 0.20;
        } else if (annual <= 2_000_000) {
            annualTax = 102_500 + (annual - 800_000) * 0.25;
        } else if (annual <= 8_000_000) {
            annualTax = 402_500 + (annual - 2_000_000) * 0.30;
        } else {
            annualTax = 2_202_500 + (annual - 8_000_000) * 0.35;
        }

        return annualTax / 12.0;
    }

    @Override
    public double calculateSSS(double grossMonthly) {
        return compensationDAO.getSSSContribution(grossMonthly);
    }

    @Override
    public double calculatePhilHealth(double grossMonthly) {
        return compensationDAO.getPhilHealthContribution(grossMonthly);
    }

    @Override
    public double calculatePagibig(double grossMonthly) {
        return compensationDAO.getPagibigContribution(grossMonthly);
    }
}
