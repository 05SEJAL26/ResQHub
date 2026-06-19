// AddHospitalPanel.java
// Team Member: Sejal Singh (240221951)
// Swing Add Hospital form for ADMIN

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class AddHospitalPanel {

    private List<Hospital> hospitals;
    private MainFrame frame;

    public AddHospitalPanel(List<Hospital> hospitals, MainFrame frame) {
        this.hospitals = hospitals;
        this.frame     = frame;
    }

    public JPanel getPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Main.BG_DARK);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Main.BG_CARD);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));
        JLabel title = new JLabel("➕  Add New Hospital");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(Main.TEXT_PRIMARY);
        JLabel sub = new JLabel("Add a new hospital and its resource availability");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sub.setForeground(Main.TEXT_MUTED);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title); titleBox.add(Box.createVerticalStrut(4)); titleBox.add(sub);
        header.add(titleBox, BorderLayout.WEST);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Main.BG_CARD);
        form.setBorder(new EmptyBorder(28, 40, 28, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0; gbc.weightx = 1;

        JTextField nameField  = Main.styledField("Hospital name");
        JTextField cityField  = Main.styledField("City");
        JTextField icuField   = Main.styledField("0");
        JTextField ventField  = Main.styledField("0");
        JTextField oxyField   = Main.styledField("0");
        JTextField bloodField = Main.styledField("0");
        JTextField traumaField = Main.styledField("0");
        JTextField ambField   = Main.styledField("0");

        JLabel statusMsg = new JLabel(" ");
        statusMsg.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JButton addBtn = new JButton("Add Hospital →");
        Main.stylePrimaryBtn(addBtn, Main.RED_PRIMARY, Main.RED_DARK);

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String city = cityField.getText().trim();
            if (name.isEmpty() || city.isEmpty()) {
                statusMsg.setForeground(new Color(255, 107, 107));
                statusMsg.setText("⚠  Name and City are required.");
                return;
            }
            try {
                Hospital h = new Hospital(name, city);
                h.setResource("ICU Beds",            Integer.parseInt(icuField.getText().trim().isEmpty()   ? "0" : icuField.getText().trim()));
                h.setResource("Ventilators",         Integer.parseInt(ventField.getText().trim().isEmpty()  ? "0" : ventField.getText().trim()));
                h.setResource("Oxygen Cylinders",    Integer.parseInt(oxyField.getText().trim().isEmpty()   ? "0" : oxyField.getText().trim()));
                h.setResource("Blood Units",         Integer.parseInt(bloodField.getText().trim().isEmpty() ? "0" : bloodField.getText().trim()));
                h.setResource("Trauma Specialists",  Integer.parseInt(traumaField.getText().trim().isEmpty()? "0" : traumaField.getText().trim()));
                h.setResource("Ambulances",          Integer.parseInt(ambField.getText().trim().isEmpty()   ? "0" : ambField.getText().trim()));
                DatabaseHelper.insertHospital(h);
                hospitals.add(h);
                statusMsg.setForeground(new Color(63, 185, 80));
                statusMsg.setText("✅  Hospital '" + name + "' added successfully!");
                nameField.setText(""); cityField.setText("");
                icuField.setText("0"); ventField.setText("0");
                oxyField.setText("0"); bloodField.setText("0");
                traumaField.setText("0"); ambField.setText("0");
            } catch (NumberFormatException ex) {
                statusMsg.setForeground(new Color(255, 107, 107));
                statusMsg.setText("⚠  Resource quantities must be valid numbers.");
            }
        });

        int row = 0;
        gbc.gridy = row++; form.add(Main.mutedLabel("Hospital Name"), gbc);
        gbc.gridy = row++; form.add(nameField, gbc);
        gbc.gridy = row++; form.add(Main.mutedLabel("City"), gbc);
        gbc.gridy = row++; form.add(cityField, gbc);
        gbc.gridy = row++; form.add(Main.mutedLabel("🛏  ICU Beds"), gbc);
        gbc.gridy = row++; form.add(icuField, gbc);
        gbc.gridy = row++; form.add(Main.mutedLabel("🫁  Ventilators"), gbc);
        gbc.gridy = row++; form.add(ventField, gbc);
        gbc.gridy = row++; form.add(Main.mutedLabel("💨  Oxygen Cylinders"), gbc);
        gbc.gridy = row++; form.add(oxyField, gbc);
        gbc.gridy = row++; form.add(Main.mutedLabel("🩸  Blood Units"), gbc);
        gbc.gridy = row++; form.add(bloodField, gbc);
        gbc.gridy = row++; form.add(Main.mutedLabel("👨‍⚕️  Trauma Specialists"), gbc);
        gbc.gridy = row++; form.add(traumaField, gbc);
        gbc.gridy = row++; form.add(Main.mutedLabel("🚑  Ambulances"), gbc);
        gbc.gridy = row++; form.add(ambField, gbc);
        gbc.gridy = row++; form.add(Box.createVerticalStrut(8), gbc);
        gbc.gridy = row++; form.add(statusMsg, gbc);
        gbc.gridy = row;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnRow.setOpaque(false); btnRow.add(addBtn);
        form.add(btnRow, gbc);

        JScrollPane scroll = new JScrollPane(form);
        scroll.getViewport().setBackground(Main.BG_DARK);
        scroll.setBorder(null);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        return root;
    }
}