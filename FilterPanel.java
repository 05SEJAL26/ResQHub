// FilterPanel.java
// Team Member: Krishni Rastogi (24022510)
// Swing Filter Panel - filter hospitals by city

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class FilterPanel {

    private List<Hospital> hospitals;

    public FilterPanel(List<Hospital> hospitals) {
        this.hospitals = hospitals;
    }

    public JPanel getPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Main.BG_DARK);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Main.BG_CARD);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));
        JLabel title = new JLabel("🗂  Filter by City");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(Main.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        // City dropdown
        List<String> cities = hospitals.stream()
                .map(Hospital::getLocation).distinct()
                .sorted().collect(Collectors.toList());
        cities.add(0, "All Cities");
        JComboBox<String> cityCombo = Main.styledCombo(cities.toArray(new String[0]));
        cityCombo.setPreferredSize(new Dimension(220, 36));

        JButton filterBtn = new JButton("Filter");
        Main.stylePrimaryBtn(filterBtn, Main.RED_PRIMARY, Main.RED_DARK);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterBar.setBackground(Main.BG_DARK);
        filterBar.setBorder(new EmptyBorder(8, 16, 0, 16));
        filterBar.add(Main.mutedLabel("Select City:"));
        filterBar.add(cityCombo);
        filterBar.add(filterBtn);

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

        Runnable doFilter = () -> {
            model.setRowCount(0);
            String city = (String) cityCombo.getSelectedItem();
            hospitals.stream()
                    .filter(h -> "All Cities".equals(city) || h.getLocation().equals(city))
                    .forEach(h -> model.addRow(new Object[]{
                            h.getName(), h.getLocation(),
                            h.getResource("ICU Beds"), h.getResource("Ventilators"),
                            h.getResource("Oxygen Cylinders"), h.getResource("Blood Units"),
                            h.getResource("Ambulances")
                    }));
        };

        doFilter.run();
        filterBtn.addActionListener(e -> doFilter.run());

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Main.BG_DARK);
        center.add(filterBar, BorderLayout.NORTH);
        center.add(scroll,    BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        return root;
    }
}