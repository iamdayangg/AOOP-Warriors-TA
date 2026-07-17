/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.dao;

import com.mycompany.motorphapps.config.DBConnection;
import com.mycompany.motorphapps.model.PayrollRecord;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for PayrollDAO — these hit the real motorph_db.
 * Every test that inserts a row cleans it up in @AfterEach, using a
 * clearly-marked test period string so repeated runs never collide
 * with real payroll history.
 *
 * @author DAYANG GWAPA
 */
class PayrollDAOTest {

    private final PayrollDAO dao = new PayrollDAO();
    private static int existingEmployeeId;
    private int createdPayrollId = -1;

    private static final String TEST_PERIOD = "UNIT_TEST_PERIOD_DO_NOT_USE";

    @BeforeAll
    static void findAnExistingEmployee() throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT employee_id FROM employees LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
            assertTrue(rs.next(), "employees table must have at least one row to run this test");
            existingEmployeeId = rs.getInt("employee_id");
        }
    }

    @AfterEach
    void cleanUp() {
        if (createdPayrollId != -1) {
            dao.deletePayrollRecord(createdPayrollId);
            createdPayrollId = -1;
        }
    }

    @Test
    void payrollRecordExists_returnsFalseForUnusedPeriod() {
        assertFalse(dao.payrollRecordExists(existingEmployeeId, TEST_PERIOD),
                "A period that was never inserted should not exist yet");
    }

    @Test
    void insertPayrollRecord_persistsAndRoundTripsThroughGetPayrollById() {
        PayrollRecord record = newTestRecord(5225.00, 235.13, 156.75, 104.50, 522.50, 1018.88, 4206.12);

        dao.insertPayrollRecord(record);
        createdPayrollId = record.getPayrollId();

        assertTrue(createdPayrollId > 0, "insertPayrollRecord should set a generated payroll_id");

        PayrollRecord fetched = dao.getPayrollById(createdPayrollId);
        assertNotNull(fetched, "Inserted record should be retrievable by id");

        assertEquals(5225.00, fetched.getGrossPay(), 0.001);
        assertEquals(4206.12, fetched.getNetPay(),   0.001);

        // Regression check for the bug fixed earlier: buildPayrollRecord() must pull
        // the SSS/PhilHealth/Pag-ibig/Tax breakdown from the `deduction` table —
        // before the fix these always came back as 0.0 regardless of what was inserted.
        assertEquals(235.13, fetched.getSssDeduction(),        0.001, "SSS breakdown should not be 0");
        assertEquals(156.75, fetched.getPhilhealthDeduction(), 0.001, "PhilHealth breakdown should not be 0");
        assertEquals(104.50, fetched.getPagibigDeduction(),    0.001, "Pag-ibig breakdown should not be 0");
        assertEquals(522.50, fetched.getWithholdingTax(),      0.001, "Tax breakdown should not be 0");
    }

    @Test
    void payrollRecordExists_returnsTrueAfterInsert() {
        PayrollRecord record = newTestRecord(1000.00, 45.00, 30.00, 20.00, 100.00, 195.00, 805.00);

        dao.insertPayrollRecord(record);
        createdPayrollId = record.getPayrollId();

        assertTrue(dao.payrollRecordExists(existingEmployeeId, TEST_PERIOD));
    }

    @Test
    void deletePayrollRecord_removesRowAndItsDeductions() {
        PayrollRecord record = newTestRecord(1000.00, 45.00, 30.00, 20.00, 100.00, 195.00, 805.00);

        dao.insertPayrollRecord(record);
        int payrollId = record.getPayrollId();

        dao.deletePayrollRecord(payrollId);
        createdPayrollId = -1; // already deleted, nothing left for cleanUp() to do

        assertNull(dao.getPayrollById(payrollId), "Deleted payroll record should no longer be retrievable");

        List<String[]> remainingDeductions = dao.getDeductionsByPayrollId(payrollId);
        assertTrue(remainingDeductions.isEmpty(), "Deduction rows for the deleted payroll should be gone too");
    }

    private PayrollRecord newTestRecord(double gross, double sss, double philHealth,
                                         double pagibig, double tax,
                                         double totalDeductions, double netPay) {
        PayrollRecord record = new PayrollRecord();
        record.setEmployeeId(existingEmployeeId);
        record.setPayrollPeriod(TEST_PERIOD);
        record.setRegularHoursWorked(80.0);
        record.setOvertimeHoursWorked(0.0);
        record.setGrossPay(gross);
        record.setSssDeduction(sss);
        record.setPhilhealthDeduction(philHealth);
        record.setPagibigDeduction(pagibig);
        record.setWithholdingTax(tax);
        record.setTotalDeductions(totalDeductions);
        record.setNetPay(netPay);
        record.setRemarks("Created by PayrollDAOTest");
        return record;
    }
}
