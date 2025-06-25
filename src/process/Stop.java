package process;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a public transport stop or station.
 * Stores the stop's identifier, name, and geographic coordinates.
 */
public class Stop {
    String stopId;
    String stopName;
    float stopLat;
    float stopLon;

    /**
     * Constructs a Stop object with the given attributes.
     * @param stopId Unique identifier for the stop
     * @param stopName Name of the stop
     * @param stopLat Latitude of the stop
     * @param stopLon Longitude of the stop
     */
    public Stop(String stopId, String stopName, float stopLat, float stopLon){
        this.stopId = stopId;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
        this.stopName = stopName;
    }

    /**
     * Returns a list of all transport types serving this stop.
     * (Currently returns an empty list; to be implemented.)
     */
    public List<String> getAllTransportType() {
        List<String> allTransport = new ArrayList<>();
        return allTransport;
    }

    /*
     * Getter of the class
     */
    public String getStopId() { return stopId; }
    public String getStopName() { return stopName; }
    public float getStopLat() { return stopLat; }
    public float getStopLon() { return stopLon; }
}
