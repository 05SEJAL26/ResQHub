import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class HospitalPanel extends JPanel {

    public HospitalPanel(List<Hospital> hospitals, Role role, MainFrame frame) {
        setLayout(new BorderLayout());

        JLabel heading = new JLabel("  Hospitals - Resource Details");
        heading.setFont(new Font("Arial", Font.BOLD, 16));
        heading.setPreferredSize(new Dimension(600, 40));
        add(heading, BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();

        for (Hospital h : hospitals) {
            JPanel panel = new JPanel(new BorderLayout());
            String[] cols = {"Resource", "Quantity", "Status"};
            DefaultTableModel model = new DefaultTableModel(cols, 0);
            for (Map.Entry<String, Integer> entry : h.getAllResources().entrySet()) {
                String status;
                int value = entry.getValue();

                if (value == 0) status = "Unavailable";
                else if (value <= 5) status = "Low";
                else status = "Available";

                model.addRow(new Object[]{
                        entry.getKey(),
                        value,
                        status
                });
            }

            JTable table = new JTable(model);
            table.setRowHeight(26);
            table.setFont(new Font("Arial", Font.PLAIN, 13));
            table.getTableHeader().setBackground(new Color(52, 58, 64));
            table.getTableHeader().setForeground(Color.WHITE);
            table.setEnabled(false);

            panel.add(new JScrollPane(table), BorderLayout.CENTER);

            if (role == Role.ADMIN) {
                JButton updateBtn = new JButton("Update Resources");
                updateBtn.setBackground(new Color(220, 53, 69));
                updateBtn.setForeground(Color.WHITE);

                updateBtn.addActionListener(e ->
                        showUpdateDialog(h, hospitals, role, frame)
                );

                JPanel btnPanel = new JPanel();
                btnPanel.add(updateBtn);
                panel.add(btnPanel, BorderLayout.SOUTH);
            }

            tabs.addTab(h.getName(), panel);
        }

        add(tabs, BorderLayout.CENTER);
    }

    private void showUpdateDialog(Hospital h, List<Hospital> hospitals, Role role, MainFrame frame) {

        JDialog dialog = new JDialog();
        dialog.setTitle("Update - " + h.getName());
        dialog.setSize(320, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new GridLayout(0, 2, 8, 8));

        Map<String, JTextField> fields = new java.util.LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : h.getAllResources().entrySet()) {
            dialog.add(new JLabel("  " + entry.getKey()));

            JTextField tf = new JTextField(String.valueOf(entry.getValue()));
            fields.put(entry.getKey(), tf);

            dialog.add(tf);
        }

        JButton save = new JButton("Save");
        save.setBackground(new Color(40, 167, 69));
        save.setForeground(Color.WHITE);

        save.addActionListener(e -> {

            for (Map.Entry<String, JTextField> f : fields.entrySet()) {
                String text = f.getValue().getText();

                if (InputValidator.isValidQuantity(text)) {
                    int val = Integer.parseInt(text.trim());
                    h.setResource(f.getKey(), val);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Invalid input for " + f.getKey(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            DatabaseHelper.updateHospital(h);

            dialog.dispose();

            frame.showPanel(new HospitalPanel(hospitals, role, frame));

            JOptionPane.showMessageDialog(null, "Resources Updated Successfully!");
        });

        dialog.add(new JLabel());
        dialog.add(save);

        dialog.setVisible(true);
    }
}