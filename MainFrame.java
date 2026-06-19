// MainFrame.java
// Team Member: Sejal Singh (240221951)
// Main window with role-based Swing navigation

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainFrame extends JFrame {

    private Role role;
    private List<Hospital> hospitals;
    private JPanel contentArea;

    public MainFrame(Role role) {
        this.role      = role;
        this.hospitals = DatabaseHelper.loadHospitals();

        setTitle("ResQHub - Emergency Resource System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Main.BG_DARK);
        add(contentArea, BorderLayout.CENTER);

        showPanel(new DashboardPanel(hospitals, role).getPanel());
        setVisible(true);
    }

    public void showPanel(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        bar.setBackground(Main.RED_PRIMARY);

        JLabel lbl = new JLabel("ResQHub   |   Logged in as: " + role);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(Color.WHITE);
        bar.add(lbl);

        return bar;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Main.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(16, 8, 16, 8));

        // Role badge
        JLabel roleLbl = new JLabel(role.toString());
        roleLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        String roleColor = role == Role.ADMIN ? "#FF6B6B" : "#74B9FF";
        roleLbl.setForeground(Color.decode(roleColor));
        roleLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        roleLbl.setBorder(new EmptyBorder(4, 8, 4, 8));
        roleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(roleLbl);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(new JSeparator());
        sidebar.add(Box.createVerticalStrut(8));

        // Common buttons for both roles
        sidebar.add(sideBtn("🏠  Dashboard", e ->
                showPanel(new DashboardPanel(hospitals, role).getPanel())));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(sideBtn("🏥  Hospitals", e ->
                showPanel(new HospitalPanel(hospitals, role, this).getPanel())));
        sidebar.add(Box.createVerticalStrut(4));

        // ADMIN only buttons
        if (role == Role.ADMIN) {
            sidebar.add(sideBtn("➕  Add Hospital", e ->
                    showPanel(new AddHospitalPanel(hospitals, this).getPanel())));
            sidebar.add(Box.createVerticalStrut(4));
            sidebar.add(sideBtn("🔔  Alerts", e ->
                    showPanel(new AlertPanel(hospitals).getPanel())));
            sidebar.add(Box.createVerticalStrut(4));
            sidebar.add(sideBtn("📋  Manage Requests", e ->
                    showPanel(new AdminRequestPanel().getPanel())));
            sidebar.add(Box.createVerticalStrut(4));
        }

        // USER only buttons
        if (role == Role.USER) {
            sidebar.add(sideBtn("🔍  Search", e ->
                    showPanel(new SearchPanel(hospitals).getPanel())));
            sidebar.add(Box.createVerticalStrut(4));
            sidebar.add(sideBtn("🗂  Filter by City", e ->
                    showPanel(new FilterPanel(hospitals).getPanel())));
            sidebar.add(Box.createVerticalStrut(4));
            sidebar.add(sideBtn("↕  Sort by Resource", e ->
                    showPanel(new SortPanel(hospitals).getPanel())));
            sidebar.add(Box.createVerticalStrut(4));
            sidebar.add(sideBtn("🆘  Raise Request", e ->
                    showPanel(new RequestPanel().getPanel())));
            sidebar.add(Box.createVerticalStrut(4));
            sidebar.add(sideBtn("📤  Export Report", e ->
                    ExportHelper.exportReport(hospitals)));
            sidebar.add(Box.createVerticalStrut(4));
        }

        // Spacer + Logout
        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = sideBtn("🚪  Logout", e -> {
            dispose();
            new Main().showLogin();
        });
        logoutBtn.setForeground(new Color(255, 107, 107));
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private JButton sideBtn(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(52, 58, 64));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(9, 12, 9, 12));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(Main.RED_PRIMARY); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(52, 58, 64)); }
        });
        btn.addActionListener(action);
        return btn;
    }
}