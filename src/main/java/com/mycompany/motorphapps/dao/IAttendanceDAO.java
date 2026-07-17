package com.mycompany.motorphapps.dao;

import com.mycompany.motorphapps.model.Attendance;
import java.util.List;

/**
 * Contract for attendance data access operations.
 */
public interface IAttendanceDAO {

    List<String[]> getAllAttendance();

    boolean logTimeIn(String employeeId, String date, String timeIn);

    boolean logTimeOut(String employeeId, String date, String timeOut);

    void saveOrUpdateAll(List<Attendance> logs);

    boolean deleteAttendance(String employeeId, String date);

    Attendance getAttendance(String employeeId, String date);
}
