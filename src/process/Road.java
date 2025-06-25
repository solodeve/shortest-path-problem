package process;

/**
 * Represents a road or transit line in the transport network.
 * Stores identifiers and descriptive information about the road/line.
 */
public class Road {
    String roadId;
    String roadShortName;
    String roadLongName;
    String transportType;

    /**
     * Constructs a Road object with the given attributes.
     * @param roadId Unique identifier for the road/line
     * @param roadShortName Short name or code for the road/line
     * @param roadLongName Full descriptive name of the road/line
     * @param transportType Type of transport (e.g., BUS, TRAM, TRAIN)
     */
    public Road(String roadId, String roadShortName, String roadLongName, String transportType){
        this.roadId = roadId;
        this.roadLongName = roadLongName;
        this.roadShortName = roadShortName;
        this.transportType = transportType;
    }

    /*
     * Getter of the class
     */
    public String getRoadId() { return roadId; }
    public String getRoadShortName() { return roadShortName; }
    public String getRoadLongName() { return roadLongName; }
    public String getTransportType() { return transportType; }
    
} 
