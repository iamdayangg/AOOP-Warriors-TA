/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.report.ReportException;
import com.mycompany.motorphapps.report.ReportService;
import com.mycompany.motorphapps.service.PayrollService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Desktop;
import java.io.File;
import java.time.YearMonth;
import java.util.List;

public class ReportPanel extends JPanel {

    private final String         employeeID;
    private final String         role;
    private final ReportService  reportService  = new ReportService();
    private final PayrollService payrollService = new PayrollService();

    private JComboBox<String> payslipEmpCombo;
    private JComboBox<String> payslipPeriodCombo;

    private JComboBox<String> deptCombo;
    private JComboBox<String> deptPeriodCombo;
    
    private JComboBox<String> timecardCombo;
    private JComboBox<String> timecardPeriodCombo;

    private JLabel statusLabel;

    public ReportPanel(String employeeID, String role) {
        this.employeeID = employeeID;
        this.role       = role;

        setLayout(new BorderLayout(15, 15));
        setBackground(UIStyle.BACKGROUND);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        add(createHeader(),   BorderLayout.NORTH);
        add(createTabs(),     BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Reports");
        title.setFont(UIStyle.FONT_TITLE);
        title.setForeground(UIStyle.TEXT_PRIMARY);

        JLabel sub = new JLabel(payrollService.canViewAllPayroll(role)
                ? "HR / Admin — generate payslips and department payroll reports"
                : "Generate and print your personal payslip");
        sub.setFont(UIStyle.FONT_SMALL);
        sub.setForeground(UIStyle.TEXT_SECONDARY);
        sub.setBorder(new EmptyBorder(3, 2, 0, 0));

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(title, BorderLayout.CENTER);
        left.add(sub,   BorderLayout.SOUTH);

        panel.add(left, BorderLayout.WEST);
        return panel;
    }

    private JTabbedPane createTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIStyle.FONT_BODY);

        tabs.addTab("  Payslip", createPayslipTab());
        
        tabs.addTab("  Timecard", createTimecardTab());

        if (payrollService.canViewAllPayroll(role)) {
            tabs.addTab("  Department Report", createDeptReportTab());
        }

