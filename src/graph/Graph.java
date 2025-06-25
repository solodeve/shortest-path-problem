package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import process.Road;
import process.Stop;
import process.StopTime;

/**
 * Represents the transport network as a directed graph.
 * Each node is a stop, and each edge is a connection (trip or walking).
 * The graph is built from GTFS data (stops, stop_times, trips, roads).
 */
public class Graph {
    HashMap<String, List<Edge>> graph = new HashMap<>();
    HashMap<String, Road> roadMap;
    HashMap<String, String> tripMap;
    HashMap<String, Stop> stopMap;
    HashMap<String, StopTime> stopTimeMap;

    public Graph(HashMap<String, Road> roadMap, HashMap<String, String> tripMap, HashMap<String, Stop> stopMap, HashMap<String, StopTime> stopTimeMap) {
        this.roadMap = roadMap;
        this.tripMap = tripMap;
        this.stopMap = stopMap;
        this.stopTimeMap = stopTimeMap;
    }

    /*
     * Getter of the class
     */
    public HashMap<String, List<Edge>> getGraph() { return graph; }
    public HashMap<String, Road> getRoadMap() { return roadMap; }
    public HashMap<String, String> getTripMap() { return tripMap; }
    public HashMap<String, Stop> getStopMap() { return stopMap; }
    public HashMap<String, StopTime> getStopTimeMap() { return stopTimeMap; }

    public void addStop(String stopId) {
        graph.putIfAbsent(stopId, new ArrayList<>());
    }

    /**
     * Adds an edge to the graph, ensuring both endpoints exist.
     */
    public void addEdge(Edge edge) {
        String departureStopId = edge.getDepartureStopId();
        String arrivalStopId = edge.getArrivalStopId();

        graph.putIfAbsent(departureStopId, new ArrayList<>());
        graph.putIfAbsent(arrivalStopId, new ArrayList<>());

        List<Edge> edges = graph.get(departureStopId);
        if (!edges.contains(edge)) { 
            edges.add(edge);
        }
    }

    /**
     * Builds the graph from the GTFS data:
     * - Adds all stops as nodes.
     * - Adds edges for each trip segment (from stop_times).
     * - Adds walking edges between stops within a certain distance.
     */
    public void createGraph() {
        int edgeCounter = 0;

        // Add all stops as nodes
        for (Stop stop : stopMap.values()) {
            String stopId = stop.getStopId();
            addStop(stopId);
        }

        // Add edges for each trip segment
        for (StopTime stopTime : stopTimeMap.values()) {
            TreeMap<Integer, List<String>> itinerary = stopTime.getItinerary();
            String previousStopId = null;
            String previousDepartureTime = null;

            for (List<String> stopInfo : itinerary.values()) {
                String currentStopId = stopInfo.get(0);
                String departureTime = stopInfo.get(1);

                if (currentStopId == null) continue;

                if (previousStopId != null) {
                    float duration = computeDuration(previousDepartureTime, departureTime);
                    if (duration < 0) continue;

                    Edge edge = new Edge(
                        previousStopId,
                        currentStopId,
                        tripMap.get(stopTime.getTripId()),
                        duration,
                        stopTime.getTripId()
                    );
                    addEdge(edge);
                    edgeCounter++;
                }

                previousStopId = currentStopId;
                previousDepartureTime = departureTime;
            }
        }

        // Add walking edges between nearby stops
        float walkingSpeed = 1.4f; // 5 km/h
        float maxWalkingDistance = 1000f; // 1km

        List<Stop> stops = new ArrayList<>(stopMap.values());
        for (int i = 0; i < stops.size(); i++) {
            Stop stopA = stops.get(i);
            for (int j = i + 1; j < stops.size(); j++) {
                Stop stopB = stops.get(j);

                // Approximate distance in meters using latitude/longitude
                float dLat = stopA.getStopLat() - stopB.getStopLat();
                float dLon = stopA.getStopLon() - stopB.getStopLon();
                float distance = (float) (Math.sqrt(dLat * dLat + dLon * dLon) * 111_000); 

                if (distance > 0 && distance <= maxWalkingDistance) {
                    float duration = distance / walkingSpeed / 60f; 

                    Edge walkEdgeAB = new Edge(
                        stopA.getStopId(),
                        stopB.getStopId(),
                        "WALK",
                        duration,
                        null
                    );
                    addEdge(walkEdgeAB);

                    Edge walkEdgeBA = new Edge(
                        stopB.getStopId(),
                        stopA.getStopId(),
                        "WALK",
                        duration,
                        null
                    );
                    addEdge(walkEdgeBA);
                }
            }
        }

        System.out.println("- Nombre total de edges créées : " + edgeCounter);
        System.out.println("- Nombre total de nodes créées : " + graph.size());
    }

    /**
     * Computes the duration in minutes between two time strings ("HH:mm:ss").
     * Returns 0 if parsing fails.
     */
    private float computeDuration(String startTime, String endTime) {
        try {
            int startSeconds = Integer.parseInt(startTime.substring(0, 2)) * 3600
                            + Integer.parseInt(startTime.substring(3, 5)) * 60
                            + Integer.parseInt(startTime.substring(6, 8));
            int endSeconds = Integer.parseInt(endTime.substring(0, 2)) * 3600
                        + Integer.parseInt(endTime.substring(3, 5)) * 60
                        + Integer.parseInt(endTime.substring(6, 8));
            return (endSeconds - startSeconds) / 60.0f;
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul de la durée entre " + startTime + " et " + endTime);
            return 0;
        }
    }
}
