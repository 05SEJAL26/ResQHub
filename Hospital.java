import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Hospital implements Serializable {

    private String name;
    private String location;
    private HashMap<String, Integer> resources;

    public Hospital(String name, String location) {
        this.name = name;
        this.location = location;
        this.resources = new HashMap<>();
    }

    public void setResource(String resourceName, int quantity) {
        resources.put(resourceName, quantity);
    }

    public int getResource(String resourceName) {
        return resources.getOrDefault(resourceName, 0);
    }

    public Map<String, Integer> getAllResources() {
        return resources;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return name + " (" + location + ") - Resources: " + resources;
    }
}
