package process;

import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * Represents the stop times for a specific trip.
 * Stores trip ID, departure time, stop ID, and manages the itinerary for the trip.
 */
public class StopTime {
    String tripId;
    String departureTime;
    String stopId;

    TreeMap<Integer, List<String>> itinerary = new TreeMap<>(); 

    HashMap<String, String> stopIdToTime = new HashMap<>();

    /**
     * Constructs a StopTime object with the given attributes.
     * @param tripId Unique identifier for the trip
     * @param departureTime Departure time from the stop
     * @param stopId Identifier of the stop
     */
    public StopTime(String tripId, String departureTime, String stopId){
        this.tripId = tripId;
        this.departureTime = departureTime;
        this.stopId = stopId;
    }

    /*
     * Getter of the class
     */
    public String getDepartureTime() { return departureTime; }
    public String getTripId() { return tripId; }
    public String getStopId() { return stopId; }
    public TreeMap<Integer, List<String>> getItinerary() { return itinerary; }

    public HashMap<String, String> getStopIdToTime() { return stopIdToTime; }

    /**
     * Adds a stop to the itinerary with its sequence, stopTimeId, and timeTable.
     * Also updates the stopId-to-time map.
     * @param stopSequence Sequence number of the stop in the trip
     * @param stopTimeId Identifier for the stop time
     * @param timeTable Scheduled time for the stop
     */
    public void addToItinerary(int stopSequence, String stopTimeId, String timeTable) {
        List<String> stopTimeInfo = new ArrayList<>();
        stopTimeInfo.add(stopTimeId);
        stopTimeInfo.add(timeTable);
        itinerary.put(stopSequence, stopTimeInfo);

        stopIdToTime.put(stopTimeId, timeTable);
    }
}