/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.payroll;

/**
 * Shared utility for computing Philippine statutory deductions using the
 * actual bracket / tiered tables instead of flat percentages.
 *
 * Rates verified current for 2026:
 *   - SSS:        RA 11199, SSS Circular 2024-006 (effective Jan 2025, sustained 2026)
 *                 15% total (5% employee / 10% employer) of Monthly Salary
 *                 Credit (MSC). MSC ranges P5,000 - P35,000 in P500 steps.
 *   - PhilHealth:  RA 11223 (Universal Health Care Act), 5% of monthly basic
 *                 salary, split 2.5% employee / 2.5% employer, income floor
 *                 P10,000, ceiling P100,000.
 *   - Pag-IBIG:    HDMF Circular No. 460 (effective Feb 2024, sustained 2026).
 *                 1% employee share if salary <= P1,500, else 2%. Employer
 *                 always 2%. Capped at Maximum Fund Salary (MFS) of P10,000
 *                 (i.e. max employee share P200/month).
 *   - Withholding: TRAIN Law (RA 10963) Phase 2 monthly table, effective
 *                 Jan 1 2023 and unchanged through 2026.
 *
 * NOTE: Government contribution tables change periodically via new circulars.
 * Verify against sss.gov.ph, philhealth.gov.ph, and pagibigfund.gov.ph
 * before using this in a real production payroll system.
 *
 * @author DAYANG GWAPA
 */
public class GovernmentContributionCalculator {

    private static final double SSS_MSC_MIN  = 5000.0;
    private static final double SSS_MSC_MAX  = 35000.0;
    private static final double SSS_MSC_STEP = 500.0;
    private static final double SSS_EMPLOYEE_RATE = 0.05; // 5% employee share of MSC

    public static double calculateSSS(double monthlySalary) {
        double msc = resolveMSC(monthlySalary);
        return round2(msc * SSS_EMPLOYEE_RATE);
    }

    private static double resolveMSC(double salary) {
        if (salary <= SSS_MSC_MIN) return SSS_MSC_MIN;
        if (salary >= SSS_MSC_MAX) return SSS_MSC_MAX;

        double steps = Math.ceil((salary - SSS_MSC_MIN) / SSS_MSC_STEP);
        return SSS_MSC_MIN + (steps * SSS_MSC_STEP);
    }

    private static final double PHILHEALTH_FLOOR   = 10000.0;
    private static final double PHILHEALTH_CEILING = 100000.0;
    private static final double PHILHEALTH_RATE    = 0.05;   // total rate
    private static final double PHILHEALTH_SHARE   = 0.5;    // employee pays half

    public static double calculatePhilHealth(double monthlySalary) {
        double base = clamp(monthlySalary, PHILHEALTH_FLOOR, PHILHEALTH_CEILING);
        double totalPremium = base * PHILHEALTH_RATE;
        return round2(totalPremium * PHILHEALTH_SHARE);
    }

    private static final double PAGIBIG_MFS = 10000.0;
    private static final double PAGIBIG_LOW_TIER_CEILING = 1500.0;
    private static final double PAGIBIG_LOW_TIER_RATE  = 0.01; // <= 1,500
    private static final double PAGIBIG_HIGH_TIER_RATE = 0.02; // > 1,500

    public static double calculatePagibig(double monthlySalary) {
        double base = Math.min(monthlySalary, PAGIBIG_MFS);
        double rate = (monthlySalary <= PAGIBIG_LOW_TIER_CEILING)
                ? PAGIBIG_LOW_TIER_RATE
                : PAGIBIG_HIGH_TIER_RATE;
        return round2(base * rate);
    }

    public static double calculateWithholdingTax(double taxableIncome) {
        if (taxableIncome <= 20833.0) {
            return 0.0;
        } else if (taxableIncome <= 33332.0) {
            return round2((taxableIncome - 20833.0) * 0.15);
        } else if (taxableIncome <= 66666.0) {
            return round2(1875.0 + (taxableIncome - 33333.0) * 0.20);
        } else if (taxableIncome <= 166666.0) {
            return round2(8541.80 + (taxableIncome - 66667.0) * 0.25);
        } else if (taxableIncome <= 666666.0) {
            return round2(33541.80 + (taxableIncome - 166667.0) * 0.30);
        } else {
            return round2(183541.80 + (taxableIncome - 666667.0) * 0.35);
        }
    }

    public static double calculateWithholdingTaxFromGross(double grossSalary) {
        double sss        = calculateSSS(grossSalary);
        double philHealth = calculatePhilHealth(grossSalary);
        double pagibig     = calculatePagibig(grossSalary);
        double taxable     = grossSalary - sss - philHealth - pagibig;
        return calculateWithholdingTax(Math.max(taxable, 0));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private GovernmentContributionCalculator() {
        // static utility class — no instances
    }
}