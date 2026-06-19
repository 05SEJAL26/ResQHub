// DashboardPanel.java
// Team Member: Anvesha Singh (24022980)
// Swing Dashboard with stat cards and hospital table

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel {

    private List<Hospital> hospitals;
    private Role role;

    public DashboardPanel(List<Hospital> hospitals, Role role) {
        this.hospitals = hospitals;
        this.role      = role;
    }

    public JPanel getPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Main.BG_DARK);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Main.BG_CARD);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));
        JLabel title = new JLabel("Dashboard - All Hospitals Overview");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(Main.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        // Stats
        int totalICU   = hospitals.stream().mapToInt(h -> h.getResource("ICU Beds")).sum();
        int totalVent  = hospitals.stream().mapToInt(h -> h.getResource("Ventilators")).sum();
        int totalOxy   = hospitals.stream().mapToInt(h -> h.getResource("Oxygen Cylinders")).sum();
        int totalBlood = hospitals.stream().mapToInt(h -> h.getResource("Blood Units")).sum();
        int totalAmb   = hospitals.stream().mapToInt(h -> h.getResource("Ambulances")).sum();
        long critical  = hospitals.stream().filter(h ->
                h.getResource("ICU Beds") <= 3 || h.getResource("Ventilators") <= 2).count();

        JPanel cards = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        cards.setBackground(Main.BG_DARK);
        cards.add(statCard("ICU Beds",    String.valueOf(totalICU),   new Color(21, 101, 192)));
        cards.add(statCard("Ventilators", String.valueOf(totalVent),  new Color(46, 125, 50)));
        cards.add(statCard("Oxygen",      String.valueOf(totalOxy),   new Color(230, 81, 0)));
        cards.add(statCard("Blood Units", String.valueOf(totalBlood), new Color(136, 14, 79)));
        cards.add(statCard("Ambulances",  String.valueOf(totalAmb),   new Color(0, 105, 92)));
        cards.add(statCard("Critical",    String.valueOf(critical),   Main.RED_PRIMARY));

        // Table
        String[] cols = {"Hospital", "City", "ICU Beds", "Ventilators", "Oxygen", "Blood Units", "Ambulances", "Status"};
        Object[][] data = new Object[hospitals.size()][8];
        for (int i = 0; i < hospitals.size(); i++) {
            Hospital h = hospitals.get(i);
            boolean crit = h.getResource("ICU Beds") <= 3 || h.getResource("Ventilators") <= 2;
            data[i] = new Object[]{
                    h.getName(), h.getLocation(),
                    h.getResource("ICU Beds"), h.getResource("Ventilators"),
                    h.getResource("Oxygen Cylinders"), h.getResource("Blood Units"),
                    h.getResource("Ambulances"), crit ? "⚠ Critical" : "✓ OK"
            };
        }

        JTable table = new JTable(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(Main.BG_DARK);
        scroll.getViewport().setBackground(Main.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(Main.BORDER_CLR));

        JLabel summary = new JLabel("  Total Hospitals: " + hospitals.size() +
                "   |   Critical Alerts: " + critical);
        summary.setFont(new Font("SansSerif", Font.PLAIN, 12));
        summary.setForeground(critical > 0 ? new Color(255, 107, 107) : new Color(63, 185, 80));
        summary.setBorder(new EmptyBorder(8, 16, 8, 16));
        summary.setOpaque(true);
        summary.setBackground(Main.BG_DARK);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Main.BG_DARK);
        center.add(cards,   BorderLayout.NORTH);
        center.add(scroll,  BorderLayout.CENTER);
        center.add(summary, BorderLayout.SOUTH);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private JPanel statCard(String label, String value, Color color) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Main.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, 5, getHeight(), 4, 4);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(120, 65));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Serif", Font.BOLD, 22));
        val.setForeground(color);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lbl.setForeground(Main.TEXT_MUTED);

        card.add(val);
        card.add(lbl);
        return card;
    }

    static void styleTable(JTable table) {
        table.setBackground(Main.BG_CARD);
        table.setForeground(Main.TEXT_PRIMARY);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(32);
        table.setGridColor(Main.BORDER_CLR);
        table.setSelectionBackground(new Color(48, 54, 61));
        table.setSelectionForeground(Main.TEXT_PRIMARY);
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(33, 38, 45));
        header.setForeground(Main.TEXT_PRIMARY);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBorder(BorderFactory.createLineBorder(Main.BORDER_CLR));
    }
}