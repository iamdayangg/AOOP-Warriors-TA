package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.service.EmployeeService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Employee Profile panel — professional navy blue theme.
 * Uses a two-column card layout instead of a raw text area.
 *
 * Now includes an "Edit Profile" dialog. The dialog accepts the birthday
 * in MM-dd-yyyy, MM/dd/yyyy, or yyyy-MM-dd format and always converts it
 * to yyyy-MM-dd (MySQL/MariaDB DATE format) before calling
 * EmployeeService.updateEmployee(), preventing "Data truncation: Incorrect
 * date value" errors.
 *
 * @author DAYANG GWAPA
 */
public class EmployeeInfoPanel extends JPanel {

    private BufferedImage profileImage;
    private final EmployeeService employeeService;
    private final String employeeID;
    private final String role;

    // Kept so the edit dialog can refresh the view after a successful save.
    private JPanel contentHolder;

    public EmployeeInfoPanel(String employeeID, String role) {
        this.employeeID = employeeID;
        this.role = role;
        employeeService = new EmployeeService();

        String gender = getEmployeeGender(employeeID);
        if ("admin".equalsIgnoreCase(role)) {
            profileImage = loadImage("/images/adminProfile.png");
        } else if ("Male".equalsIgnoreCase(gender)) {
            profileImage = loadImage("/images/user_male.png");
        } else {
            profileImage = loadImage("/images/user.png");
        }

        setLayout(new BorderLayout());
        setBackground(UIStyle.BACKGROUND);

        add(buildTopBar(),              BorderLayout.NORTH);

        contentHolder = new JPanel(new BorderLayout());
        contentHolder.setBackground(UIStyle.BACKGROUND);
        contentHolder.add(buildProfileContent(employeeID), BorderLayout.CENTER);
        add(contentHolder, BorderLayout.CENTER);
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIStyle.PRIMARY);
        bar.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        left.add(icon);
        left.add(title);
        bar.add(left, BorderLayout.WEST);

        // ── Edit Profile button ────────────────────────────────────────────
        JButton editBtn = new JButton("Edit Profile");
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        editBtn.setForeground(UIStyle.PRIMARY);
        editBtn.setBackground(Color.WHITE);
        editBtn.setFocusPainted(false);
        editBtn.setBorder(new EmptyBorder(8, 16, 8, 16));
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editBtn.addActionListener(e -> openEditDialog());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(editBtn);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    // ── Main content ──────────────────────────────────────────────────────────
    private JPanel buildProfileContent(String employeeID) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(UIStyle.BACKGROUND);
        outer.setBorder(new EmptyBorder(24, 32, 24, 32));

        String[] data = getEmployeeData(employeeID);

        // ── Header card (avatar + name + position) ────────────────────────
        JPanel headerCard = new JPanel(new BorderLayout(20, 0));
        headerCard.setBackground(UIStyle.PRIMARY);
        headerCard.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Avatar
        JLabel avatarLabel = buildAvatar();

