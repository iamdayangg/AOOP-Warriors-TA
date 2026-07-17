package com.mycompany.motorphapps.payroll;

import com.mycompany.motorphapps.model.PayrollRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PayrollProcessorTest {

    @Test
    void buildRecord_chainsCalculatorIntoCorrectGrossAndDeductions() {
        PayrollProcessor processor = new PayrollProcessor(new StandardPayrollCalculator());

        PayrollRecord record = processor.buildRecord(
                18, "Diane Ababao", "2026-06-01 to 2026-06-15",
                5225.00, 0.00,   
                0.0, 0.0);       

        assertEquals(5225.00, record.getGrossPay(),            0.001);
        assertEquals(0.00,  record.getWithholdingTax(),      0.001);
        assertEquals(275.00, record.getSssDeduction(), 0.001);
        assertEquals(250.00,
             record.getPhilhealthDeduction(),
             0.001);
        assertEquals(104.50, record.getPagibigDeduction(), 0.001);
    }

    @Test
    void buildRecord_netPayAlwaysReconcilesWithTotalDeductions() {
        PayrollProcessor processor = new PayrollProcessor(new StandardPayrollCalculator());

        PayrollRecord record = processor.buildRecord(
                18, "Diane Ababao", "2026-06-01 to 2026-06-15",
                5225.00, 0.00, 0.0, 0.0);

        assertEquals(record.getGrossPay() - record.getTotalDeductions(),
                     record.getNetPay(), 0.001,
                     "Net pay must equal gross minus total deductions exactly");
    }

    @Test
    void buildRecord_carriesEmployeeAndPeriodFieldsThrough() {
        PayrollProcessor processor = new PayrollProcessor(); 

        PayrollRecord record = processor.buildRecord(
                7, "Rosie Atienza", "2026-07-01 to 2026-07-15",
                4750.00, 0.00, 0.0, 0.0);

        assertEquals(7, record.getEmployeeId());
        assertEquals("Rosie Atienza", record.getEmployeeName());
        assertEquals("2026-07-01 to 2026-07-15", record.getPayrollPeriod());
    }
}

