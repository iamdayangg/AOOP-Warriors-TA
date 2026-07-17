package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.model.Payslip;
import com.mycompany.motorphapps.payroll.OvertimePayrollCalculator;
import com.mycompany.motorphapps.payroll.PayrollCalculator;
import com.mycompany.motorphapps.service.PayrollService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.time.YearMonth;
import java.util.List;

public class PayrollPanel extends JPanel {

    private JTable            table;
    private DefaultTableModel model;
    private PayrollService    payrollService;

    public PayrollPanel(String employeeID, String role) {
        payrollService = new PayrollService();

        setLayout(new BorderLayout(15, 15));
        setBackground(UIStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createHeader(),                    BorderLayout.NORTH);
        add(createTable(),                     BorderLayout.CENTER);
        add(createRightPanel(employeeID, role), BorderLayout.EAST);

        if (payrollService.canProcessPayroll(role)) {
            add(createBottomPanel(), BorderLayout.SOUTH);
        }

        loadPayrollData(employeeID, role);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel title = new JLabel("Payroll Management");
        title.setFont(UIStyle.FONT_TITLE);
        panel.add(title, BorderLayout.WEST);
        return panel;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    // Model has 10 columns; column 9 (payroll_id) is hidden after creation.
    // Visible: ID | Name | Period | Gross | Tax | SSS | PhilHealth | Pagibig | Net
    private JScrollPane createTable() {
        model = new DefaultTableModel(
                new String[]{
                    "ID", "Name", "Period", "Gross",
                    "Tax", "SSS", "PhilHealth", "Pagibig", "Net",
                    "payroll_id"   // hidden column at index 9
                }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        UIStyle.styleTable(table);

        // Hide payroll_id column — keep it in model for delete lookup
        TableColumnModel tcm = table.getColumnModel();
        tcm.getColumn(9).setMinWidth(0);
        tcm.getColumn(9).setMaxWidth(0);
        tcm.getColumn(9).setWidth(0);

        return new JScrollPane(table);
    }

    // ── Right panel (payslip viewer shortcut) ─────────────────────────────────
    private JPanel createRightPanel(String employeeID, String role) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JPanel card = UIStyle.createElevatedCard();

        JLabel title = new JLabel("Payslip");
        title.setFont(UIStyle.FONT_HEADING);

        JLabel desc = new JLabel("<html>View and print employee payslips.</html>");
        desc.setFont(UIStyle.FONT_BODY);

        JButton btn = UIStyle.createPrimaryButton("Open Viewer");
        btn.addActionListener(e -> {
            JFrame frame = new JFrame("Payslip Viewer");
            frame.setSize(700, 600);
            frame.setLocationRelativeTo(null);
            frame.add(new PayslipPanel(employeeID, role));
            frame.setVisible(true);
        });

        card.add(title);
        card.add(desc);
        card.add(Box.createVerticalStrut(10));
        card.add(btn);

        panel.add(card, BorderLayout.NORTH);
        return panel;
    }

    // ── Bottom panel (finance/admin only) ─────────────────────────────────────
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.add(createCalculationPanel(), BorderLayout.CENTER);
        panel.add(createDeleteButton(),     BorderLayout.SOUTH);
        return panel;
    }

    // ── Payroll calculation form ───────────────────────────────────────────────
    private JPanel createCalculationPanel() {
        JPanel card = UIStyle.createElevatedCard();
        card.setLayout(new GridLayout(5, 2, 10, 10));

        JTextField id   = UIStyle.createTextField(10);
        JTextField hrs  = UIStyle.createTextField(10);
        JTextField rate = UIStyle.createTextField(10);

        JComboBox<String> period = new JComboBox<>();
        loadPeriods(period);

        JButton calc = UIStyle.createPrimaryButton("Calculate");
        calc.addActionListener(e -> {
            try {
                PayrollCalculator calcObj = new OvertimePayrollCalculator();

                Payslip p = payrollService.processPayroll(
                        id.getText(),
                        (String) period.getSelectedItem(),
                        hrs.getText(),
                        rate.getText(),
                        calcObj
                );

                JOptionPane.showMessageDialog(this,
                        "Net Salary: ₱" + String.format("%,.2f", p.getNet()));

                // Refresh table after new record is saved
                loadPayrollData(null, "System Administrator");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Calculation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(new JLabel("Employee ID")); card.add(id);
        card.add(new JLabel("Hours"));       card.add(hrs);
        card.add(new JLabel("Rate"));        card.add(rate);
        card.add(new JLabel("Period"));      card.add(period);
        card.add(new JLabel());              card.add(calc);

        return card;
    }

    // ── Delete button ─────────────────────────────────────────────────────────
    private JPanel createDeleteButton() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        JButton delete = UIStyle.createDangerButton("Delete Selected");
        delete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a row first.");
                return;
            }

            // Convert view row → model row (handles sorting)
            int modelRow = table.convertRowIndexToModel(row);

            String empId  = (String) model.getValueAt(modelRow, 0); // employee_id
            String period = (String) model.getValueAt(modelRow, 2); // payroll_period

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete payroll for employee " + empId
                            + "\nPeriod: " + period + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                payrollService.deletePayroll(empId, period);
                loadPayrollData(null, "System Administrator");
            }
        });

        panel.add(delete);
        return panel;
    }

    // ── Period combo loader ───────────────────────────────────────────────────
    private void loadPeriods(JComboBox<String> combo) {
        combo.addItem("Select Period");
        int year = java.time.LocalDate.now().getYear();
        for (int m = 1; m <= 12; m++) {
            YearMonth ym   = YearMonth.of(year, m);
            int       last = ym.lengthOfMonth();
            String    mon  = String.format("%02d", m);
            combo.addItem(year + "-" + mon + "-01 to " + year + "-" + mon + "-15");
            combo.addItem(year + "-" + mon + "-16 to " + year + "-" + mon + "-" + last);
        }
    }

    // ── Load payroll into table ───────────────────────────────────────────────
    // getAllPayroll() row layout:
    //  [0] employee_id  [1] full_name  [2] payroll_period
    //  [3] gross        [4] tax        [5] sss
    //  [6] philhealth   [7] pagibig   [8] net_pay
    //  [9] payroll_id   (hidden)
    private void loadPayrollData(String employeeID, String role) {
        model.setRowCount(0);

        List<String[]> records = payrollService.getAllPayroll();

        for (String[] row : records) {
            // row[0] = employee_id
            boolean showAll  = payrollService.canViewAllPayroll(role);
            boolean ownRecord = employeeID != null && row[0].equals(employeeID);

            if (showAll || ownRecord) {
                model.addRow(row);
            }
        }
    }
}