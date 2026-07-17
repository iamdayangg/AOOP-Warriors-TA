package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.model.AttendanceRow;
import com.mycompany.motorphapps.model.AttendanceSummaryResult;
import com.mycompany.motorphapps.model.AttendanceSummaryResult.EmployeeHoursRow;
import com.mycompany.motorphapps.service.AttendanceService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Attendance Panel — professional navy blue theme.
 * Display-only: all logic lives in AttendanceService.
 *
 * @author DAYANG GWAPA
 */
public class AttendancePanel extends JPanel {

    private DefaultTableModel model;
    private JTable            table;
    private final AttendanceService attendanceService;
    private final String employeeID;
    private final String role;

    private JLabel  summaryTitleLabel;
    private JPanel  summaryChipsPanel;
    private JPanel  summaryWrapper;
    private final List<AttendanceRow> rowStyles = new ArrayList<>();

    public AttendancePanel(String employeeID, String role) {
        this.employeeID = employeeID;
        this.role       = role;
        attendanceService = new AttendanceService();

        setLayout(new BorderLayout(0, 0));
        setBackground(UIStyle.BACKGROUND);

        String[] columns = {
            "Employee ID", "Employee Name", "Date",
            "Time In", "Time Out", "Total Hours", "OT Status"
        };
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        add(buildTopBar(),       BorderLayout.NORTH);
        add(buildTableArea(),    BorderLayout.CENTER);
        add(buildSummaryPanel(), BorderLayout.SOUTH);

        refresh();
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIStyle.PRIMARY);
        bar.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Left: icon + title + live clock
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel title = new JLabel("Attendance Records");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel clockLabel = new JLabel();
        clockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        clockLabel.setForeground(new Color(180, 210, 255));

        // Live clock timer
        Timer clockTimer = new Timer(1000, e -> {
            clockLabel.setText(LocalTime.now().format(
                    DateTimeFormatter.ofPattern("hh:mm:ss a")));
        });
        clockTimer.start();
        clockLabel.setText(LocalTime.now().format(
                DateTimeFormatter.ofPattern("hh:mm:ss a")));

        left.add(icon);
        left.add(title);
        left.add(Box.createHorizontalStrut(16));
        left.add(clockLabel);

        // Right: action buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton timeInBtn  = buildHeaderButton(" Time In",  new Color(22, 130, 80));
        JButton timeOutBtn = buildHeaderButton(" Time Out", new Color(192, 38, 38));

