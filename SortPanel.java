// SortPanel.java
// Team Member: Krishni Rastogi (24022510)
// Swing Sort Panel - sort hospitals by resource availability

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SortPanel {

    private List<Hospital> hospitals;

    public SortPanel(List<Hospital> hospitals) {
        this.hospitals = hospitals;
    }

    public JPanel getPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Main.BG_DARK);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Main.BG_CARD);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));
        JLabel title = new JLabel("↕  Sort by Resource");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(Main.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        String[] resources = {"ICU Beds", "Ventilators", "Oxygen Cylinders",
                "Blood Units", "Ambulances", "Trauma Specialists"};
        JComboBox<String> resourceCombo = Main.styledCombo(resources);
        resourceCombo.setPreferredSize(new Dimension(200, 36));

        String[] orders = {"High to Low", "Low to High"};
        JComboBox<String> orderCombo = Main.styledCombo(orders);
        orderCombo.setPreferredSize(new Dimension(140, 36));

        JButton sortBtn = new JButton("Sort");
        Main.stylePrimaryBtn(sortBtn, Main.RED_PRIMARY, Main.RED_DARK);

        JPanel sortBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        sortBar.setBackground(Main.BG_DARK);
        sortBar.setBorder(new EmptyBorder(8, 16, 0, 16));
        sortBar.add(Main.mutedLabel("Sort by:"));
        sortBar.add(resourceCombo);
        sortBar.add(Main.mutedLabel("Order:"));
        sortBar.add(orderCombo);
        sortBar.add(sortBtn);

        String[] cols = {"Hospital", "City", "ICU Beds", "Ventilators",
                "Oxygen", "Blood Units", "Ambulances"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        DashboardPanel.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Main.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(Main.BORDER_CLR));

        Runnable doSort = () -> {
            model.setRowCount(0);
            String res   = (String) resourceCombo.getSelectedItem();
            boolean desc = "High to Low".equals(orderCombo.getSelectedItem());

            hospitals.stream()
                    .sorted((a, b) -> desc
                            ? b.getResource(res) - a.getResource(res)
                            : a.getResource(res) - b.getResource(res))
                    .forEach(h -> model.addRow(new Object[]{
                            h.getName(), h.getLocation(),
                            h.getResource("ICU Beds"), h.getResource("Ventilators"),
                            h.getResource("Oxygen Cylinders"), h.getResource("Blood Units"),
                            h.getResource("Ambulances")
                    }));
        };

        doSort.run();
        sortBtn.addActionListener(e -> doSort.run());

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Main.BG_DARK);
        center.add(sortBar, BorderLayout.NORTH);
        center.add(scroll,  BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        return root;
    }
}