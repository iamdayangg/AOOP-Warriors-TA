package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.model.LeaveRequest;
import com.mycompany.motorphapps.service.LeaveService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author DAYANG GWAPA
 */
public class LeavePanel extends JPanel {

    private JTable            table;
    private DefaultTableModel model;
    private LeaveService      leaveService;
    private final String      role;
    private final String      employeeId;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public LeavePanel(String role, String employeeId) {
        this.role       = role;
        this.employeeId = employeeId;
        leaveService    = new LeaveService();

        setLayout(new BorderLayout(0, 0));
        setBackground(UIStyle.BACKGROUND);

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildTable(),     BorderLayout.CENTER);
        add(buildButtonBar(), BorderLayout.SOUTH);

        loadLeaves();
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIStyle.PRIMARY);
        bar.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel title = new JLabel("Leave Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        left.add(icon);
        left.add(title);
        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        List<LeaveRequest> all = leaveService.getAllLeaves();
        long pending  = all.stream().filter(l -> "Pending".equalsIgnoreCase(l.getStatus())).count();
        long approved = all.stream().filter(l -> "Approved".equalsIgnoreCase(l.getStatus())).count();

        right.add(buildHeaderChip("Pending",  String.valueOf(pending),  new Color(200, 140,  0)));
        right.add(buildHeaderChip("Approved", String.valueOf(approved), new Color( 22, 130, 80)));

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JLabel buildHeaderChip(String label, String value, Color color) {
        JLabel chip = new JLabel(label + ": " + value) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chip.setForeground(color);
        chip.setOpaque(false);
        chip.setBorder(new EmptyBorder(5, 12, 5, 12));
        return chip;
    }

    private JPanel buildTable() {
        model = new DefaultTableModel(
                new String[]{"Leave ID", "Employee ID", "Type", "Start Date", "End Date", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        UIStyle.styleTable(table);

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel, boolean focus, int row, int col) {

                JPanel cell = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 3));
                cell.setOpaque(true);
                cell.setBackground(sel ? UIStyle.PRIMARY_LIGHT
                        : row % 2 == 0 ? UIStyle.SURFACE : UIStyle.ROW_ALT);

                String status = value == null ? "" : value.toString();
                Color bg;
                switch (status.toLowerCase()) {
                    case "approved": bg = new Color(22, 130, 80);  break;
                    case "rejected": bg = new Color(192, 38, 38);  break;
                    default:         bg = new Color(200, 140,  0); break;  // Pending
                }
                cell.add(UIStyle.createBadge(status, bg, Color.WHITE));
                return cell;
            }
        });

        int[] widths = {80, 110, 140, 120, 120, 110};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(UIStyle.BORDER, 1, true));
        scroll.getViewport().setBackground(UIStyle.SURFACE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIStyle.BACKGROUND);
        wrapper.setBorder(new EmptyBorder(16, 24, 8, 24));
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }
    
    private JPanel buildButtonBar() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(UIStyle.BACKGROUND);
        outer.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UIStyle.BORDER),
                new EmptyBorder(12, 24, 16, 24)));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        if (leaveService.canManageLeave(role)) {
            JButton deleteBtn  = UIStyle.createDangerButton(" Delete");
            JButton rejectBtn  = UIStyle.createSecondaryButton(" Reject");
            JButton approveBtn = UIStyle.createPrimaryButton(" Approve");

            deleteBtn.addActionListener(e  -> deleteLeave());
            rejectBtn.addActionListener(e  -> updateStatus("Rejected"));
            approveBtn.addActionListener(e -> updateStatus("Approved"));

            btnRow.add(deleteBtn);
            btnRow.add(rejectBtn);
            btnRow.add(approveBtn);
        } else {
            JButton requestBtn = UIStyle.createPrimaryButton("＋  Request Leave");
            requestBtn.addActionListener(e -> requestLeave());
            btnRow.add(requestBtn);
        }

        outer.add(btnRow, BorderLayout.EAST);
        return outer;
    }

    // ── Leave request dialog ──────────────────────────────────────────────────
    private void requestLeave() {
        // Leave type combo
        JComboBox<String> typeCombo = new JComboBox<>(leaveService.getLeaveTypes());
        typeCombo.setFont(UIStyle.FONT_BODY);

        // --- FIX: Use JSpinner with DateEditor instead of plain JTextField ---
        // This completely eliminates format errors — the spinner only produces valid dates.
        JSpinner startSpinner = createDateSpinner();
        JSpinner endSpinner   = createDateSpinner();

        // Default end date to tomorrow so range is always valid out of the box
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        endSpinner.setValue(cal.getTime());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyle.SURFACE);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(7, 8, 7, 8);
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        addDialogRow(form, gc, 0, "Leave Type:",  typeCombo);
        addDialogRow(form, gc, 1, "Start Date:",  startSpinner);
        addDialogRow(form, gc, 2, "End Date:",    endSpinner);

        JPanel wrapper = buildDialogWrapper("Request Leave", form);

        int result = JOptionPane.showConfirmDialog(this, wrapper,
                "Request Leave", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        // Commit any in-progress spinner edit before reading values
        try { startSpinner.commitEdit(); } catch (java.text.ParseException ignored) {}
        try { endSpinner.commitEdit();   } catch (java.text.ParseException ignored) {}

        // Format the spinner Dates to yyyy-MM-dd strings for the service
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String startDate = sdf.format((Date) startSpinner.getValue());
        String endDate   = sdf.format((Date) endSpinner.getValue());

        try {
            leaveService.requestLeave(
                    employeeId,
                    (String) typeCombo.getSelectedItem(),
                    startDate,
                    endDate);
            loadLeaves();
            JOptionPane.showMessageDialog(this, "Leave request submitted.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates a JSpinner configured for date-only input in yyyy-MM-dd display format.
     * The value is always a valid java.util.Date — no manual parsing needed.
     */
    private JSpinner createDateSpinner() {
        // Start from today
        SpinnerDateModel dateModel = new SpinnerDateModel(
                new Date(),                          
                null,                                
                null,                               
                java.util.Calendar.DAY_OF_MONTH);   

        JSpinner spinner = new JSpinner(dateModel);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, DATE_FORMAT);
        spinner.setEditor(editor);
        spinner.setFont(UIStyle.FONT_BODY);

        // Match the field styling used elsewhere in the panel
        spinner.setBorder(new CompoundBorder(
                new LineBorder(UIStyle.BORDER, 1, true),
                new EmptyBorder(2, 4, 2, 4)));

        // Style the inner text field of the spinner
        JFormattedTextField tf = editor.getTextField();
        tf.setFont(UIStyle.FONT_BODY);
        tf.setForeground(UIStyle.TEXT_PRIMARY);
        tf.setBackground(UIStyle.SURFACE);
        tf.setHorizontalAlignment(JTextField.LEFT);

        return spinner;
    }

    private void updateStatus(String status) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a leave request first.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String leaveId = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + status.toLowerCase() + " this leave request?",
                status + " Leave", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            leaveService.updateStatus(leaveId, status);
            loadLeaves();
            JOptionPane.showMessageDialog(this,
                    "Leave request " + status.toLowerCase() + ".",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteLeave() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a record first.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this leave record?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String leaveId = model.getValueAt(row, 0).toString();
            leaveService.deleteLeave(leaveId);
            loadLeaves();
            JOptionPane.showMessageDialog(this, "Leave record deleted.",
                    "Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadLeaves() {
        model.setRowCount(0);
        for (LeaveRequest leave : leaveService.getAllLeaves()) {
            if (leaveService.canViewAllLeaves(role) || leave.getEmployeeId().equals(employeeId)) {
                model.addRow(new Object[]{
                    leave.getId(), leave.getEmployeeId(), leave.getLeaveType(),
                    leave.getStartDate(), leave.getEndDate(), leave.getStatus()
                });
            }
        }
    }

    private void addDialogRow(JPanel p, GridBagConstraints gc,
                               int row, String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIStyle.FONT_BODY);
        lbl.setForeground(UIStyle.TEXT_SECONDARY);
        p.add(lbl, gc);

        gc.gridx = 1; gc.weightx = 0.7;
        p.add(field, gc);
    }

    private JPanel buildDialogWrapper(String heading, JPanel form) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setBackground(UIStyle.SURFACE);
        wrapper.setBorder(new EmptyBorder(4, 4, 4, 4));

        JLabel title = new JLabel(heading);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(UIStyle.PRIMARY);
        title.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIStyle.BORDER),
                new EmptyBorder(0, 0, 10, 0)));

        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(form,  BorderLayout.CENTER);
        return wrapper;
    }
}