        timeInBtn.addActionListener(e -> {
            boolean ok = attendanceService.recordTimeIn(employeeID, LocalTime.now());
            JOptionPane.showMessageDialog(this,
                    ok ? "Timed in successfully." : "Already timed in today.",
                    ok ? "Success" : "Warning",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            refresh();
        });

        timeOutBtn.addActionListener(e -> {
            boolean ok = attendanceService.recordTimeOut(employeeID, LocalTime.now());
            JOptionPane.showMessageDialog(this,
                    ok ? "Timed out successfully." : "No Time In found. Please Time In first.",
                    ok ? "Success" : "Warning",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            refresh();
        });

        right.add(timeInBtn);
        right.add(timeOutBtn);

        if (attendanceService.canDeleteAttendance(role)) {
            JButton deleteBtn = buildHeaderButton(" Delete", new Color(120, 40, 40));
            deleteBtn.addActionListener(e -> deleteSelected());
            right.add(deleteBtn);
        }

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JButton buildHeaderButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        return btn;
    }

    // ── Table area ────────────────────────────────────────────────────────────
    private JPanel buildTableArea() {
        table = new JTable(model);
        UIStyle.styleTable(table);

        // Override renderers for hours + OT columns
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel,
                    boolean focus, int row, int col) {

                Component c = super.getTableCellRendererComponent(
                        t, value, sel, focus, row, col);

                if (!sel) {
                    if (col == 5) {
                        c.setBackground(new Color(232, 255, 240));
                        ((JLabel) c).setFont(new Font("Segoe UI", Font.BOLD, 13));
                        c.setForeground(new Color(22, 130, 80));
                    } else if (col == 6 && row < rowStyles.size()) {
                        AttendanceRow ar = rowStyles.get(row);
                        int[] bg = ar.getStatusBackgroundRGB();
                        int[] fg = ar.getStatusForegroundRGB();
                        c.setBackground(new Color(bg[0], bg[1], bg[2]));
                        ((JLabel) c).setForeground(new Color(fg[0], fg[1], fg[2]));
                        ((JLabel) c).setFont(new Font("Segoe UI",
                                ar.isStatusBold() ? Font.BOLD : Font.PLAIN, 13));
                    } else {
                        c.setBackground(row % 2 == 0 ? UIStyle.SURFACE : UIStyle.ROW_ALT);
                        c.setForeground(UIStyle.TEXT_PRIMARY);
                    }
                }
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                ((JLabel) c).setBorder(new EmptyBorder(0, 6, 0, 6));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(UIStyle.BORDER, 1, true));
        scroll.getViewport().setBackground(UIStyle.SURFACE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIStyle.BACKGROUND);
        wrapper.setBorder(new EmptyBorder(16, 24, 8, 24));
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Summary panel ─────────────────────────────────────────────────────────
    private JPanel buildSummaryPanel() {
        summaryWrapper = new JPanel(new BorderLayout(8, 8));
        summaryWrapper.setBackground(UIStyle.SURFACE);
        summaryWrapper.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UIStyle.BORDER),
                new EmptyBorder(14, 24, 14, 24)));

        summaryTitleLabel = new JLabel("");
        summaryTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        summaryTitleLabel.setForeground(UIStyle.PRIMARY);

        summaryChipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        summaryChipsPanel.setBackground(UIStyle.SURFACE);

        summaryWrapper.add(summaryTitleLabel, BorderLayout.NORTH);
        summaryWrapper.add(summaryChipsPanel, BorderLayout.CENTER);
        return summaryWrapper;
    }

    // ── Refresh ───────────────────────────────────────────────────────────────
    private void refresh() {
        populateTable();
        populateSummary();
    }

    private void populateTable() {
        model.setRowCount(0);
        rowStyles.clear();
        for (AttendanceRow r : attendanceService.getAttendanceRows(employeeID, role)) {
            rowStyles.add(r);
            model.addRow(new Object[]{
                r.getEmployeeId(), r.getEmployeeName(), r.getDate(),
                r.getTimeIn(), r.getTimeOut(), r.getTotalHours(), r.getOvertimeStatus()
            });
        }
    }

    private void populateSummary() {
        AttendanceSummaryResult result = attendanceService.getSummaryResult(employeeID, role);

        summaryTitleLabel.setText(result.getSummaryLabel());
        summaryChipsPanel.removeAll();

        addSummaryChip("My Total Hours",     result.getMyTotalHours(),    UIStyle.PRIMARY);
        if (result.isShowAllTotal()) {
            addSummaryChip("All Employees Total", result.getAllEmployeesTotal(), UIStyle.SUCCESS);
        }

        // Remove old calculate btn if present
        if (summaryWrapper.getComponentCount() > 2)
            summaryWrapper.remove(2);

        if (result.isShowCalculateBtn()) {
            JButton calcBtn = UIStyle.createPrimaryButton(" Calculate All Employee Hours");
            calcBtn.addActionListener(e -> showBreakdownDialog(result));
            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            btnRow.setBackground(UIStyle.SURFACE);
            btnRow.add(calcBtn);
            summaryWrapper.add(btnRow, BorderLayout.SOUTH);
        }

        summaryChipsPanel.revalidate();
        summaryChipsPanel.repaint();
        summaryWrapper.revalidate();
        summaryWrapper.repaint();
    }

    private void addSummaryChip(String label, String value, Color color) {
        JPanel chip = new JPanel(new BorderLayout(4, 2));
        chip.setBackground(UIStyle.SURFACE);
        chip.setBorder(new CompoundBorder(
                new LineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80), 1, true),
                new EmptyBorder(10, 16, 10, 16)));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIStyle.FONT_SMALL);
        lbl.setForeground(UIStyle.TEXT_SECONDARY);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 18));
        val.setForeground(color);

        chip.add(lbl, BorderLayout.NORTH);
        chip.add(val, BorderLayout.CENTER);
        summaryChipsPanel.add(chip);
    }

    // ── Breakdown dialog ──────────────────────────────────────────────────────
    private void showBreakdownDialog(AttendanceSummaryResult result) {
        String[] cols = {"Employee ID", "Employee Name", "Total Hours"};
        DefaultTableModel dm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (EmployeeHoursRow row : result.getEmployeeBreakdown()) {
            dm.addRow(new Object[]{
                row.getEmployeeId(), row.getEmployeeName(), row.getTotalHours()
            });
        }

        JTable t = new JTable(dm);
        UIStyle.styleTable(t);

        JScrollPane scroll = new JScrollPane(t);
        scroll.setPreferredSize(new Dimension(520, 320));
        scroll.setBorder(new LineBorder(UIStyle.BORDER, 1, true));

        JLabel grandTotal = new JLabel("Grand Total: " + result.getAllEmployeesTotal());
        grandTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        grandTotal.setForeground(UIStyle.SUCCESS);
        grandTotal.setBorder(new EmptyBorder(10, 4, 4, 4));

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UIStyle.SURFACE);
        panel.add(scroll,      BorderLayout.CENTER);
        panel.add(grandTotal,  BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel,
                "Total Hours Worked — All Employees",
                JOptionPane.PLAIN_MESSAGE);
    }

    // ── Delete selected record ────────────────────────────────────────────────
    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to delete.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String empId = model.getValueAt(row, 0).toString();
        String date  = model.getValueAt(row, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete attendance record for Employee " + empId + " on " + date + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            attendanceService.deleteAttendance(empId, date);
            JOptionPane.showMessageDialog(this, "Record deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            refresh();
        }
    }
}