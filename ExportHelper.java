// ExportHelper.java
// Team Member: Krishni Rastogi (24022510)
// Exports hospital resource report to a TXT file - USER only

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ExportHelper {

    public static void exportReport(List<Hospital> hospitals) {

        // Swing file chooser — replaces JavaFX FileChooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        fileChooser.setSelectedFile(new java.io.File("ResQHub_Report.txt"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Text Files (*.txt)", "txt"));

        int result = fileChooser.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) return; // user cancelled

        java.io.File file = fileChooser.getSelectedFile();

        // Make sure file ends with .txt
        if (!file.getName().endsWith(".txt")) {
            file = new java.io.File(file.getAbsolutePath() + ".txt");
        }

        try (FileWriter writer = new FileWriter(file)) {

            String time = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

            // Report header
            writer.write("================================================\n");
            writer.write("       RESQHUB - EMERGENCY RESOURCE REPORT       \n");
            writer.write("================================================\n");
            writer.write("Generated: " + time + "\n");
            writer.write("Total Hospitals: " + hospitals.size() + "\n");
            writer.write("================================================\n\n");

            // Each hospital
            for (Hospital h : hospitals) {
                writer.write("Hospital : " + h.getName() + "\n");
                writer.write("Location : " + h.getLocation() + "\n");
                writer.write("Resources:\n");

                for (Map.Entry<String, Integer> entry :
                        h.getAllResources().entrySet()) {
                    int qty = entry.getValue();
                    String status = qty == 0 ? "[CRITICAL]" :
                            qty <= 5  ? "[LOW]"      : "[OK]";
                    writer.write(String.format(
                            "  %-25s : %3d  %s\n",
                            entry.getKey(), qty, status));
                }
                writer.write("------------------------------------------------\n\n");
            }

            // Summary
            int totalICU  = hospitals.stream()
                    .mapToInt(h -> h.getResource("ICU Beds")).sum();
            int totalVent = hospitals.stream()
                    .mapToInt(h -> h.getResource("Ventilators")).sum();
            long critical = hospitals.stream().filter(h ->
                    h.getResource("ICU Beds") <= 3 ||
                            h.getResource("Ventilators") <= 2).count();

            writer.write("================================================\n");
            writer.write("SUMMARY\n");
            writer.write("================================================\n");
            writer.write("Total ICU Beds    : " + totalICU  + "\n");
            writer.write("Total Ventilators : " + totalVent + "\n");
            writer.write("Critical Hospitals: " + critical  + "\n");
            writer.write("================================================\n");
            writer.write("End of Report\n");

            // Swing dialog — replaces JavaFX Alert
            JOptionPane.showMessageDialog(null,
                    "Report saved to:\n" + file.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to save report: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}