        // Name + info
        JPanel nameBlock = new JPanel();
        nameBlock.setOpaque(false);
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));

        String firstName = data.length > 2 ? data[2] : "";
        String lastName  = data.length > 1 ? data[1] : "";
        JLabel nameLabel = new JLabel(firstName + " " + lastName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel posLabel = new JLabel(data.length > 11 ? data[11] : "");
        posLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        posLabel.setForeground(new Color(180, 210, 255));
        posLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        posLabel.setBorder(new EmptyBorder(4, 0, 8, 0));

        // Status badge
        String status = data.length > 10 ? data[10] : "";
        Color badgeBg = "Regular".equalsIgnoreCase(status)
                ? new Color(22, 130, 80) : new Color(200, 140, 0);
        JLabel badge = UIStyle.createBadge(status, badgeBg, Color.WHITE);
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel idLabel = new JLabel("ID: " + (data.length > 0 ? data[0] : ""));
        idLabel.setFont(UIStyle.FONT_SMALL);
        idLabel.setForeground(new Color(160, 195, 240));
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        idLabel.setBorder(new EmptyBorder(10, 0, 0, 0));

        nameBlock.add(nameLabel);
        nameBlock.add(posLabel);
        nameBlock.add(badge);
        nameBlock.add(idLabel);

        headerCard.add(avatarLabel, BorderLayout.WEST);
        headerCard.add(nameBlock,   BorderLayout.CENTER);

        outer.add(headerCard, BorderLayout.NORTH);

        // ── Info sections ─────────────────────────────────────────────────────
        JPanel sections = new JPanel();
        sections.setLayout(new BoxLayout(sections, BoxLayout.Y_AXIS));
        sections.setBackground(UIStyle.BACKGROUND);
        sections.setBorder(new EmptyBorder(20, 0, 0, 0));

        sections.add(buildInfoSection("Personal Information", new String[][]{
            {"Employee ID",   data.length > 0  ? data[0]  : ""},
            {"Last Name",     data.length > 1  ? data[1]  : ""},
            {"First Name",    data.length > 2  ? data[2]  : ""},
            {"Birthday",      data.length > 3  ? data[3]  : ""},
            {"Address",       data.length > 4  ? data[4]  : ""},
            {"Phone Number",  data.length > 5  ? data[5]  : ""}
        }));

        sections.add(Box.createVerticalStrut(16));
        sections.add(buildInfoSection("Government IDs", new String[][]{
            {"SSS #",        data.length > 6  ? data[6]  : ""},
            {"PhilHealth #", data.length > 7  ? data[7]  : ""},
            {"TIN #",        data.length > 8  ? data[8]  : ""},
            {"Pag-ibig #",   data.length > 9  ? data[9]  : ""}
        }));

        sections.add(Box.createVerticalStrut(16));
        sections.add(buildInfoSection("Employment Details", new String[][]{
            {"Status",              data.length > 10 ? data[10] : ""},
            {"Position",            data.length > 11 ? data[11] : ""},
            {"Immediate Supervisor",data.length > 12 ? data[12] : ""}
        }));

        sections.add(Box.createVerticalStrut(16));
        sections.add(buildInfoSection("Compensation", new String[][]{
            {"Basic Salary",            data.length > 13 ? data[13] : ""},
            {"Rice Subsidy",            data.length > 14 ? data[14] : ""},
            {"Phone Allowance",         data.length > 15 ? data[15] : ""},
            {"Clothing Allowance",      data.length > 16 ? data[16] : ""},
            {"Gross Semi-monthly Rate", data.length > 17 ? data[17] : ""},
            {"Hourly Rate",             data.length > 18 ? data[18] : ""}
        }));

        JScrollPane scroll = new JScrollPane(sections);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(UIStyle.BACKGROUND);
        scroll.getViewport().setBackground(UIStyle.BACKGROUND);
        outer.add(scroll, BorderLayout.CENTER);

        return outer;
    }

    private JLabel buildAvatar() {
        JLabel label = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                // Circle clip
                g2.setClip(new java.awt.geom.Ellipse2D.Double(0, 0, 80, 80));
                if (profileImage != null) {
                    g2.drawImage(profileImage, 0, 0, 80, 80, null);
                } else {
                    g2.setColor(UIStyle.PRIMARY_LIGHT);
                    g2.fillOval(0, 0, 80, 80);
                }
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public Dimension getPreferredSize() { return new Dimension(80, 80); }
        };
        label.setBorder(null);
        return label;
    }

    private JPanel buildInfoSection(String title, String[][] rows) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(UIStyle.SURFACE);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setBorder(new CompoundBorder(
                new LineBorder(UIStyle.BORDER, 1, true),
                new EmptyBorder(16, 20, 16, 20)));

        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sectionTitle.setForeground(UIStyle.PRIMARY);
        sectionTitle.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIStyle.BORDER),
                new EmptyBorder(0, 0, 10, 0)));
        card.add(sectionTitle, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 16, 8));
        grid.setOpaque(false);

        for (String[] row : rows) {
            JPanel item = new JPanel(new BorderLayout(0, 2));
            item.setOpaque(false);

            JLabel lbl = new JLabel(row[0]);
            lbl.setFont(UIStyle.FONT_SMALL);
            lbl.setForeground(UIStyle.TEXT_SECONDARY);

            String val = (row[1] == null || row[1].isBlank()) ? "—" : row[1];
            JLabel valLabel = new JLabel(val);
            valLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            valLabel.setForeground(UIStyle.TEXT_PRIMARY);

            item.add(lbl,      BorderLayout.NORTH);
            item.add(valLabel, BorderLayout.CENTER);
            grid.add(item);
        }

        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EDIT PROFILE DIALOG
    // ─────────────────────────────────────────────────────────────────────────
    private void openEditDialog() {
        String[] data = getEmployeeData(employeeID);
        // Ensure we always have 20 slots to work with.
        String[] current = new String[20];
        for (int i = 0; i < 20; i++) current[i] = i < data.length && data[i] != null ? data[i] : "";

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Edit Profile", true);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        form.setBackground(UIStyle.SURFACE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.gridx = 0;
        gbc.weightx = 0;

        // Editable fields. Birthday gets a format hint.
        JTextField lastNameField  = new JTextField(current[1]);
        JTextField firstNameField = new JTextField(current[2]);
        JTextField birthdayField  = new JTextField(current[3]);
        JTextField addressField   = new JTextField(current[4]);
        JTextField phoneField     = new JTextField(current[5]);
        JTextField sssField       = new JTextField(current[6]);
        JTextField philhealthField= new JTextField(current[7]);
        JTextField tinField       = new JTextField(current[8]);
        JTextField pagibigField   = new JTextField(current[9]);

        String[][] rows = {
            {"Last Name", null},
            {"First Name", null},
            {"Birthday (MM-DD-YYYY or YYYY-MM-DD)", null},
            {"Address", null},
            {"Phone Number", null},
            {"SSS #", null},
            {"PhilHealth #", null},
            {"TIN #", null},
            {"Pag-ibig #", null}
        };
        JTextField[] fields = {
            lastNameField, firstNameField, birthdayField, addressField, phoneField,
            sssField, philhealthField, tinField, pagibigField
        };

        int y = 0;
        for (int i = 0; i < fields.length; i++) {
            gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0;
            JLabel lbl = new JLabel(rows[i][0] + ":");
            lbl.setFont(UIStyle.FONT_SMALL);
            lbl.setForeground(UIStyle.TEXT_SECONDARY);
            form.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 1;
            fields[i].setColumns(20);
            form.add(fields[i], gbc);
            y++;
        }

        dialog.add(form, BorderLayout.CENTER);

        // ── Buttons ─────────────────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UIStyle.SURFACE);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(UIStyle.PRIMARY);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);

        saveBtn.addActionListener(e -> {
            String rawBirthday = birthdayField.getText().trim();
            String mysqlBirthday;
            try {
                mysqlBirthday = toMysqlDate(rawBirthday);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Birthday must be in MM-DD-YYYY, MM/DD/YYYY, or YYYY-MM-DD format.",
                        "Invalid Date",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Build the full 20-element array expected by EmployeeService/EmployeeDAO.
            String[] updated = current.clone();
            updated[1] = lastNameField.getText().trim();
            updated[2] = firstNameField.getText().trim();
            updated[3] = mysqlBirthday;
            updated[4] = addressField.getText().trim();
            updated[5] = phoneField.getText().trim();
            updated[6] = sssField.getText().trim();
            updated[7] = philhealthField.getText().trim();
            updated[8] = tinField.getText().trim();
            updated[9] = pagibigField.getText().trim();

            try {
                employeeService.updateEmployee(updated);
                JOptionPane.showMessageDialog(dialog,
                        "Profile updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshContent();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to update profile:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    /**
     * Converts a date string to MySQL/MariaDB DATE format (yyyy-MM-dd).
     * Accepts MM-dd-yyyy, MM/dd/yyyy, or yyyy-MM-dd as input.
     * Throws DateTimeParseException if none of the formats match.
     */
    private String toMysqlDate(String input) {
        if (input == null || input.isBlank()) {
            throw new DateTimeParseException("Birthday is required.", input == null ? "" : input, 0);
        }

        String[] patterns = {"MM-dd-yyyy", "MM/dd/yyyy", "yyyy-MM-dd"};
        for (String pattern : patterns) {
            try {
                LocalDate date = LocalDate.parse(input, DateTimeFormatter.ofPattern(pattern));
                return date.format(DateTimeFormatter.ISO_LOCAL_DATE); // yyyy-MM-dd
            } catch (DateTimeParseException ignored) {
                // try next pattern
            }
        }
        // None of the accepted formats matched.
        throw new DateTimeParseException("Unrecognized date format: " + input, input, 0);
    }

    /** Rebuilds the profile content after a successful save. */
    private void refreshContent() {
        contentHolder.removeAll();
        contentHolder.add(buildProfileContent(employeeID), BorderLayout.CENTER);
        contentHolder.revalidate();
        contentHolder.repaint();
    }

    // ── Data helpers ──────────────────────────────────────────────────────────
    private String[] getEmployeeData(String employeeID) {
        for (String[] emp : employeeService.getAllEmployees()) {
            if (emp.length > 0 && emp[0].trim().equals(employeeID.trim())) {
                String[] data = new String[20];
                for (int i = 0; i < data.length; i++)
                    data[i] = i < emp.length ? emp[i].trim() : "";
                return data;
            }
        }
        return new String[20];
    }

    private String getEmployeeGender(String employeeID) {
        for (String[] emp : employeeService.getAllEmployees()) {
            if (emp.length > 0 && emp[0].trim().equals(employeeID.trim())) {
                if (emp.length > 19 && !emp[19].isEmpty()) return emp[19];
                return emp[0].equals("10000") ? "Female" : "Male";
            }
        }
        return "Female";
    }

    private BufferedImage loadImage(String path) {
        try { return ImageIO.read(getClass().getResource(path)); }
        catch (Exception e) { System.out.println("Image not found: " + path); return null; }
    }
}