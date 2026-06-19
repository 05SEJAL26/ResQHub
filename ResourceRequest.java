// ResourceRequest.java
// Team Member: Anvesha Singh (24022980)
// Model class for resource requests raised by USER

public class ResourceRequest {

    private int    id;
    private String raisedBy;
    private String city;
    private String resourceNeeded;
    private int    quantityNeeded;
    private String reason;
    private String status;
    private String assignedHospital;   // ← NEW
    private String raisedAt;
    private String fulfilledAt;

    // Constructor for loading from database (includes assignedHospital)
    public ResourceRequest(int id, String raisedBy, String city,
                           String resourceNeeded, int quantityNeeded,
                           String reason, String status,
                           String assignedHospital,
                           String raisedAt, String fulfilledAt) {
        this.id               = id;
        this.raisedBy         = raisedBy;
        this.city             = city;
        this.resourceNeeded   = resourceNeeded;
        this.quantityNeeded   = quantityNeeded;
        this.reason           = reason;
        this.status           = status;
        this.assignedHospital = assignedHospital;
        this.raisedAt         = raisedAt;
        this.fulfilledAt      = fulfilledAt;
    }

    // Constructor for creating new request (no id/hospital/timestamps yet)
    public ResourceRequest(String raisedBy, String city,
                           String resourceNeeded, int quantityNeeded,
                           String reason) {
        this(0, raisedBy, city, resourceNeeded,
                quantityNeeded, reason, "Pending", null, null, null);
    }

    public int    getId()               { return id;             }
    public String getRaisedBy()         { return raisedBy;       }
    public String getCity()             { return city;           }
    public String getResourceNeeded()   { return resourceNeeded; }
    public int    getQuantityNeeded()   { return quantityNeeded; }
    public String getReason()           { return reason;         }
    public String getStatus()           { return status;         }
    public String getAssignedHospital() { return assignedHospital != null ? assignedHospital : "-"; }
    public String getRaisedAt()         { return raisedAt;       }
    public String getFulfilledAt()      { return fulfilledAt != null ? fulfilledAt : "-"; }

    @Override
    public String toString() {
        return "[" + id + "] " + city + " needs " +
                quantityNeeded + " " + resourceNeeded +
                " | Status: " + status +
                (assignedHospital != null ? " | Hospital: " + assignedHospital : "");
    }
}