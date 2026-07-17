/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.model.PayrollRecord;
import com.mycompany.motorphapps.service.PayrollHistoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Browsable, searchable view of past payroll runs, backed by the existing
 * `payroll` + `deduction` tables.
 *
 * @author DAYANG GWAPA
 */
public class PayrollHistoryPanel extends JPanel {

    private final PayrollHistoryService service = new PayrollHistoryService();

    private final String[] COLUMNS = {
        "Payroll ID", "Employee", "Period", "Reg. Hours", "OT Hours",
        "Gross Pay", "SSS", "PhilHealth", "Pag-ibig", "Tax",
        "Total Deductions", "Net Pay", "Date Processed", "Remarks"
    };

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField employeeIdFilter;
    private List<PayrollRecord> currentRecords;

    public PayrollHistoryPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UIStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        loadAllHistory();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Payroll History");
        title.setFont(UIStyle.FONT_TITLE);
        title.setForeground(UIStyle.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBar.setOpaque(false);

        employeeIdFilter = UIStyle.createTextField(8);

        JButton searchBtn  = UIStyle.createSecondaryButton("Search");
        JButton showAllBtn = UIStyle.createSecondaryButton("Show All");
        JButton processBtn = UIStyle.createPrimaryButton("Process New Payroll");

        searchBtn.addActionListener(e -> searchByEmployee());
        showAllBtn.addActionListener(e -> loadAllHistory());
        processBtn.addActionListener(e -> openProcessPayrollDialog());

        JLabel filterLbl = new JLabel("Employee ID:");
        filterLbl.setFont(UIStyle.FONT_BODY);
        filterLbl.setForeground(UIStyle.TEXT_SECONDARY);

        searchBar.add(filterLbl);
        searchBar.add(employeeIdFilter);
        searchBar.add(searchBtn);
        searchBar.add(showAllBtn);
        searchBar.add(processBtn);

        header.add(searchBar, BorderLayout.EAST);
        return header;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        UIStyle.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER, 1));
        return scroll;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footer.setOpaque(false);

        JButton viewBtn   = UIStyle.createSecondaryButton("View Details");
        JButton deleteBtn = UIStyle.createDangerButton("Delete Selected");

        viewBtn.addActionListener(e -> viewSelectedDetails());
        deleteBtn.addActionListener(e -> deleteSelected());

        footer.add(viewBtn);
        footer.add(deleteBtn);
        return footer;
    }

    private void loadAllHistory() {
        try {
            currentRecords = service.getAllHistory();
            fillTable(currentRecords);
        } catch (Exception ex) {
            showError("Failed to load payroll history.", ex);
        }
    }

    private void searchByEmployee() {
        String text = employeeIdFilter.getText().trim();
        if (text.isEmpty()) { loadAllHistory(); return; }
        try {
            int employeeId = Integer.parseInt(text);
            currentRecords = service.getHistoryForEmployee(employeeId);
            fillTable(currentRecords);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Employee ID must be a number.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            showError("Failed to search payroll history.", ex);
        }
    }

    private void fillTable(List<PayrollRecord> records) {
        tableModel.setRowCount(0);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (PayrollRecord r : records) {
            tableModel.addRow(new Object[]{
                r.getPayrollId(),
                r.getEmployeeName(),
                r.getPayrollPeriod(),
                r.getRegularHoursWorked(),
                r.getOvertimeHoursWorked(),
                peso(r.getGrossPay()),
                peso(r.getSssDeduction()),
                peso(r.getPhilhealthDeduction()),
                peso(r.getPagibigDeduction()),
                peso(r.getWithholdingTax()),
                peso(r.getTotalDeductions()),
                peso(r.getNetPay()),
                r.getDateProcessed() != null ? fmt.format(r.getDateProcessed()) : "",
                r.getRemarks() != null ? r.getRemarks() : ""
            });
        }
    }

    private void viewSelectedDetails() {
        PayrollRecord r = getSelectedRecord();
        if (r == null) {
            JOptionPane.showMessageDialog(this, "Select a row first.",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String details = String.format(
            "Employee: %s (ID %d)%n" +
            "Period: %s%n%n" +
            "Regular Hours: %.2f      Overtime Hours: %.2f%n%n" +
            "Gross Pay: %s%n" +
            "SSS: %s%n" +
            "PhilHealth: %s%n" +
            "Pag-ibig: %s%n" +
            "Withholding Tax: %s%n" +
            "Total Deductions: %s%n" +
            "Net Pay: %s%n%n" +
            "Processed: %s%n" +
            "Remarks: %s",
            r.getEmployeeName(), r.getEmployeeId(), r.getPayrollPeriod(),
            r.getRegularHoursWorked(), r.getOvertimeHoursWorked(),
            peso(r.getGrossPay()), peso(r.getSssDeduction()), peso(r.getPhilhealthDeduction()),
            peso(r.getPagibigDeduction()), peso(r.getWithholdingTax()),
            peso(r.getTotalDeductions()), peso(r.getNetPay()),
            r.getDateProcessed(), r.getRemarks() == null ? "\u2014" : r.getRemarks()
        );
        JOptionPane.showMessageDialog(this, details, "Payroll Details", JOptionPane.PLAIN_MESSAGE);
    }

    private void deleteSelected() {
        PayrollRecord r = getSelectedRecord();
        if (r == null) {
            JOptionPane.showMessageDialog(this, "Select a row first.",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete payroll record #" + r.getPayrollId() + " for " + r.getEmployeeName() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            service.deleteRecord(r.getPayrollId());
            loadAllHistory();
        } catch (Exception ex) {
            showError("Failed to delete payroll record.", ex);
        }
    }

    private PayrollRecord getSelectedRecord() {
        int row = table.getSelectedRow();
        if (row < 0 || currentRecords == null || row >= currentRecords.size()) return null;
        return currentRecords.get(row);
    }

    private void openProcessPayrollDialog() {
        JDialog dialog = new JDialog((Frame) null, "Process New Payroll", true);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(UIStyle.SURFACE);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 12, 8, 12);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField employeeIdField  = UIStyle.createTextField(15);
        JTextField periodField      = UIStyle.createTextField(15);
        JTextField regularHrsField  = UIStyle.createTextField(15);
        regularHrsField.setText("0");
        JTextField overtimeHrsField = UIStyle.createTextField(15);
        overtimeHrsField.setText("0");
        JTextField remarksField     = UIStyle.createTextField(15);

        addFormRow(dialog, gc, 0, "Employee ID:", employeeIdField);
        addFormRow(dialog, gc, 1, "Payroll Period:", periodField);
        addFormRow(dialog, gc, 2, "Regular Hours:", regularHrsField);
        addFormRow(dialog, gc, 3, "Overtime Hours:", overtimeHrsField);
        addFormRow(dialog, gc, 4, "Remarks (optional):", remarksField);

        JButton submitBtn = UIStyle.createPrimaryButton("Compute & Save");
        submitBtn.addActionListener(e -> {
            try {
                int employeeId       = Integer.parseInt(employeeIdField.getText().trim());
                String period          = periodField.getText().trim();
                double regularHours  = parseOrZero(regularHrsField.getText());
                double overtimeHours = parseOrZero(overtimeHrsField.getText());
                String remarks         = remarksField.getText().trim();

                if (period.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Payroll period is required.",
                            "Missing Field", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                PayrollRecord saved = service.processNewPayroll(
                        employeeId, period, regularHours, overtimeHours, remarks);

                JOptionPane.showMessageDialog(dialog,
                        "Payroll #" + saved.getPayrollId() + " saved.\nNet Pay: " + peso(saved.getNetPay()),
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                loadAllHistory();

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dialog, "Employee ID and hours must be numeric.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
            } catch (IllegalStateException | IllegalArgumentException biz) {
                JOptionPane.showMessageDialog(dialog, biz.getMessage(),
                        "Cannot Process", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                showError("Failed to process payroll.", ex);
            }
        });

        gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 2; gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.CENTER;
        dialog.add(submitBtn, gc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addFormRow(JDialog dialog, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1; gc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIStyle.FONT_BODY);
        lbl.setForeground(UIStyle.TEXT_PRIMARY);
        dialog.add(lbl, gc);

        gc.gridx = 1; gc.weightx = 1;
        dialog.add(field, gc);
    }

    private double parseOrZero(String text) {
        try { return text == null || text.isBlank() ? 0.0 : Double.parseDouble(text.trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private String peso(double value) {
        return String.format("\u20B1%,.2f", value);
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this, message + "\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}