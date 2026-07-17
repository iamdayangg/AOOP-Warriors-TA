package com.mycompany.motorphapps.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class AttendanceSummaryFactoryTest {

    @Test
    void shouldReturnFinanceAdminSummaryForAdminRole() {
        AttendanceSummary summary =
                AttendanceSummaryFactory.forRole("ADMIN");

        assertNotNull(summary);
        assertTrue(summary instanceof FinanceAdminSummary);
    }

    @Test
    void shouldReturnFinanceAdminSummaryForFinanceRole() {
        AttendanceSummary summary =
                AttendanceSummaryFactory.forRole("FINANCE");

        assertNotNull(summary);
        assertTrue(summary instanceof FinanceAdminSummary);
    }

    @Test
    void shouldReturnManagerSummaryForHrRole() {
        AttendanceSummary summary =
                AttendanceSummaryFactory.forRole("HR");

        assertNotNull(summary);
        assertTrue(summary instanceof ManagerSummary);
    }

    @Test
    void shouldReturnManagerSummaryForItRole() {
        AttendanceSummary summary =
                AttendanceSummaryFactory.forRole("IT");

        assertNotNull(summary);
        assertTrue(summary instanceof ManagerSummary);
    }

    @Test
    void shouldReturnEmployeeSummaryForUnknownRole() {
        AttendanceSummary summary =
                AttendanceSummaryFactory.forRole("EMPLOYEE");

        assertNotNull(summary);
        assertTrue(summary instanceof EmployeeSummary);
    }

    @Test
    void shouldReturnEmployeeSummaryForNullRole() {
        AttendanceSummary summary =
                AttendanceSummaryFactory.forRole(null);

        assertNotNull(summary);
        assertTrue(summary instanceof EmployeeSummary);
    }

    @Test
    void shouldIgnoreCaseAndSpaces() {
        AttendanceSummary summary =
                AttendanceSummaryFactory.forRole("   finance   ");

        assertNotNull(summary);
        assertTrue(summary instanceof FinanceAdminSummary);
    }
}