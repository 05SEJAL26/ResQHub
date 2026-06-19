// RequestPanel.java
// Team Member: Anvesha Singh (24022980)
// Swing request form - USER raises resource requests

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class RequestPanel {

    public JPanel getPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Main.BG_DARK);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Main.BG_CARD);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));
        JLabel title = new JLabel("🆘  Raise a Resource Request");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(Main.TEXT_PRIMARY);
        JLabel sub = new JLabel("Submit your request and admin will assign the nearest hospital");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sub.setForeground(Main.TEXT_MUTED);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title); titleBox.add(Box.createVerticalStrut(4)); titleBox.add(sub);
        header.add(titleBox, BorderLayout.WEST);

        // Form
        JPanel form = new JPanel();
        form.setBackground(Main.BG_CARD);
        form.setLayout(new GridBagLayout());
        form.setBorder(new EmptyBorder(28, 40, 28, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0; gbc.weightx = 1;

        JTextField nameField     = Main.styledField("Enter your full name");
        JTextField cityField     = Main.styledField("Enter your city");
        String[]   resources     = {"Ventilators", "ICU Beds", "Oxygen Cylinders",
                "Blood Units", "Ambulances", "Trauma Specialists"};
        JComboBox<String> resourceCombo = Main.styledCombo(resources);
        JTextField qtyField      = Main.styledField("e.g. 2");
        JTextArea  reasonArea    = new JTextArea(4, 20);
        reasonArea.setBackground(Main.BG_INPUT);
        reasonArea.setForeground(Main.TEXT_PRIMARY);
        reasonArea.setCaretColor(Main.TEXT_PRIMARY);
        reasonArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Main.BORDER_CLR),
                new EmptyBorder(8, 12, 8, 12)));
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setBorder(BorderFactory.createLineBorder(Main.BORDER_CLR));

        JLabel statusMsg = new JLabel(" ");
        statusMsg.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JButton submitBtn = new JButton("Submit Request →");
        Main.stylePrimaryBtn(submitBtn, Main.RED_PRIMARY, Main.RED_DARK);

        submitBtn.addActionListener(e -> {
            String name     = nameField.getText().trim();
            String city     = cityField.getText().trim();
            String resource = (String) resourceCombo.getSelectedItem();
            String qtyStr   = qtyField.getText().trim();
            String reason   = reasonArea.getText().trim();

            if (name.isEmpty() || city.isEmpty() || qtyStr.isEmpty() || reason.isEmpty()) {
                statusMsg.setForeground(new Color(255, 107, 107));
                statusMsg.setText("⚠  Please fill in all fields.");
                return;
            }
            int qty;
            try {
                qty = Integer.parseInt(qtyStr);
                if (qty <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                statusMsg.setForeground(new Color(255, 107, 107));
                statusMsg.setText("⚠  Quantity must be a positive number.");
                return;
            }

            DatabaseHelper.insertRequest(new ResourceRequest(name, city, resource, qty, reason));
            statusMsg.setForeground(new Color(63, 185, 80));
            statusMsg.setText("✅  Request submitted! Admin will assign a hospital shortly.");
            nameField.setText(""); cityField.setText("");
            qtyField.setText(""); reasonArea.setText("");
            resourceCombo.setSelectedIndex(0);
        });

        gbc.gridy = 0; form.add(Main.mutedLabel("👤  Your Name"), gbc);
        gbc.gridy = 1; form.add(nameField, gbc);
        gbc.gridy = 2; form.add(Box.createVerticalStrut(4), gbc);
        gbc.gridy = 3; form.add(Main.mutedLabel("📍  Your City"), gbc);
        gbc.gridy = 4; form.add(cityField, gbc);
        gbc.gridy = 5; form.add(Box.createVerticalStrut(4), gbc);
        gbc.gridy = 6; form.add(Main.mutedLabel("🏥  Resource Needed"), gbc);
        gbc.gridy = 7; form.add(resourceCombo, gbc);
        gbc.gridy = 8; form.add(Box.createVerticalStrut(4), gbc);
        gbc.gridy = 9; form.add(Main.mutedLabel("🔢  Quantity Needed"), gbc);
        gbc.gridy = 10; form.add(qtyField, gbc);
        gbc.gridy = 11; form.add(Box.createVerticalStrut(4), gbc);
        gbc.gridy = 12; form.add(Main.mutedLabel("📝  Reason / Description"), gbc);
        gbc.gridy = 13; form.add(reasonScroll, gbc);
        gbc.gridy = 14; form.add(Box.createVerticalStrut(8), gbc);
        gbc.gridy = 15; form.add(statusMsg, gbc);
        gbc.gridy = 16;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.add(submitBtn);
        form.add(btnRow, gbc);

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBackground(Main.BG_DARK);
        scrollForm.getViewport().setBackground(Main.BG_DARK);
        scrollForm.setBorder(null);

        root.add(header, BorderLayout.NORTH);
        root.add(scrollForm, BorderLayout.CENTER);
        return root;
    }
}