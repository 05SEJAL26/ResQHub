// SearchPanel.java
// Team Member: Krishni Rastogi (24022510)
// Swing Search Panel

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class SearchPanel {

    private List<Hospital> hospitals;

    public SearchPanel(List<Hospital> hospitals) {
        this.hospitals = hospitals;
    }

    public JPanel getPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Main.BG_DARK);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Main.BG_CARD);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));
        JLabel title = new JLabel("🔍  Search Hospitals");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(Main.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        searchBar.setBackground(Main.BG_DARK);
        searchBar.setBorder(new EmptyBorder(8, 16, 0, 16));

        JTextField searchField = Main.styledField("Search by hospital name or city...");
        searchField.setPreferredSize(new Dimension(300, 36));

        String[] resources = {"Any Resource", "ICU Beds", "Ventilators",
                "Oxygen Cylinders", "Blood Units", "Ambulances", "Trauma Specialists"};
        JComboBox<String> resourceFilter = Main.styledCombo(resources);
        resourceFilter.setPreferredSize(new Dimension(180, 36));

        JButton searchBtn = new JButton("Search");
        Main.stylePrimaryBtn(searchBtn, Main.RED_PRIMARY, Main.RED_DARK);

        searchBar.add(new JLabel("  ") {{ setForeground(Main.TEXT_MUTED); }});
        searchBar.add(searchField);
        searchBar.add(resourceFilter);
        searchBar.add(searchBtn);

        // Table
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

        Runnable doSearch = () -> {
            model.setRowCount(0);
            String query = searchField.getText().trim().toLowerCase();
            String res   = (String) resourceFilter.getSelectedItem();

            hospitals.stream()
                    .filter(h -> query.isEmpty() ||
                            h.getName().toLowerCase().contains(query) ||
                            h.getLocation().toLowerCase().contains(query))
                    .filter(h -> "Any Resource".equals(res) || h.getResource(res) > 0)
                    .forEach(h -> model.addRow(new Object[]{
                            h.getName(), h.getLocation(),
                            h.getResource("ICU Beds"), h.getResource("Ventilators"),
                            h.getResource("Oxygen Cylinders"), h.getResource("Blood Units"),
                            h.getResource("Ambulances")
                    }));
        };

        doSearch.run();
        searchBtn.addActionListener(e -> doSearch.run());
        searchField.addActionListener(e -> doSearch.run());

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Main.BG_DARK);
        center.add(searchBar, BorderLayout.NORTH);
        center.add(scroll,    BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        return root;
    }
}