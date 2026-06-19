// AlertPanel.java
// Team Member: Sejal Singh (240221951)
// Swing Alert Panel - shows hospitals with critically low resources

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class AlertPanel {

    private List<Hospital> hospitals;

    public AlertPanel(List<Hospital> hospitals) {
        this.hospitals = hospitals;
    }

    public JPanel getPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Main.BG_DARK);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Main.BG_CARD);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));
        JLabel title = new JLabel("🔔  Resource Alerts");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(Main.TEXT_PRIMARY);
        JLabel sub = new JLabel("Hospitals with critically low resources (below threshold)");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sub.setForeground(Main.TEXT_MUTED);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title); titleBox.add(Box.createVerticalStrut(4)); titleBox.add(sub);
        header.add(titleBox, BorderLayout.WEST);

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Main.BG_DARK);
        content.setBorder(new EmptyBorder(16, 16, 16, 16));

        boolean anyAlert = false;
        for (Hospital h : hospitals) {
            boolean icuLow  = h.getResource("ICU Beds") <= 3;
            boolean ventLow = h.getResource("Ventilators") <= 2;
            boolean oxyLow  = h.getResource("Oxygen Cylinders") <= 5;
            boolean bloodLow = h.getResource("Blood Units") <= 5;

            if (icuLow || ventLow || oxyLow || bloodLow) {
                anyAlert = true;
                content.add(buildAlertCard(h, icuLow, ventLow, oxyLow, bloodLow));
                content.add(Box.createVerticalStrut(10));
            }
        }

        if (!anyAlert) {
            JLabel ok = new JLabel("✅  All hospitals have sufficient resources. No alerts!");
            ok.setFont(new Font("SansSerif", Font.PLAIN, 14));
            ok.setForeground(new Color(63, 185, 80));
            ok.setBorder(new EmptyBorder(20, 20, 20, 20));
            content.add(ok);
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.getViewport().setBackground(Main.BG_DARK);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildAlertCard(Hospital h, boolean icuLow, boolean ventLow,
                                  boolean oxyLow, boolean bloodLow) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(new Color(60, 20, 20));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Main.RED_PRIMARY, 1),
                new EmptyBorder(14, 16, 14, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel name = new JLabel("⚠  " + h.getName() + " — " + h.getLocation());
        name.setFont(new Font("Serif", Font.BOLD, 15));
        name.setForeground(new Color(255, 107, 107));

        JPanel alerts = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        alerts.setOpaque(false);

        if (icuLow)  alerts.add(alertChip("ICU Beds: "   + h.getResource("ICU Beds")));
        if (ventLow) alerts.add(alertChip("Ventilators: " + h.getResource("Ventilators")));
        if (oxyLow)  alerts.add(alertChip("Oxygen: "     + h.getResource("Oxygen Cylinders")));
        if (bloodLow)alerts.add(alertChip("Blood Units: " + h.getResource("Blood Units")));

        card.add(name,   BorderLayout.NORTH);
        card.add(alerts, BorderLayout.CENTER);
        return card;
    }

    private JLabel alertChip(String text) {
        JLabel lbl = new JLabel("  " + text + "  ");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);
        lbl.setBackground(Main.RED_PRIMARY);
        lbl.setBorder(new EmptyBorder(3, 8, 3, 8));
        return lbl;
    }
}