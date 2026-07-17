package com.mycompany.motorphapps.payroll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests — no database involved.
 * DeductionCalculator computes statutory deductions using the
 * government contribution tables.
 *
 * @author DAYANG GWAPA
 */
class DeductionCalculatorTest {

    @Test
    void computeDeductions_usesGovernmentContributionTables() {
        DeductionCalculator calc = new DeductionCalculator();
        calc.computeDeductions(5225.00);

        assertEquals(0.00, calc.getTax(), 0.001,
                "Tax should follow TRAIN Law brackets");

        assertEquals(275.00, calc.getSss(), 0.001,
                "SSS should follow the SSS contribution table");

        assertEquals(250.00, calc.getPhilHealth(), 0.001,
                "PhilHealth should follow the government contribution table");

        assertEquals(104.50, calc.getPagibig(), 0.001,
                "Pag-IBIG contribution should follow the government contribution table");
    }

    @Test
    void getTotalDeductions_equalsSumOfTheFourComponents() {
        DeductionCalculator calc = new DeductionCalculator();
        calc.computeDeductions(5225.00);

        double expectedTotal =
                calc.getSss()
                + calc.getPhilHealth()
                + calc.getPagibig()
                + calc.getTax();

        assertEquals(expectedTotal,
                calc.getTotalDeductions(),
                0.001);
    }

    @Test
    void netPay_reconcilesExactlyAtRoundingBoundary() {
        double gross = 5225.00;

        DeductionCalculator calc = new DeductionCalculator();
        calc.computeDeductions(gross);

        double net = round(gross - calc.getTotalDeductions());

        assertEquals(
                gross,
                round(net + calc.getTotalDeductions()),
                0.0,
                "Gross should exactly equal net pay plus total deductions."
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}