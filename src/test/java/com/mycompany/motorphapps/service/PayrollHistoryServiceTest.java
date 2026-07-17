/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.service;

import com.mycompany.motorphapps.config.DBConnection;
import com.mycompany.motorphapps.dao.PayrollDAO;
import com.mycompany.motorphapps.model.PayrollRecord;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for PayrollHistoryService's business rules — these hit
 * the real motorph_db through PayrollDAO and EmployeeDAO.
 *
 * @author DAYANG GWAPA
 */
class PayrollHistoryServiceTest {

    private final PayrollHistoryService service    = new PayrollHistoryService();
    private final PayrollDAO            payrollDAO = new PayrollDAO();

    private static int existingEmployeeId;
    private static final int    NONEXISTENT_EMPLOYEE_ID = 999999;
    private static final String TEST_PERIOD             = "UNIT_TEST_PERIOD_DO_NOT_USE_2";

    private int createdPayrollId = -1;

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
            payrollDAO.deletePayrollRecord(createdPayrollId);
            createdPayrollId = -1;
        }
    }

    @Test
    void processNewPayroll_throwsForNonexistentEmployee() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.processNewPayroll(NONEXISTENT_EMPLOYEE_ID, TEST_PERIOD, 0, 0, ""));

        assertTrue(ex.getMessage().contains(String.valueOf(NONEXISTENT_EMPLOYEE_ID)),
                "Error message should mention the employee id that wasn't found");
    }

    @Test
    void processNewPayroll_throwsOnDuplicatePeriodForSameEmployee() {
        PayrollRecord first = service.processNewPayroll(
                existingEmployeeId, TEST_PERIOD, 80, 0, "first run");
        createdPayrollId = first.getPayrollId();

        assertThrows(IllegalStateException.class, () ->
                service.processNewPayroll(existingEmployeeId, TEST_PERIOD, 80, 0, "duplicate"));
    }

    @Test
    void processNewPayroll_computesNetPayThatReconcilesWithGrossAndDeductions() {
        PayrollRecord record = service.processNewPayroll(
                existingEmployeeId, TEST_PERIOD, 80, 0, "reconciliation check");
        createdPayrollId = record.getPayrollId();

        assertEquals(record.getGrossPay() - record.getTotalDeductions(),
                     record.getNetPay(), 0.001);
    }
}