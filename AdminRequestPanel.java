// AdminRequestPanel.java
// Team Member: Sejal Singh (240221951)
// Swing Admin panel - view requests, assign hospitals filtered by city AND resource

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class AdminRequestPanel {

    public JPanel getPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Main.BG_DARK);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Main.BG_CARD);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));

        JLabel title = new JLabel("📋  Manage Patient Requests");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(Main.TEXT_PRIMARY);

        JLabel sub = new JLabel("Hospitals filtered by patient's city and required resource");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sub.setForeground(Main.TEXT_MUTED);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(sub);

        JButton refreshBtn = new JButton("🔄  Refresh");
        Main.styleSecondaryBtn(refreshBtn);

        header.add(titleBox, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Main.BG_DARK);
        content.setBorder(new EmptyBorder(16, 16, 16, 16));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBackground(Main.BG_DARK);
        scroll.getViewport().setBackground(Main.BG_DARK);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        Runnable load = () -> {
            content.removeAll();
            List<ResourceRequest> requests = DatabaseHelper.loadAllRequests();
            if (requests.isEmpty()) {
                JLabel empty = new JLabel("No requests yet.");
                empty.setForeground(Main.TEXT_MUTED);
                empty.setFont(new Font("SansSerif", Font.PLAIN, 13));
                content.add(empty);
            } else {
                for (ResourceRequest req : requests) {
                    content.add(buildCard(req));
                    content.add(Box.createVerticalStrut(12));
                }
            }
            content.revalidate();
            content.repaint();
        };

        load.run();
        refreshBtn.addActionListener(e -> load.run());

        root.add(header, BorderLayout.NORTH);
        root.add(scroll,  BorderLayout.CENTER);
        return root;
    }

    private JPanel buildCard(ResourceRequest req) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Main.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Main.BORDER_CLR, 1),
                new EmptyBorder(14, 16, 14, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Top row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel nameAndId = new JLabel("#" + req.getId() + "  " + req.getRaisedBy());
        nameAndId.setFont(new Font("Serif", Font.BOLD, 15));
        nameAndId.setForeground(Main.TEXT_PRIMARY);

        JLabel badge = statusBadge(req.getStatus());
        topRow.add(nameAndId, BorderLayout.WEST);
        topRow.add(badge, BorderLayout.EAST);

        // Info panel
        JPanel info = new JPanel(new GridLayout(0, 2, 12, 4));
        info.setOpaque(false);
        addInfoRow(info, "📍 City",     req.getCity());
        addInfoRow(info, "🏥 Resource", req.getResourceNeeded());
        addInfoRow(info, "🔢 Quantity", String.valueOf(req.getQuantityNeeded()));
        addInfoRow(info, "📝 Reason",   req.getReason());
        addInfoRow(info, "🕐 Raised",   req.getRaisedAt());

        // Always show assigned hospital row
        addInfoRow(info, "🏨 Assigned Hospital",
                req.getAssignedHospital().equals("-")
                        ? "Not yet assigned"
                        : req.getAssignedHospital());

        // Action row
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionRow.setOpaque(false);

        if (req.getStatus().equals("Pending")) {

            String resourceNeeded = req.getResourceNeeded();
            String patientCity    = req.getCity();

            List<Hospital> allHospitals = DatabaseHelper.loadHospitals();

            // Same city hospitals with resource available — shown first
            List<Hospital> sameCity = allHospitals.stream()
                    .filter(h -> h.getLocation().equalsIgnoreCase(patientCity)
                            && h.getResource(resourceNeeded) > 0)
                    .sorted((a, b) ->
                            b.getResource(resourceNeeded) - a.getResource(resourceNeeded))
                    .collect(Collectors.toList());

            // Other cities as backup
            List<Hospital> otherCities = allHospitals.stream()
                    .filter(h -> !h.getLocation().equalsIgnoreCase(patientCity)
                            && h.getResource(resourceNeeded) > 0)
                    .sorted((a, b) ->
                            b.getResource(resourceNeeded) - a.getResource(resourceNeeded))
                    .collect(Collectors.toList());

            JComboBox<String> combo = new JComboBox<>();
            combo.setBackground(Main.BG_INPUT);
            combo.setForeground(Main.TEXT_PRIMARY);
            combo.setFont(new Font("SansSerif", Font.PLAIN, 11));
            combo.setPreferredSize(new Dimension(380, 30));

            if (sameCity.isEmpty() && otherCities.isEmpty()) {
                combo.addItem("No hospital has " + resourceNeeded + " available");
                combo.setEnabled(false);
            } else {
                combo.addItem("Select hospital...");

                // Same city first
                if (!sameCity.isEmpty()) {
                    combo.addItem("-- Hospitals in " + patientCity + " --");
                    for (Hospital h : sameCity)
                        combo.addItem(h.getName() + " (" +
                                h.getResource(resourceNeeded) + " " +
                                resourceNeeded + " available) — " + h.getLocation());
                }

                // Other cities as backup
                if (!otherCities.isEmpty()) {
                    combo.addItem("-- Other Cities (Backup) --");
                    for (Hospital h : otherCities)
                        combo.addItem(h.getName() + " (" +
                                h.getResource(resourceNeeded) + " " +
                                resourceNeeded + " available) — " + h.getLocation());
                }
            }

            // City info label
            JLabel cityInfo = new JLabel(sameCity.isEmpty()
                    ? "No hospitals in " + patientCity + " — showing backup cities"
                    : sameCity.size() + " hospital(s) found in " + patientCity);
            cityInfo.setFont(new Font("SansSerif", Font.ITALIC, 10));
            cityInfo.setForeground(sameCity.isEmpty()
                    ? new Color(255, 107, 107)
                    : new Color(63, 185, 80));

            JButton assignBtn = new JButton("✅ Assign");
            Main.stylePrimaryBtn(assignBtn, Main.BLUE_BTN, new Color(13, 71, 161));
            assignBtn.setPreferredSize(new Dimension(100, 30));

            JButton rejectBtn = new JButton("✗ Reject");
            Main.stylePrimaryBtn(rejectBtn, new Color(108, 48, 48), new Color(80, 30, 30));
            rejectBtn.setPreferredSize(new Dimension(90, 30));

            JLabel msgLbl = new JLabel(" ");
            msgLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));

            assignBtn.addActionListener(e -> {
                String selected = (String) combo.getSelectedItem();
                if (selected == null
                        || selected.startsWith("Select")
                        || selected.startsWith("--")
                        || selected.startsWith("No hospital")) {
                    msgLbl.setForeground(new Color(255, 107, 107));
                    msgLbl.setText("Please select a hospital!");
                    return;
                }
                String hospitalName = selected.split(" \\(")[0];

                // FIX: pass resourceNeeded so correct column is deducted
                DatabaseHelper.assignHospital(
                        req.getId(),
                        hospitalName,
                        resourceNeeded,
                        req.getQuantityNeeded()
                );

                msgLbl.setForeground(new Color(63, 185, 80));
                msgLbl.setText("Assigned to " + hospitalName);
                assignBtn.setEnabled(false);
                rejectBtn.setEnabled(false);
                badge.setText("Assigned");
                badge.setBackground(Main.BLUE_BTN);

                // Update assigned hospital label in info panel live
                Component[] comps = info.getComponents();
                for (int i = 0; i < comps.length; i++) {
                    if (comps[i] instanceof JLabel lbl &&
                            lbl.getText().contains("Not yet assigned")) {
                        lbl.setText(hospitalName);
                        lbl.setForeground(new Color(116, 185, 255));
                        break;
                    }
                }
                info.revalidate();
                info.repaint();
            });

            rejectBtn.addActionListener(e -> {
                DatabaseHelper.rejectRequest(req.getId());
                msgLbl.setForeground(new Color(255, 107, 107));
                msgLbl.setText("Request rejected.");
                assignBtn.setEnabled(false);
                rejectBtn.setEnabled(false);
                badge.setText("Rejected");
                badge.setBackground(new Color(108, 48, 48));
            });

            // Layout — city info on top line, then combo + buttons
            JPanel comboSection = new JPanel();
            comboSection.setOpaque(false);
            comboSection.setLayout(new BoxLayout(comboSection, BoxLayout.Y_AXIS));
            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            btnRow.setOpaque(false);
            btnRow.add(combo);
            btnRow.add(assignBtn);
            btnRow.add(rejectBtn);
            btnRow.add(msgLbl);
            comboSection.add(cityInfo);
            comboSection.add(btnRow);
            actionRow.add(comboSection);

        } else if (req.getStatus().equals("Assigned")) {
            JButton fulfillBtn = new JButton("🏁 Mark as Fulfilled");
            Main.stylePrimaryBtn(fulfillBtn, Main.GREEN_BTN, new Color(27, 94, 32));
            fulfillBtn.addActionListener(e -> {
                DatabaseHelper.fulfillRequest(req.getId());
                fulfillBtn.setEnabled(false);
                fulfillBtn.setText("Fulfilled");
                badge.setText("Fulfilled");
                badge.setBackground(Main.GREEN_BTN);
            });
            actionRow.add(fulfillBtn);
        }

        card.add(topRow,   BorderLayout.NORTH);
        card.add(info,     BorderLayout.CENTER);
        if (actionRow.getComponentCount() > 0)
            card.add(actionRow, BorderLayout.SOUTH);

        return card;
    }

    private JLabel statusBadge(String status) {
        JLabel badge = new JLabel("  " + status + "  ");
        badge.setFont(new Font("SansSerif", Font.BOLD, 10));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        Color bg = switch (status) {
            case "Fulfilled" -> Main.GREEN_BTN;
            case "Assigned"  -> Main.BLUE_BTN;
            case "Rejected"  -> new Color(108, 48, 48);
            default          -> new Color(125, 98, 0);
        };
        badge.setBackground(bg);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        return badge;
    }

    private void addInfoRow(JPanel panel, String key, String value) {
        JLabel k = new JLabel(key + ": ");
        k.setFont(new Font("SansSerif", Font.PLAIN, 11));
        k.setForeground(Main.TEXT_MUTED);

        JLabel v = new JLabel(value != null ? value : "-");
        v.setFont(new Font("SansSerif", Font.PLAIN, 11));
        v.setForeground(Main.TEXT_PRIMARY);

        panel.add(k);
        panel.add(v);
    }
}