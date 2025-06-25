package graph;

/**
 * Represents a directed edge in the transport graph, corresponding to a connection
 * between two stops (nodes) in the network. Each edge contains information about
 * the departure and arrival stops, the road or line ID, the duration of the trip,
 * and the trip ID.
 */
public class Edge {
    String departureStopId;
    String arrivalStopId;
    String roadId;
    float duration; 
    String tripId;

    /**
     * Constructs an Edge with the specified parameters.
     * @param departureStop the ID of the departure stop
     * @param arrivalStop the ID of the arrival stop
     * @param roadId the road ID
     * @param duration the duration in minutes
     * @param tripId the trip ID (can be null for walking edges)
     */
    public Edge(String departureStop, String arrivalStop, String roadId, float duration, String tripId) { //}, String horraire) {
        this.departureStopId = departureStop;
        this.arrivalStopId = arrivalStop;
        this.roadId = roadId;
        this.duration = duration; 
        this.tripId = tripId;
    }

    /*
     * Getter of the class
     */
    public String getDepartureStopId() { return departureStopId; }
    public String getArrivalStopId() { return arrivalStopId; }

    public String getRoadId() { return roadId; }
    public float getDuration() { return duration; }
    public String getTripId() { return tripId; }
}