        return tabs;
    }

    private JPanel createPayslipTab() {
        JPanel outer = new JPanel(new BorderLayout(12, 12));
        outer.setBackground(UIStyle.BACKGROUND);
        outer.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel card = UIStyle.createElevatedCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8, 8, 8, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.anchor  = GridBagConstraints.WEST;

        boolean isHR = payrollService.canViewAllPayroll(role);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        card.add(new JLabel("Employee:"), gbc);

        payslipEmpCombo = new JComboBox<>();
        loadPayslipEmployees();
        gbc.gridx = 1; gbc.weightx = 1;
        card.add(payslipEmpCombo, gbc);

        if (!isHR) payslipEmpCombo.setEnabled(false);

        payslipEmpCombo.addActionListener(e -> loadPayslipPeriods());

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        card.add(new JLabel("Pay Period:"), gbc);

        payslipPeriodCombo = new JComboBox<>();
        loadPayslipPeriods();
        gbc.gridx = 1; gbc.weightx = 1;
        card.add(payslipPeriodCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JLabel info = new JLabel(
                "<html><i style='color:#888'>The report will be saved as a PDF "
                + "and opened automatically.</i></html>");
        info.setFont(UIStyle.FONT_SMALL);
        card.add(info, gbc);

        gbc.gridy = 3; gbc.gridwidth = 2;
        JButton generate = UIStyle.createPrimaryButton("Generate Payslip PDF");
        generate.addActionListener(e -> onGeneratePayslip());
        card.add(generate, gbc);

        outer.add(card, BorderLayout.NORTH);
        outer.add(createPayslipInstructions(), BorderLayout.CENTER);
        return outer;
    }

    private JPanel createPayslipInstructions() {
        JPanel card = UIStyle.createElevatedCard();
        JLabel heading = new JLabel("How it works");
        heading.setFont(UIStyle.FONT_HEADING);
        heading.setForeground(UIStyle.PRIMARY);

        JLabel body = new JLabel("<html>"
                + "<ol style='margin-left:16px'>"
                + "<li>Select an employee (HR/admin can pick any; employees see only themselves).</li>"
                + "<li>Choose the pay period from the dropdown.</li>"
                + "<li>Click <b>Generate Payslip PDF</b>.</li>"
                + "<li>The PDF opens automatically in your default viewer where you can print it.</li>"
                + "</ol>"
                + "</html>");
        body.setFont(UIStyle.FONT_BODY);

        card.add(heading);
        card.add(Box.createVerticalStrut(8));
        card.add(body);
        return card;
    }

    private void onGeneratePayslip() {
        Object empSel = payslipEmpCombo.getSelectedItem();
        Object perSel = payslipPeriodCombo.getSelectedItem();

        if (empSel == null || perSel == null || perSel.toString().isBlank()) {
            setStatus("  Please select an employee and a pay period.", UIStyle.WARNING);
            return;
        }

        String targetId = empSel.toString().split(" - ")[0].trim();
        String period   = perSel.toString();

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("Payslip_" + targetId + "_" + period.replace(" ", "_") + ".pdf"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File outFile = fc.getSelectedFile();
        if (!outFile.getName().endsWith(".pdf"))
            outFile = new File(outFile.getAbsolutePath() + ".pdf");

        setStatus(" Generating payslip…", UIStyle.TEXT_SECONDARY);
        final File finalOut = outFile;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            String errMsg = null;

            @Override
            protected Void doInBackground() {
                try {
                    reportService.generatePayslip(
                            employeeID, role, targetId, period, finalOut);
                } catch (ReportException ex) {
                    errMsg = ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (errMsg != null) {
                    setStatus("✗  " + errMsg, UIStyle.DANGER);
                    JOptionPane.showMessageDialog(ReportPanel.this,
                            errMsg, "Report Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    setStatus("  Payslip saved: " + finalOut.getName(), UIStyle.SUCCESS);
                    openFile(finalOut);
                }
            }
        };
        worker.execute();
    }

    private JPanel createDeptReportTab() {
        JPanel outer = new JPanel(new BorderLayout(12, 12));
        outer.setBackground(UIStyle.BACKGROUND);
        outer.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel card = UIStyle.createElevatedCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8, 8, 8, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.anchor  = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        card.add(new JLabel("Department:"), gbc);

        deptCombo = new JComboBox<>();
        deptCombo.addItem("All Departments");
        List<String> depts = reportService.getAllDepartments();
        depts.forEach(deptCombo::addItem);
        gbc.gridx = 1; gbc.weightx = 1;
        card.add(deptCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        card.add(new JLabel("Period:"), gbc);

        deptPeriodCombo = new JComboBox<>();
        loadDeptPeriods();
        gbc.gridx = 1; gbc.weightx = 1;
        card.add(deptPeriodCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton generate = UIStyle.createPrimaryButton("Generate Department Report PDF");
        generate.addActionListener(e -> onGenerateDeptReport());
        card.add(generate, gbc);

        outer.add(card, BorderLayout.NORTH);
        outer.add(createDeptReportInstructions(), BorderLayout.CENTER);
        return outer;
    }

    private JPanel createDeptReportInstructions() {
        JPanel card = UIStyle.createElevatedCard();
        JLabel heading = new JLabel("Department Report");
        heading.setFont(UIStyle.FONT_HEADING);
        heading.setForeground(UIStyle.PRIMARY);

        JLabel body = new JLabel("<html>"
                + "<ul style='margin-left:16px'>"
                + "<li>Shows gross pay, deductions, and net pay for every employee.</li>"
                + "<li>Filter by department and/or pay period, or leave both on <i>All</i> for a full summary.</li>"
                + "<li>The PDF is generated in landscape A4 with grand totals at the bottom.</li>"
                + "<li>Available to HR and Admin roles only.</li>"
                + "</ul>"
                + "</html>");
        body.setFont(UIStyle.FONT_BODY);

        card.add(heading);
        card.add(Box.createVerticalStrut(8));
        card.add(body);
        return card;
    }
    
    private JPanel createTimecardTab() {
    JPanel outer = new JPanel(new BorderLayout(12, 12));
    outer.setBackground(UIStyle.BACKGROUND);
    outer.setBorder(new EmptyBorder(16, 0, 0, 0));

    JPanel card = UIStyle.createElevatedCard();
    card.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets  = new Insets(8, 8, 8, 8);
    gbc.fill    = GridBagConstraints.HORIZONTAL;
    gbc.anchor  = GridBagConstraints.WEST;

    gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
    card.add(new JLabel("Pay Period:"), gbc);

    timecardPeriodCombo = new JComboBox<>();
    loadTimecardPeriods();
    gbc.gridx = 1; gbc.weightx = 1;
    card.add(timecardPeriodCombo, gbc);

    gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
    JLabel info = new JLabel(
            "<html><i style='color:#888'>The timecard will be saved as a PDF " +
            "and opened automatically.</i></html>");
    info.setFont(UIStyle.FONT_SMALL);
    card.add(info, gbc);

    gbc.gridy = 2;
    JButton generate = UIStyle.createPrimaryButton("Generate Timecard PDF");
    generate.addActionListener(e -> onGenerateTimecard());
    card.add(generate, gbc);

    outer.add(card, BorderLayout.NORTH);
    return outer;
}

    private void onGenerateDeptReport() {
        String dept   = (String) deptCombo.getSelectedItem();
        String period = (String) deptPeriodCombo.getSelectedItem();

        String generatedBy = employeeID;

        JFileChooser fc = new JFileChooser();
        String safeDept = (dept == null ? "All" : dept.replace(" ", "_").replace("/", "-"));
        fc.setSelectedFile(new File("PayrollReport_" + safeDept + ".pdf"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File outFile = fc.getSelectedFile();
        if (!outFile.getName().endsWith(".pdf"))
            outFile = new File(outFile.getAbsolutePath() + ".pdf");

        setStatus(" Generating department report…", UIStyle.TEXT_SECONDARY);
        final File   finalOut    = outFile;
        final String finalDept   = (dept   != null && dept.startsWith("All"))   ? "" : dept;
        final String finalPeriod = (period != null && period.startsWith("All")) ? "" : period;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            String errMsg = null;

            @Override
            protected Void doInBackground() {
                try {
                    reportService.generateDepartmentReport(
                            role, generatedBy, finalDept, finalPeriod, finalOut);
                } catch (ReportException ex) {
                    errMsg = ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (errMsg != null) {
                    setStatus("✗  " + errMsg, UIStyle.DANGER);
                    JOptionPane.showMessageDialog(ReportPanel.this,
                            errMsg, "Report Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    setStatus("✔  Report saved: " + finalOut.getName(), UIStyle.SUCCESS);
                    openFile(finalOut);
                }
            }
        };
        worker.execute();
    }

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIStyle.SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyle.BORDER),
                new EmptyBorder(6, 10, 6, 10)));

        statusLabel = new JLabel("Ready.");
        statusLabel.setFont(UIStyle.FONT_SMALL);
        statusLabel.setForeground(UIStyle.TEXT_SECONDARY);

        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }


    private void loadPayslipEmployees() {
        payslipEmpCombo.removeAllItems();
        boolean isHR = payrollService.canViewAllPayroll(role);

        for (String[] emp : new com.mycompany.motorphapps.service.EmployeeService().getAllEmployees()) {
            if (!isHR && !emp[0].equals(employeeID)) continue;
            payslipEmpCombo.addItem(emp[0] + " - " + emp[2] + " " + emp[1]);
        }
    }

    private void loadPayslipPeriods() {
        payslipPeriodCombo.removeAllItems();
        payslipPeriodCombo.addItem("-- Select Period --");

        Object sel = payslipEmpCombo.getSelectedItem();
        if (sel == null) return;

        String empId = sel.toString().split(" - ")[0].trim();
        boolean found = false;
        for (String[] row : new com.mycompany.motorphapps.dao.PayrollDAO().readAllPayroll()) {
            if (row[1].equals(empId)) {
                payslipPeriodCombo.addItem(row[3]);
                found = true;
            }
        }

        if (!found) {
            int year = java.time.LocalDate.now().getYear();
            for (int m = 1; m <= 12; m++) {
                YearMonth ym = YearMonth.of(year, m);
                payslipPeriodCombo.addItem(year + "-" + String.format("%02d", m) + "-01 to "
                        + year + "-" + String.format("%02d", m) + "-15");
                payslipPeriodCombo.addItem(year + "-" + String.format("%02d", m) + "-16 to "
                        + year + "-" + String.format("%02d", m) + "-" + ym.lengthOfMonth());
            }
        }
    }
    
    private void onGenerateTimecard() {
    Object perSel = timecardPeriodCombo.getSelectedItem();
    if (perSel == null || perSel.toString().startsWith("--")) {
        setStatus("  Please select a pay period.", UIStyle.WARNING);
        return;
    }
    String period = perSel.toString();

    JFileChooser fc = new JFileChooser();
    fc.setSelectedFile(new File("Timecard_" + employeeID + "_" + period.replace(" ", "_") + ".pdf"));
    if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

    File outFile = fc.getSelectedFile();
    if (!outFile.getName().endsWith(".pdf"))
        outFile = new File(outFile.getAbsolutePath() + ".pdf");

    setStatus(" Generating timecard…", UIStyle.TEXT_SECONDARY);
    final File finalOut = outFile;

    SwingWorker<Void, Void> worker = new SwingWorker<>() {
        String errMsg = null;

        @Override
        protected Void doInBackground() {
            try {
                reportService.generateTimecard(employeeID, role, employeeID, period, finalOut);
            } catch (ReportException ex) {
                errMsg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void done() {
            if (errMsg != null) {
                setStatus("✗  " + errMsg, UIStyle.DANGER);
                JOptionPane.showMessageDialog(ReportPanel.this,
                        errMsg, "Report Error", JOptionPane.ERROR_MESSAGE);
            } else {
                setStatus("✔  Timecard saved: " + finalOut.getName(), UIStyle.SUCCESS);
                openFile(finalOut);
            }
        }
    };
    worker.execute();
}

    private void loadDeptPeriods() {
        deptPeriodCombo.removeAllItems();
        deptPeriodCombo.addItem("All Periods");

        java.util.Set<String> seen = new java.util.LinkedHashSet<>();
        for (String[] row : new com.mycompany.motorphapps.dao.PayrollDAO().readAllPayroll()) {
            seen.add(row[3]);
        }
        seen.forEach(deptPeriodCombo::addItem);
    }
    
    private void loadTimecardPeriods() {
    timecardPeriodCombo.removeAllItems();
    timecardPeriodCombo.addItem("-- Select Period --");

    java.util.Set<String> seen = new java.util.LinkedHashSet<>();
    for (String[] row : new com.mycompany.motorphapps.dao.PayrollDAO().readAllPayroll()) {
        if (row[1].equals(employeeID)) seen.add(row[3]);
    }
    seen.forEach(timecardPeriodCombo::addItem);
}

    private void openFile(File file) {
        if (!Desktop.isDesktopSupported()) return;
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "PDF saved but could not open automatically.\nPath: " + file.getAbsolutePath(),
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}