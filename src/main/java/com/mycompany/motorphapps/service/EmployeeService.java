package com.mycompany.motorphapps.service;

import com.mycompany.motorphapps.dao.EmployeeDAO;
import com.mycompany.motorphapps.dao.IEmployeeDAO;
import com.mycompany.motorphapps.model.Role;

import java.util.List;

/**
 * Business logic + validation layer for Employee module.
 *
 * This service depends on the IEmployeeDAO contract rather than a concrete
 * EmployeeDAO implementation, improving maintainability, extensibility,
 * and testability (Dependency Inversion Principle).
 */
public class EmployeeService {

    private final IEmployeeDAO employeeDAO;

    /**
     * Default constructor used by the application.
     * Keeps backward compatibility with existing code.
     */
    public EmployeeService() {
        this(new EmployeeDAO());
    }

    /**
     * Dependency injection constructor.
     * Allows alternate DAO implementations (mock, cache, database, etc.)
     * without changing this service.
     */
    public EmployeeService(IEmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    public List<String[]> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    public void addEmployee(String[] data) {
        requireLength(data, 20, "addEmployee");
        validate(data, false);
        employeeDAO.addEmployee(data);
    }

    public void updateEmployee(String[] data) {
        requireLength(data, 20, "updateEmployee");
        validate(data, true);
        employeeDAO.updateEmployee(data);
    }

    public void deleteEmployee(String employeeId) {

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be empty.");
        }

        employeeDAO.deleteEmployee(employeeId);
    }

    public boolean employeeExists(String employeeId) {
        return employeeDAO.employeeExists(employeeId);
    }

    public boolean canManageEmployees(String roleStr) {
        return Role.fromString(roleStr).canEditEmployees();
    }

    private void validate(String[] d, boolean isUpdate) {

        if (d == null) {
            throw new IllegalArgumentException("Employee data is null.");
        }

        if (!isUpdate) {
            InputValidator.requireEmployeeId(get(d, 0));
        }

        InputValidator.requireName(get(d, 1), "Last Name");
        InputValidator.requireName(get(d, 2), "First Name");
        InputValidator.requireBirthday(get(d, 3), "Birthday");
        InputValidator.requireNonBlank(get(d, 4), "Address");
        InputValidator.requirePhone(get(d, 5));
        InputValidator.requireSSS(get(d, 6));
        InputValidator.requirePhilHealth(get(d, 7));
        InputValidator.requireTIN(get(d, 8));
        InputValidator.requirePagIbig(get(d, 9));

        String status = InputValidator.requireNonBlank(get(d, 10), "Status");

        if (!status.equals("Regular")
                && !status.equals("Probationary")
                && !status.equals("Contractual")) {

            throw new IllegalArgumentException(
                    "Status must be Regular, Probationary, or Contractual.");
        }

        InputValidator.requireNonBlank(get(d, 11), "Position");

        InputValidator.requirePositiveDouble(get(d, 13), "Basic Salary");
        InputValidator.requireNonNegativeDouble(get(d, 14), "Rice Allowance");
        InputValidator.requireNonNegativeDouble(get(d, 15), "Phone Allowance");
        InputValidator.requireNonNegativeDouble(get(d, 16), "Clothing Allowance");
        InputValidator.requirePositiveDouble(get(d, 17), "Gross Semi-monthly Rate");
        InputValidator.requireHourlyRate(get(d, 18));

        String gender = get(d, 19);

        if (!gender.isEmpty()
                && !gender.equalsIgnoreCase("Male")
                && !gender.equalsIgnoreCase("Female")
                && !gender.equalsIgnoreCase("Other")) {

            throw new IllegalArgumentException(
                    "Gender must be Male, Female, or Other.");
        }
    }

    private String get(String[] arr, int i) {

        return (arr != null
                && i < arr.length
                && arr[i] != null)
                ? arr[i].trim()
                : "";
    }

    private void requireLength(String[] data, int expected, String method) {

        if (data == null || data.length != expected) {

            throw new IllegalArgumentException(
                    method
                    + " requires exactly "
                    + expected
                    + " columns. Received: "
                    + (data == null ? "null" : data.length));
        }
    }
}