package com.mycompany.motorphapps.payroll;

import com.mycompany.motorphapps.dao.CompensationDAO;

/**
 * Payroll calculator for employees with overtime (OT at 1.5× rate).
 *
 * Deduction methods now delegate to {@link CompensationDAO} for bracket-based
 * SSS / PhilHealth / Pag-IBIG lookups, and use the TRAIN Law annual bracket
 * table for withholding tax — replacing the previous flat-rate approximations.
 */
public class OvertimePayrollCalculator implements PayrollCalculator {

    private final CompensationDAO compensationDAO;

    public OvertimePayrollCalculator() {
        this.compensationDAO = new CompensationDAO();
    }

    /** Constructor for dependency injection (e.g. in tests). */
    public OvertimePayrollCalculator(CompensationDAO compensationDAO) {
        this.compensationDAO = compensationDAO;
    }

    // ── Gross ─────────────────────────────────────────────────────────────────

    /**
     * Computes gross pay including overtime premium (1.5× for hours beyond 8).
     *
     * @param hoursWorked  total hours worked in the period
     * @param ratePerHour  the employee's hourly rate
     */
    @Override
    public double calculateGross(double hoursWorked, double ratePerHour) {
        double regularHours  = Math.min(hoursWorked, 8);
        double overtimeHours = Math.max(hoursWorked - 8, 0);

        double regularPay    = regularHours * ratePerHour;
        double overtimePay   = overtimeHours * ratePerHour * 1.5;

        return regularPay + overtimePay;
    }

    // ── Deductions (bracket-based) ────────────────────────────────────────────

    /**
     * BIR withholding tax — TRAIN Law (RR 8-2018) annual brackets, returned
     * as the monthly equivalent.
     */
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

    /** SSS employee contribution — from sss_contributions bracket table. */
    @Override
    public double calculateSSS(double grossMonthly) {
        return compensationDAO.getSSSContribution(grossMonthly);
    }

    /** PhilHealth employee share — from philhealth_contributions bracket table. */
    @Override
    public double calculatePhilHealth(double grossMonthly) {
        return compensationDAO.getPhilHealthContribution(grossMonthly);
    }

    /** Pag-IBIG employee contribution — from pagibig_contributions bracket table. */
    @Override
    public double calculatePagibig(double grossMonthly) {
        return compensationDAO.getPagibigContribution(grossMonthly);
    }
}
