package com.mycompany.motorphapps.service;

import com.mycompany.motorphapps.dao.LeaveDAO;
import com.mycompany.motorphapps.model.LeaveRequest;
import com.mycompany.motorphapps.model.Role;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * All leave business logic and validation lives here.
 */
public class LeaveService {

    // =========================================================
    // VALID LEAVE TYPES
    // =========================================================
    private static final String[] VALID_TYPES = {
        "Vacation Leave",
        "Sick Leave",
        "Emergency Leave",
        "Maternity Leave",
        "Paternity Leave",
        "Bereavement Leave"
    };

    private final LeaveDAO leaveDAO;

    public LeaveService() {
        this.leaveDAO = new LeaveDAO();
    }

    // =========================================================
    // GET ALL LEAVES
    // =========================================================
    public List<LeaveRequest> getAllLeaves() {

        List<String[]> rows = leaveDAO.getAllLeaves();
        List<LeaveRequest> result = new ArrayList<>();

        for (String[] row : rows) {

            if (row.length >= 6) {

                result.add(
                    new LeaveRequest(
                        row[0], // leave_request_id
                        row[1], // employee_id
                        row[2], // leave_name
                        row[3], // start_date
                        row[4], // end_date
                        row[5]  // status
                    )
                );
            }
        }

        return result;
    }

    // =========================================================
    // REQUEST LEAVE
    // =========================================================
    public void requestLeave(
            String employeeId,
            String type,
            String startDate,
            String endDate
    ) {

        // -----------------------------------------------------
        // VALIDATION
        // -----------------------------------------------------

        String cleanType =
            InputValidator.requireNonBlank(type, "Leave Type");

        LocalDate start =
            InputValidator.requireDate(startDate, "Start Date");

        LocalDate end =
            InputValidator.requireDate(endDate, "End Date");

        InputValidator.requireFutureOrToday(start, "Start Date");

        InputValidator.requireDateRange(start, end);

        // -----------------------------------------------------
        // VALIDATE LEAVE TYPE
        // -----------------------------------------------------

        boolean typeValid = false;

        for (String t : VALID_TYPES) {
            if (t.equalsIgnoreCase(cleanType)) {
                typeValid = true;
                break;
            }
        }

        if (!typeValid) {
            throw new IllegalArgumentException(
                "Invalid leave type."
            );
        }

        // -----------------------------------------------------
        // MAX 30 DAYS
        // -----------------------------------------------------

        long days =
            java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;

        if (days > 30) {
            throw new IllegalArgumentException(
                "Leave duration cannot exceed 30 days."
            );
        }

        // -----------------------------------------------------
        // CONVERT LEAVE NAME -> leave_type_id
        // -----------------------------------------------------

        int leaveTypeId = getLeaveTypeId(cleanType);

        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        leaveDAO.addLeave(
            employeeId,
            leaveTypeId,
            startDate.trim(),
            endDate.trim()
        );
    }

    // =========================================================
    // UPDATE STATUS
    // =========================================================
    public void updateStatus(String leaveId, String status) {
        leaveDAO.updateStatus(leaveId, status);
    }

    // =========================================================
    // DELETE LEAVE
    // =========================================================
    public void deleteLeave(String leaveId) {
        leaveDAO.deleteLeave(leaveId);
    }

    // =========================================================
    // ROLE CHECKS
    // =========================================================
    public boolean canManageLeave(String roleStr) {
        return Role.fromString(roleStr).canManageLeave();
    }

    public boolean canViewAllLeaves(String roleStr) {
        return Role.fromString(roleStr).canManageLeave();
    }

    // =========================================================
    // GET LEAVE TYPES
    // =========================================================
    public String[] getLeaveTypes() {
        return VALID_TYPES.clone();
    }

    // =========================================================
    // CONVERT LEAVE NAME -> DATABASE ID
    // =========================================================
    private int getLeaveTypeId(String leaveType) {

        switch (leaveType.toLowerCase()) {

            case "vacation leave":
                return 1;

            case "sick leave":
                return 2;

            case "emergency leave":
                return 3;

            case "maternity leave":
                return 4;

            case "paternity leave":
                return 5;

            case "bereavement leave":
                return 6;

            default:
                throw new IllegalArgumentException(
                    "Unknown leave type: " + leaveType
                );
        }
    }
}