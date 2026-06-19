// DatabaseHelper.java
// Team Member: Krishni Rastogi (24022510)
// Handles all MySQL database operations including resource requests

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String URL      = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "Sandeep@2307";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Create both tables
    public static void initDatabase() {
        String createHospitals = "CREATE TABLE IF NOT EXISTS hospitals (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100), city VARCHAR(100), " +
                "icu_beds INT, ventilators INT, oxygen INT, " +
                "blood_units INT, trauma_specialists INT, ambulances INT)";

        String createRequests = "CREATE TABLE IF NOT EXISTS requests (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "raised_by VARCHAR(100), city VARCHAR(100), " +
                "resource_needed VARCHAR(100), quantity_needed INT, " +
                "reason TEXT, status VARCHAR(20) DEFAULT 'Pending', " +
                "assigned_hospital VARCHAR(100) DEFAULT NULL, " +
                "raised_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "fulfilled_at TIMESTAMP NULL)";

        String alterRequests = "ALTER TABLE requests ADD COLUMN IF NOT EXISTS " +
                "assigned_hospital VARCHAR(100) DEFAULT NULL";

        try (Connection con = getConnection();
             Statement st = con.createStatement()) {
            st.execute(createHospitals);
            st.execute(createRequests);
            try { st.execute(alterRequests); } catch (SQLException ignored) {}
            System.out.println("Database tables ready.");
        } catch (SQLException e) {
            System.err.println("Init error: " + e.getMessage());
        }
    }

    // ===== HOSPITAL METHODS =====

    public static List<Hospital> loadHospitals() {
        List<Hospital> hospitals = new ArrayList<>();
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM hospitals")) {
            while (rs.next()) {
                Hospital h = new Hospital(
                        rs.getString("name"), rs.getString("city"));
                h.setResource("ICU Beds",           rs.getInt("icu_beds"));
                h.setResource("Ventilators",        rs.getInt("ventilators"));
                h.setResource("Oxygen Cylinders",   rs.getInt("oxygen"));
                h.setResource("Blood Units",        rs.getInt("blood_units"));
                h.setResource("Trauma Specialists", rs.getInt("trauma_specialists"));
                h.setResource("Ambulances",         rs.getInt("ambulances"));
                hospitals.add(h);
            }
        } catch (SQLException e) {
            System.err.println("Error loading hospitals: " + e.getMessage());
        }
        return hospitals;
    }

    public static void insertHospital(Hospital h) {
        String q = "INSERT INTO hospitals (name,city,icu_beds,ventilators,oxygen," +
                "blood_units,trauma_specialists,ambulances) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {
            ps.setString(1, h.getName());
            ps.setString(2, h.getLocation());
            ps.setInt(3, h.getResource("ICU Beds"));
            ps.setInt(4, h.getResource("Ventilators"));
            ps.setInt(5, h.getResource("Oxygen Cylinders"));
            ps.setInt(6, h.getResource("Blood Units"));
            ps.setInt(7, h.getResource("Trauma Specialists"));
            ps.setInt(8, h.getResource("Ambulances"));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting: " + e.getMessage());
        }
    }

    public static void updateHospital(Hospital h) {
        String q = "UPDATE hospitals SET icu_beds=?,ventilators=?,oxygen=?," +
                "blood_units=?,trauma_specialists=?,ambulances=? WHERE name=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {
            ps.setInt(1, h.getResource("ICU Beds"));
            ps.setInt(2, h.getResource("Ventilators"));
            ps.setInt(3, h.getResource("Oxygen Cylinders"));
            ps.setInt(4, h.getResource("Blood Units"));
            ps.setInt(5, h.getResource("Trauma Specialists"));
            ps.setInt(6, h.getResource("Ambulances"));
            ps.setString(7, h.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating: " + e.getMessage());
        }
    }

    public static void deleteHospital(String name) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM hospitals WHERE name=?")) {
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting: " + e.getMessage());
        }
    }

    // ===== REQUEST METHODS =====

    public static void insertRequest(ResourceRequest req) {
        String q = "INSERT INTO requests " +
                "(raised_by,city,resource_needed,quantity_needed,reason) " +
                "VALUES (?,?,?,?,?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {
            ps.setString(1, req.getRaisedBy());
            ps.setString(2, req.getCity());
            ps.setString(3, req.getResourceNeeded());
            ps.setInt(4,    req.getQuantityNeeded());
            ps.setString(5, req.getReason());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting request: " + e.getMessage());
        }
    }

    public static List<ResourceRequest> loadAllRequests() {
        List<ResourceRequest> list = new ArrayList<>();
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM requests ORDER BY raised_at DESC")) {
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) {
            System.err.println("Error loading requests: " + e.getMessage());
        }
        return list;
    }

    public static List<ResourceRequest> loadRequestsByCity(String city) {
        List<ResourceRequest> list = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM requests WHERE city=? ORDER BY raised_at DESC")) {
            ps.setString(1, city);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) {
            System.err.println("Error loading by city: " + e.getMessage());
        }
        return list;
    }

    /**
     * Assign a hospital to a request.
     * Deducts the CORRECT resource column based on what was requested.
     * e.g. if request is for Ventilators, deducts ventilators
     *      if request is for ICU Beds, deducts icu_beds
     */
    public static void assignHospital(int requestId, String hospitalName,
                                      String resourceNeeded, int quantityNeeded) {

        // Map resource name to actual database column name
        String dbColumn = mapToDbColumn(resourceNeeded);

        String updateRequest  = "UPDATE requests SET status='Assigned', " +
                "assigned_hospital=? WHERE id=?";

        // Dynamically deduct the correct column
        String deductResource = "UPDATE hospitals SET " + dbColumn +
                " = GREATEST(" + dbColumn + " - ?, 0) WHERE name=?";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps1 = con.prepareStatement(updateRequest);
                 PreparedStatement ps2 = con.prepareStatement(deductResource)) {

                // Update request status and assigned hospital
                ps1.setString(1, hospitalName);
                ps1.setInt(2, requestId);
                ps1.executeUpdate();

                // Deduct correct resource from hospital
                ps2.setInt(1, quantityNeeded);
                ps2.setString(2, hospitalName);
                ps2.executeUpdate();

                con.commit();
                System.out.println("Assigned " + hospitalName +
                        " for " + quantityNeeded + " " + resourceNeeded);

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error assigning hospital: " + e.getMessage());
        }
    }

    /**
     * Maps user-facing resource name to MySQL column name
     */
    private static String mapToDbColumn(String resourceNeeded) {
        return switch (resourceNeeded) {
            case "ICU Beds"           -> "icu_beds";
            case "Ventilators"        -> "ventilators";
            case "Oxygen Cylinders"   -> "oxygen";
            case "Blood Units"        -> "blood_units";
            case "Trauma Specialists" -> "trauma_specialists";
            case "Ambulances"         -> "ambulances";
            default                   -> "icu_beds"; // fallback
        };
    }

    public static void fulfillRequest(int id) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE requests SET status='Fulfilled'," +
                             "fulfilled_at=NOW() WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error fulfilling: " + e.getMessage());
        }
    }

    public static void rejectRequest(int id) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE requests SET status='Rejected' WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error rejecting: " + e.getMessage());
        }
    }

    private static ResourceRequest extract(ResultSet rs) throws SQLException {
        return new ResourceRequest(
                rs.getInt("id"),
                rs.getString("raised_by"),
                rs.getString("city"),
                rs.getString("resource_needed"),
                rs.getInt("quantity_needed"),
                rs.getString("reason"),
                rs.getString("status"),
                rs.getString("assigned_hospital"),
                rs.getString("raised_at"),
                rs.getString("fulfilled_at")
        );
    }

    public static boolean testConnection() {
        try (Connection con = getConnection()) {
            return con.isValid(2);
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return false;
        }
    }
}