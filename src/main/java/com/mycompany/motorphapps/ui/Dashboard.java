package com.mycompany.motorphapps.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.mycompany.motorphapps.model.Person;
import com.mycompany.motorphapps.service.AttendanceService;
import com.mycompany.motorphapps.service.AuthService;
import com.mycompany.motorphapps.service.EmployeeService;
import com.mycompany.motorphapps.service.PayrollService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Main application dashboard — professional navy blue theme.
 * Home screen statistics are pulled live from services.
 *
 * @author DAYANG GWAPA
 */
public class Dashboard extends JFrame {

    private final Person  loggedInUser;
    private final String  employeeID;
    private final String  role;
    private JPanel        contentPanel;
    private JButton       activeBtn;

    // Services for live stats
    private final EmployeeService   employeeService   = new EmployeeService();
    private final AttendanceService attendanceService = new AttendanceService();
    private final PayrollService    payrollService    = new PayrollService();

    // Palette reused by both charts so they visually match the stat cards
    private static final Color[] SERIES_COLORS = {
            new Color(37, 99, 235),
            new Color(124, 58, 237),
            new Color(22, 163, 74),
            new Color(202, 138, 4),
            new Color(225, 29, 72),
            new Color(14, 165, 233)
    };

    public Dashboard(Person user) {
        this.loggedInUser = user;
        this.employeeID   = user.getEmployeeId();
        this.role         = user.getRole();

        setTitle("MotorPH Management System");
        setSize(1200, 700);
        setMinimumSize(new Dimension(960, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildSidebar();

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIStyle.BACKGROUND);
        add(contentPanel, BorderLayout.CENTER);

        showHome();
        setVisible(true);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private void buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UIStyle.PRIMARY_DARK);
        sidebar.setPreferredSize(new Dimension(220, 0));

        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBackground(UIStyle.PRIMARY_DARK);
        brand.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 30)),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel logoImage = new JLabel();
        try {
            java.net.URL imgURL = getClass().getResource("/images/MTPH.png");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image scaled = icon.getImage().getScaledInstance(72, 50, Image.SCALE_SMOOTH);
                logoImage.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception e) { e.printStackTrace(); }
        logoImage.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoImage.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel logo = new JLabel("MotorPH");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Management System");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tagline.setForeground(new Color(140, 170, 220));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(logoImage);
        brand.add(logo);
        brand.add(tagline);
        sidebar.add(brand, BorderLayout.NORTH);

        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(UIStyle.PRIMARY_DARK);
        nav.setBorder(new EmptyBorder(12, 0, 12, 0));

        AuthService  authService = new AuthService();
        List<String> allowed     = authService.getAllowedMenus(role);

        JButton homeBtn       = buildNavButton("", "Home");
        JButton employeesBtn  = buildNavButton("", "Employees");
        JButton accountsBtn   = buildNavButton("", "Accounts");
        JButton attendanceBtn = buildNavButton("", "Attendance");
        JButton leaveBtn      = buildNavButton("", "Leave");
        JButton payrollBtn    = buildNavButton("", "Payroll");
        JButton payrollHistoryBtn = buildNavButton("", "Payroll History");
        JButton payslipBtn    = buildNavButton("", "Payslip");
        JButton reportsBtn    = buildNavButton("", "Reports");
        JButton profileBtn    = buildNavButton("", "Profile");

        nav.add(homeBtn);
        if (allowed.contains("employees"))  { nav.add(Box.createVerticalStrut(2)); nav.add(employeesBtn); }
        if (allowed.contains("accounts"))   { nav.add(Box.createVerticalStrut(2)); nav.add(accountsBtn); }
        if (allowed.contains("attendance")) { nav.add(Box.createVerticalStrut(2)); nav.add(attendanceBtn); }
        if (allowed.contains("leave"))      { nav.add(Box.createVerticalStrut(2)); nav.add(leaveBtn); }
        if (allowed.contains("payroll"))    { nav.add(Box.createVerticalStrut(2)); nav.add(payrollBtn); }
        if (allowed.contains("payroll"))    { nav.add(Box.createVerticalStrut(2)); nav.add(payrollHistoryBtn); }
        if (allowed.contains("payslip"))    { nav.add(Box.createVerticalStrut(2)); nav.add(payslipBtn); }
        if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("hr")) {
            nav.add(Box.createVerticalStrut(2));
            nav.add(reportsBtn);
        }
        nav.add(Box.createVerticalStrut(2));
        nav.add(profileBtn);

        sidebar.add(nav, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(UIStyle.PRIMARY_DARK);
        bottom.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 30)),
                new EmptyBorder(14, 18, 16, 18)));

        JLabel userName = new JLabel(loggedInUser.getDisplayName(true));
        userName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userName.setForeground(Color.WHITE);
        userName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userRole = new JLabel(role.toUpperCase() + "  •  ID " + employeeID);
        userRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        userRole.setForeground(new Color(140, 170, 220));
        userRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton logoutBtn = buildNavButton("", "Logout");
        logoutBtn.setForeground(new Color(255, 160, 150));

        bottom.add(userName);
        bottom.add(Box.createVerticalStrut(2));
        bottom.add(userRole);
        bottom.add(Box.createVerticalStrut(12));
        bottom.add(logoutBtn);

        sidebar.add(bottom, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.WEST);

        setActive(homeBtn);
        homeBtn.addActionListener(e       -> { setActive(homeBtn);       showHome(); });
        employeesBtn.addActionListener(e  -> { setActive(employeesBtn);  swap(new AllEmployeesPanel(role)); });
        accountsBtn.addActionListener(e   -> { setActive(accountsBtn);   swap(new AccountManagementPanel()); });
        attendanceBtn.addActionListener(e -> { setActive(attendanceBtn); swap(new AttendancePanel(employeeID, role)); });
        leaveBtn.addActionListener(e      -> { setActive(leaveBtn);      swap(new LeavePanel(role, employeeID)); });
        payrollBtn.addActionListener(e    -> { setActive(payrollBtn);    swap(new PayrollPanel(employeeID, role)); });
        payrollHistoryBtn.addActionListener(e -> { setActive(payrollHistoryBtn); swap(new PayrollHistoryPanel()); });
        payslipBtn.addActionListener(e    -> { setActive(payslipBtn);    swap(new PayslipPanel(employeeID, role)); });
        reportsBtn.addActionListener(e    -> { setActive(reportsBtn);    swap(new ReportPanel(employeeID, role)); });
        profileBtn.addActionListener(e    -> { setActive(profileBtn);    swap(new EmployeeInfoPanel(employeeID, role)); });
        logoutBtn.addActionListener(e     -> { dispose(); new LoginScreen(); });
    }

    private JButton buildNavButton(String emoji, String label) {
        JButton btn = new JButton(emoji + "  " + label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (this == activeBtn) {
                    g2.setColor(UIStyle.PRIMARY);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(UIStyle.ACCENT);
                    g2.fillRect(0, 0, 3, getHeight());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 18));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(190, 215, 255));
        btn.setBackground(UIStyle.PRIMARY_DARK);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(11, 18, 11, 18));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    private void setActive(JButton btn) {
        activeBtn = btn;
        Container sidebar = btn.getParent();
        if (sidebar != null) sidebar.repaint();
    }

    // ── Home screen ───────────────────────────────────────────────────────────
    private void showHome() {
        DashboardStats stats = loadStats();

        // Outer scroll so content isn't clipped on smaller screens
        JPanel home = new JPanel();
        home.setLayout(new BoxLayout(home, BoxLayout.Y_AXIS));
        home.setBackground(UIStyle.BACKGROUND);
        home.setBorder(new EmptyBorder(24, 28, 24, 28));

        // ── Header ──
        home.add(buildHeaderRow());
        home.add(Box.createVerticalStrut(20));

        // ── 4 stat cards ──
        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        cards.add(buildStatCard("👥", "Total Employees",
                String.valueOf(stats.totalEmployees), "Registered in system", UIStyle.PRIMARY));
        cards.add(buildStatCard("📅", "Today's Attendance",
                stats.attendanceToday + " present",
                "As of " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d")),
                UIStyle.SUCCESS));
        cards.add(buildStatCard("📋", "Payroll Records",
                String.valueOf(stats.totalPayrollRecords),
                "Total processed entries", new Color(120, 60, 200)));
        cards.add(buildStatCard("⏱", "Attendance Logs",
                String.valueOf(stats.totalAttendanceLogs),
                "All-time log entries", new Color(180, 100, 20)));
        home.add(cards);
        home.add(Box.createVerticalStrut(18));

        // ── Charts row ──
        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 14, 0));
        chartsRow.setOpaque(false);
        chartsRow.setPreferredSize(new Dimension(1000, 320));
        chartsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        chartsRow.add(buildDeptBarChart(stats.deptData));
        chartsRow.add(buildNetPayLineChart(stats.netPayData));
        home.add(chartsRow);
        home.add(Box.createVerticalStrut(18));

        // ── Bottom row: quick summary + account card ──
        JPanel bottomRow = new JPanel(new BorderLayout(14, 0));
        bottomRow.setOpaque(false);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        bottomRow.add(buildAccountCard(), BorderLayout.CENTER);
        bottomRow.add(buildActivityPanel(stats), BorderLayout.EAST);
        home.add(bottomRow);

        JScrollPane scroll = new JScrollPane(home);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setBackground(UIStyle.BACKGROUND);

        swap(scroll);
    }

    // ── Header row ────────────────────────────────────────────────────────────
    private JPanel buildHeaderRow() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome back, " + loggedInUser.getDisplayName(true) + "!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcome.setForeground(UIStyle.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Here's your live system overview for today.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(UIStyle.TEXT_SECONDARY);
        subtitle.setBorder(new EmptyBorder(3, 0, 0, 0));

        left.add(welcome);
        left.add(subtitle);

        JPanel clockPanel = new JPanel();
        clockPanel.setBackground(UIStyle.SURFACE);
        clockPanel.setBorder(new CompoundBorder(
                new LineBorder(UIStyle.BORDER, 1, true),
                new EmptyBorder(10, 18, 10, 18)));
        clockPanel.setLayout(new BoxLayout(clockPanel, BoxLayout.Y_AXIS));

        JLabel dateLabel = new JLabel(
                LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(UIStyle.TEXT_SECONDARY);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        timeLabel.setForeground(UIStyle.PRIMARY);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Timer clock = new Timer(1000, e ->
                timeLabel.setText(LocalTime.now().format(
                        DateTimeFormatter.ofPattern("hh:mm:ss a"))));
        clock.start();
        timeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));

        clockPanel.add(dateLabel);
        clockPanel.add(Box.createVerticalStrut(4));
        clockPanel.add(timeLabel);

        row.add(left,       BorderLayout.WEST);
        row.add(clockPanel, BorderLayout.EAST);
        return row;
    }

    // ── Stat card ─────────────────────────────────────────────────────────────
    private JPanel buildStatCard(String emoji, String title,
                              String value, String sub, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(UIStyle.SURFACE);
        card.setBorder(new CompoundBorder(
                new LineBorder(UIStyle.BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)));
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(0, 3));
        card.add(accentBar, BorderLayout.NORTH);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        // Fixed-size emoji box, drawn manually so it never gets clipped on resize/maximize
        JPanel emojiBox = new JPanel() {
            @Override
            public Dimension getPreferredSize() { return new Dimension(34, 34); }
            @Override
            public Dimension getMinimumSize() { return new Dimension(34, 34); }
            @Override
            public Dimension getMaximumSize() { return new Dimension(34, 34); }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                // Critical: remove any inherited clip so the emoji glyph can't be cropped
                g2.setClip(null);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

                Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 20);
                g2.setFont(emojiFont);

                java.awt.font.FontRenderContext frc = g2.getFontRenderContext();
                java.awt.font.GlyphVector gv = emojiFont.createGlyphVector(frc, emoji);
                Rectangle2D visualBounds = gv.getVisualBounds();

                // Center based on the glyph's true visual bounding box, not FontMetrics
                double x = (getWidth() - visualBounds.getWidth()) / 2 - visualBounds.getX();
                double y = (getHeight() - visualBounds.getHeight()) / 2 - visualBounds.getY();

                g2.drawGlyphVector(gv, (float) x, (float) y);
                g2.dispose();
            }
        };
        emojiBox.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(UIStyle.TEXT_SECONDARY);
        top.add(emojiBox, BorderLayout.WEST);
        top.add(titleLbl, BorderLayout.EAST);
        card.add(top, BorderLayout.CENTER);
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLbl.setForeground(accent);
        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(UIStyle.FONT_SMALL);
        subLbl.setForeground(UIStyle.TEXT_SECONDARY);
        bottom.add(valueLbl);
        bottom.add(subLbl);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    // ── Department Bar Chart (JFreeChart) ─────────────────────────────────────
    private JPanel buildDeptBarChart(List<String[]> data) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setBackground(UIStyle.SURFACE);
        wrapper.setBorder(new CompoundBorder(
                new LineBorder(UIStyle.BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)));

        JLabel title = new JLabel("Employees by Department");
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(UIStyle.PRIMARY);
        wrapper.add(title, BorderLayout.NORTH);

        if (data == null || data.isEmpty()) {
            JLabel empty = new JLabel("No data available");
            empty.setForeground(UIStyle.TEXT_SECONDARY);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            wrapper.add(empty, BorderLayout.CENTER);
            return wrapper;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String[] row : data) {
            dataset.addValue(Integer.parseInt(row[1]), "Employees", row[0]);
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                null, null, null, dataset,
                PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(UIStyle.SURFACE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.setDomainGridlinesVisible(false);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        domainAxis.setTickLabelPaint(UIStyle.TEXT_SECONDARY);
        domainAxis.setAxisLineVisible(false);
        domainAxis.setTickMarksVisible(false);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        rangeAxis.setTickLabelPaint(UIStyle.TEXT_SECONDARY);
        rangeAxis.setAxisLineVisible(false);

        // Color each department bar differently, echoing the original palette
        BarRenderer renderer = new BarRenderer() {
            @Override
            public Paint getItemPaint(int row, int column) {
                return SERIES_COLORS[column % SERIES_COLORS.length];
            }
        };
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setMaximumBarWidth(0.12);
        renderer.setItemMargin(0.0);
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 10));
        plot.setRenderer(renderer);

        barChart.setBackgroundPaint(UIStyle.SURFACE);
        barChart.removeLegend();

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setBackground(UIStyle.SURFACE);
        chartPanel.setPreferredSize(null);
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setBorder(null);

        wrapper.add(chartPanel, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Net Pay Line Chart (JFreeChart) ────────────────────────────────────────
    private JPanel buildNetPayLineChart(List<String[]> data) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setBackground(UIStyle.SURFACE);
        wrapper.setBorder(new CompoundBorder(
                new LineBorder(UIStyle.BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)));

        JLabel title = new JLabel("Total Net Pay by Period");
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(UIStyle.PRIMARY);
        wrapper.add(title, BorderLayout.NORTH);

        if (data == null || data.isEmpty()) {
            JLabel empty = new JLabel("No data available");
            empty.setForeground(UIStyle.TEXT_SECONDARY);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            wrapper.add(empty, BorderLayout.CENTER);
            return wrapper;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String[] row : data) {
            dataset.addValue(Double.parseDouble(row[1]), "Net Pay", row[0]);
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                null, null, null, dataset,
                PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(UIStyle.SURFACE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.setDomainGridlinesVisible(false);

        CategoryAxis domainAxis = plot.getDomainAxis();
        // Diagonal labels so adjacent periods don't collide, same intent as before
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4));
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 9));
        domainAxis.setTickLabelPaint(UIStyle.TEXT_SECONDARY);
        domainAxis.setAxisLineVisible(false);
        domainAxis.setTickMarksVisible(false);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new java.text.DecimalFormat("#,##0"));
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        rangeAxis.setTickLabelPaint(UIStyle.TEXT_SECONDARY);
        rangeAxis.setAxisLineVisible(false);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, UIStyle.PRIMARY);
        renderer.setSeriesStroke(0, new BasicStroke(3f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-5, -5, 10, 10));
        renderer.setUseFillPaint(true);
        renderer.setSeriesFillPaint(0, Color.WHITE);
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 9));
        renderer.setDefaultItemLabelPaint(UIStyle.TEXT_PRIMARY);
        plot.setRenderer(renderer);

        lineChart.setBackgroundPaint(UIStyle.SURFACE);
        lineChart.removeLegend();

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setBackground(UIStyle.SURFACE);
        chartPanel.setPreferredSize(null);
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setBorder(null);

        wrapper.add(chartPanel, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Right quick-summary panel ─────────────────────────────────────────────
    private JPanel buildActivityPanel(DashboardStats stats) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBackground(UIStyle.SURFACE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(UIStyle.BORDER, 1, true),
                new EmptyBorder(14, 14, 14, 14)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Quick Summary");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 12));
        heading.setForeground(UIStyle.PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(4));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(UIStyle.BORDER);
        panel.add(sep);
        panel.add(Box.createVerticalStrut(10));

        addQuickRow(panel, "Today",
                LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM d")));
        addQuickRow(panel, "Role",    role.toUpperCase());
        addQuickRow(panel, "Emp ID",  employeeID);
        addQuickRow(panel, "Staff",   stats.totalEmployees + " employees");
        addQuickRow(panel, "My Pay",  stats.myPayrollRecords + " records");
        addQuickRow(panel, "My Att.", stats.myAttendanceLogs + " logs");

        panel.add(Box.createVerticalGlue());
        JLabel updated = new JLabel("Live — updated on open");
        updated.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        updated.setForeground(UIStyle.TEXT_SECONDARY);
        updated.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(updated);

        return panel;
    }

    private void addQuickRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel left = new JLabel(label);
        left.setFont(UIStyle.FONT_SMALL);
        left.setForeground(UIStyle.TEXT_SECONDARY);
        JLabel right = new JLabel(value);
        right.setFont(new Font("Segoe UI", Font.BOLD, 11));
        right.setForeground(UIStyle.TEXT_PRIMARY);
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(left,  BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        parent.add(row);
        parent.add(Box.createVerticalStrut(6));
    }

    // ── Account info card ─────────────────────────────────────────────────────
    private JPanel buildAccountCard() {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(UIStyle.SURFACE);
        card.setBorder(new CompoundBorder(
                new LineBorder(UIStyle.BORDER, 1, true),
                new EmptyBorder(14, 18, 14, 18)));

        JLabel title = new JLabel("Your Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(UIStyle.PRIMARY);
        title.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIStyle.BORDER),
                new EmptyBorder(0, 0, 8, 0)));
        card.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 6, 10, 0));
        grid.setOpaque(false);
        addInfoCell(grid, "Full Name",   loggedInUser.getDisplayName(true));
        addInfoCell(grid, "Employee ID", employeeID);
        addInfoCell(grid, "Role",        role.toUpperCase());
        addInfoCell(grid, "Login Date",
                LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
        addInfoCell(grid, "Login Time",
                LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
        addInfoCell(grid, "System", "MotorPH v1.0");
        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private void addInfoCell(JPanel grid, String label, String value) {
        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIStyle.FONT_SMALL);
        lbl.setForeground(UIStyle.TEXT_SECONDARY);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 12));
        val.setForeground(UIStyle.TEXT_PRIMARY);
        cell.add(lbl);
        cell.add(val);
        grid.add(cell);
    }

    // ── Live stats loader ─────────────────────────────────────────────────────
    private DashboardStats loadStats() {
        DashboardStats s = new DashboardStats();

        List<String[]> employees = employeeService.getAllEmployees();
        s.totalEmployees = employees.size();

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<String[]> allAttendance = attendanceService.getAllAttendance();
        java.util.Set<String> presentToday = new java.util.HashSet<>();
        int myLogs = 0;
        for (String[] row : allAttendance) {
            if (row.length >= 3) {
                if (row[2].trim().equals(today)) presentToday.add(row[0].trim());
                if (row[0].trim().equals(employeeID)) myLogs++;
            }
        }
        s.attendanceToday     = presentToday.size();
        s.totalAttendanceLogs = allAttendance.size();
        s.myAttendanceLogs    = myLogs;

        List<String[]> payroll = payrollService.getAllPayroll();
        s.totalPayrollRecords = payroll.size();
        int myPay = 0;
        for (String[] row : payroll) {
            if (row.length >= 1 && row[0].trim().equals(employeeID)) myPay++;
        }
        s.myPayrollRecords = myPay;

        // Chart data
        s.deptData   = payrollService.getEmployeeCountByDepartment();
        s.netPayData = payrollService.getNetPayByPeriod();

        return s;
    }

    private static class DashboardStats {
        int totalEmployees       = 0;
        int attendanceToday      = 0;
        int totalAttendanceLogs  = 0;
        int totalPayrollRecords  = 0;
        int myAttendanceLogs     = 0;
        int myPayrollRecords     = 0;
        List<String[]> deptData;
        List<String[]> netPayData;
    }

    // ── Swap content ──────────────────────────────────────────────────────────
    private void swap(JComponent panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}