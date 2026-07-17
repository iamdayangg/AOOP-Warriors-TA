package com.mycompany.motorphapps.payroll;

import com.mycompany.motorphapps.dao.CompensationDAO;

/**
 * Computes statutory deductions using the official Philippine government
 * bracket tables (SSS, PhilHealth, Pag-IBIG via CompensationDAO, which
 * delegates to GovernmentContributionCalculator; withholding tax directly
 * via GovernmentContributionCalculator's TRAIN Law bracket table).
 *
 * Previously this class applied flat percentages (SSS 4.5%, PhilHealth 3%,
 * etc.). Those have been replaced with bracket-aware lookups so the amounts
 * match the government schedule used since CP1.
 */
public class DeductionCalculator {

    private final CompensationDAO compensationDAO;

    private double tax;
    private double sss;
    private double philHealth;
    private double pagibig;

    public DeductionCalculator() {
        this.compensationDAO = new CompensationDAO();
    }

    /** Constructor for dependency injection (e.g. in tests). */
    public DeductionCalculator(CompensationDAO compensationDAO) {
        this.compensationDAO = compensationDAO;
    }

    /**
     * Computes all four deductions for the given gross monthly salary.
     *
     * @param grossMonthlySalary the employee's gross pay for the month
     */
    public void computeDeductions(double grossMonthlySalary) {
        sss        = round(compensationDAO.getSSSContribution(grossMonthlySalary));
        philHealth = round(compensationDAO.getPhilHealthContribution(grossMonthlySalary));
        pagibig    = round(compensationDAO.getPagibigContribution(grossMonthlySalary));

        // Withholding tax applies to TAXABLE income (gross minus SSS,
        // PhilHealth, and Pag-IBIG employee shares) — not raw gross.
        // calculateWithholdingTaxFromGross() handles that subtraction
        // internally using the TRAIN Law (RA 10963) monthly bracket table.
        tax = round(GovernmentContributionCalculator.calculateWithholdingTaxFromGross(grossMonthlySalary));
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public double getTax()       { return tax; }
    public double getSss()       { return sss; }
    public double getPhilHealth(){ return philHealth; }
    public double getPagibig()   { return pagibig; }

    public double getTotalDeductions() {
        return round(tax + sss + philHealth + pagibig);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}