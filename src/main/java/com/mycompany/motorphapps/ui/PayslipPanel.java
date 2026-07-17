package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.model.Payslip;
import com.mycompany.motorphapps.service.EmployeeService;
import com.mycompany.motorphapps.service.PayrollService;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterException;
import java.util.List;

public class PayslipPanel extends JPanel {

    private JComboBox<String> employeeCombo;
    private JComboBox<String> periodCombo;
    private JEditorPane       area;
    private PayrollService    payrollService;
    private EmployeeService   employeeService;

    public PayslipPanel(String employeeID, String role) {
        payrollService  = new PayrollService();
        employeeService = new EmployeeService();

        setLayout(new BorderLayout(15, 15));
        setBackground(UIStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createTop(employeeID, role), BorderLayout.NORTH);
        add(createViewer(),              BorderLayout.CENTER);
        add(createBottomBar(),           BorderLayout.SOUTH);
    }

    // ── Top controls ──────────────────────────────────────────────────────────
    private JPanel createTop(String employeeID, String role) {
        JPanel panel = UIStyle.createElevatedCard();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        employeeCombo = new JComboBox<>();
        periodCombo   = new JComboBox<>();

        loadEmployees(employeeID, role);
        loadPeriods();

        // Reload periods whenever the selected employee changes
        employeeCombo.addActionListener(e -> loadPeriods());

        JButton view = UIStyle.createPrimaryButton("View");
        view.addActionListener(e -> display());

        panel.add(new JLabel("Employee"));
        panel.add(employeeCombo);
        panel.add(new JLabel("Period"));
        panel.add(periodCombo);
        panel.add(view);
        return panel;
    }

    // ── Viewer ────────────────────────────────────────────────────────────────
    private JScrollPane createViewer() {
        area = new JEditorPane();
        area.setContentType("text/html");
        area.setEditable(false);
        area.setText("<html><body style='font-family:Segoe UI;color:#888'>"
                + "<br><br><center>Select an employee and period, "
                + "then click <b>View</b>.</center></body></html>");
        return new JScrollPane(area);
    }

    // ── Bottom bar ────────────────────────────────────────────────────────────
    private JPanel createBottomBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        panel.setOpaque(false);

        JButton printBtn = UIStyle.createPrimaryButton("🖨  Print");
        printBtn.addActionListener(e -> {
            try {
                area.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this,
                        "Print failed: " + ex.getMessage(),
                        "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(printBtn);
        return panel;
    }

    // ── Display payslip ───────────────────────────────────────────────────────
    private void display() {
        Object empItem = employeeCombo.getSelectedItem();
        Object perItem = periodCombo.getSelectedItem();

        if (empItem == null || perItem == null
                || "No records found".equals(perItem.toString())) {
            showNoPayslip("No payslip available for this selection.");
            return;
        }

        String empId  = empItem.toString().split(" - ")[0].trim();
        String period = perItem.toString();

        Payslip p = payrollService.buildPayslipDetail(empId, period);

        if (p == null) {
            showNoPayslip("No payslip found for the selected period.");
            return;
        }

        area.setText(buildPayslipHtml(p));
    }

    private void showNoPayslip(String message) {
        area.setText("<html><body style='font-family:Segoe UI'>"
                + "<br><br><center><b>" + message + "</b></center>"
                + "</body></html>");
    }

    // ── HTML renderer ─────────────────────────────────────────────────────────
    private String buildPayslipHtml(Payslip p) {
        return "<html><body style='font-family:Segoe UI; margin:20px'>"
             + "<div style='max-width:600px;margin:auto;border:1px solid #ccc;"
             +      "border-radius:8px;padding:24px'>"

             + "<h2 style='color:#1a3a6b;margin-bottom:4px'>MotorPH</h2>"
             + "<p style='color:#888;margin-top:0'>Official Payslip</p>"
             + "<hr style='border:1px solid #e0e0e0'/>"

             + "<table width='100%' cellpadding='4'>"
             + "<tr>"
             + "  <td><b>Employee ID</b></td><td>" + p.getEmployeeId()   + "</td>"
             + "  <td><b>Name</b></td><td>"        + p.getEmployeeName() + "</td>"
             + "</tr>"
             + "<tr>"
             + "  <td><b>Period</b></td>"
             + "  <td colspan='3'>" + p.getPeriod() + "</td>"
             + "</tr>"
             + "</table>"

             + "<hr style='border:1px solid #e0e0e0'/>"

             + "<table width='100%' cellpadding='4'>"
             + "<tr style='background:#f5f8ff'>"
             + "  <td><b>Earnings</b></td><td></td>"
             + "  <td><b>Deductions</b></td><td></td>"
             + "</tr>"
             + "<tr>"
             + "  <td>Gross Pay</td>"
             + "  <td>&#x20B1;" + fmt(p.getGross()) + "</td>"
             + "  <td>Tax (Withholding)</td>"
             + "  <td>&#x20B1;" + fmt(p.getTax()) + "</td>"
             + "</tr>"
             + "<tr>"
             + "  <td></td><td></td>"
             + "  <td>SSS</td>"
             + "  <td>&#x20B1;" + fmt(p.getSss()) + "</td>"
             + "</tr>"
             + "<tr>"
             + "  <td></td><td></td>"
             + "  <td>PhilHealth</td>"
             + "  <td>&#x20B1;" + fmt(p.getPhilHealth()) + "</td>"
             + "</tr>"
             + "<tr>"
             + "  <td></td><td></td>"
             + "  <td>Pag-IBIG</td>"
             + "  <td>&#x20B1;" + fmt(p.getPagibig()) + "</td>"
             + "</tr>"
             + "</table>"

             + "<hr style='border:1px solid #e0e0e0'/>"

             + "<table width='100%' cellpadding='4'>"
             + "<tr style='background:#e8f5e9'>"
             + "  <td><b style='font-size:14px'>NET PAY</b></td>"
             + "  <td><b style='font-size:14px;color:#1a7a40'>"
             +      "&#x20B1;" + fmt(p.getNet()) + "</b></td>"
             + "</tr>"
             + "</table>"

             + "</div></body></html>";
    }

    private String fmt(double value) {
        return String.format("%,.2f", value);
    }

    // ── Data loaders ──────────────────────────────────────────────────────────

    // getAllEmployees() row: [0]=employee_id [1]=last_name [2]=first_name
    private void loadEmployees(String employeeID, String role) {
        employeeCombo.removeAllItems();
        List<String[]> list = employeeService.getAllEmployees();

        for (String[] e : list) {
            if (!payrollService.canViewAllPayroll(role)
                    && !e[0].equals(employeeID)) continue;
            // "10001 - Manuel III Garcia"
            employeeCombo.addItem(e[0] + " - " + e[2] + " " + e[1]);
        }
    }

    // getAllPayroll() row: [0]=employee_id [1]=full_name [2]=payroll_period
    private void loadPeriods() {
        periodCombo.removeAllItems();

        Object selected = employeeCombo.getSelectedItem();
        if (selected == null) {
            periodCombo.addItem("No records found");
            return;
        }

        String empId = selected.toString().split(" - ")[0].trim();
        List<String[]> records = payrollService.getAllPayroll();

        boolean found = false;
        for (String[] row : records) {
            // row[0] = employee_id, row[2] = payroll_period
            if (row[0].trim().equals(empId)) {
                periodCombo.addItem(row[2]);
                found = true;
            }
        }

        if (!found) {
            periodCombo.addItem("No records found");
        }
    }
}