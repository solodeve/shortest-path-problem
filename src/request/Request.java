package request;

// import request.Astar;
import graph.Graph;
import process.Road;
import process.Stop;
import process.StopTime;
import common.Common;

import java.util.HashMap;
import java.util.List;

import graph.Edge;

/**
 * Handles a route request between two stops, using A* search on the transport graph.
 * Stores request parameters and provides methods to execute the search and display the result.
 */
public class Request {
    Astar aStar;
    String startName;
    String goalName;
    float startHorraire;

    /**
     * Constructs a Request object with the given parameters.
     * Initializes the A* search with the provided graph and options.
     * @param startName Name of the departure stop
     * @param goalName Name of the arrival stop
     * @param startHorraire Departure time as string (HH:mm:ss)
     * @param graph The transport graph
     * @param optionLst List of user options (e.g., preferred/avoided modes)
     */
    public Request(String startName, String goalName, String startHorraire, Graph graph, List<String> optionLst) {
        this.startName = startName;
        this.goalName = goalName;
        this.startHorraire = Common.HoraireToFloat(startHorraire);
        this.aStar = new Astar(graph, startName, goalName, this.startHorraire, optionLst);
    }

    /**
     * Displays the computed path in a human-readable format.
     * Groups consecutive edges of the same mode/line and prints details for each segment.
     * @param pathEdges List of edges representing the path
     */
    public void view(List<Edge> pathEdges) {
        Graph graph = aStar.graph;
        HashMap<String, Stop> stopMap = graph.getStopMap();
        HashMap<String, String> tripMap = graph.getTripMap();
        HashMap<String, Road> roadMap = graph.getRoadMap();
        HashMap<String, StopTime> stopTimeMap = graph.getStopTimeMap();

        int i = 0;
        float lastArrival = startHorraire; 
        while (i < pathEdges.size()) {
            Edge edge = pathEdges.get(i);

            String mode = edge.getRoadId();
            String tripId = edge.getTripId();
            String routeId = tripId != null ? tripMap.get(tripId) : null;
            Road road = (routeId != null) ? roadMap.get(routeId) : null;
            String transportType = "WALK".equals(mode) ? "WALK" : (road != null ? road.getTransportType() : "UNKNOWN");
            String lineName = "WALK".equals(mode) ? "" : (road != null ? road.getRoadShortName() : "");
            String company = "WALK".equals(mode) ? "" : (routeId != null && routeId.contains("DELIJN")) ? "DELIJN" : (routeId != null && routeId.contains("SNCB")) ? "SNCB" : (routeId != null && routeId.contains("STIB")) ? "STIB" : (routeId != null && routeId.contains("TEC")) ? "TEC" : "";

            String startStopId = edge.getDepartureStopId();
            String startStopName = stopMap.get(startStopId) != null ? stopMap.get(startStopId).getStopName() : startStopId;
            String startTime = getEdgeTime(stopTimeMap, edge, startStopId);

            int j = i;
            String endStopId = edge.getArrivalStopId();
            String endStopName = stopMap.get(endStopId) != null ? stopMap.get(endStopId).getStopName() : endStopId;
            String endTime = getEdgeTime(stopTimeMap, edge, endStopId);

            while (j + 1 < pathEdges.size()) {
                Edge next = pathEdges.get(j + 1);
                String nextTripId = next.getTripId();
                String nextRouteId = nextTripId != null ? tripMap.get(nextTripId) : null;
                Road nextRoad = (nextRouteId != null) ? roadMap.get(nextRouteId) : null;
                String nextLineName = (nextRoad != null) ? nextRoad.getRoadShortName() : "";

                boolean sameLine = false;
                if ("WALK".equals(mode) && "WALK".equals(next.getRoadId())) {
                    sameLine = true;
                } else if (!"WALK".equals(mode) && mode != null && next.getRoadId() != null && lineName.equals(nextLineName) && !lineName.isEmpty()) {
                    sameLine = true;
                }
                if (!sameLine) break;
                endStopId = next.getArrivalStopId();
                endStopName = stopMap.get(endStopId) != null ? stopMap.get(endStopId).getStopName() : endStopId;
                endTime = getEdgeTime(stopTimeMap, next, endStopId);
                j++;
            }

            if ("WALK".equals(mode)) {
                float totalWalkDuration = 0f;
                for (int k = i; k <= j; k++) {
                    totalWalkDuration += pathEdges.get(k).getDuration();
                }
                String walkStartTime = Common.floatToHoraire(lastArrival);
                String walkEndTime = Common.floatToHoraire(lastArrival + totalWalkDuration);
                System.out.printf("Walk from %s (%s) to %s (%s)\n", startStopName, walkStartTime, endStopName, walkEndTime);
                lastArrival += totalWalkDuration;
            } else {
                System.out.printf("Take %s %s %s from %s (%s) to %s (%s)\n", company, transportType, lineName, startStopName, startTime, endStopName, endTime);
                if (endTime != null && !endTime.isEmpty()) {
                    String[] parts = endTime.split(":");
                    float arr = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]) + Integer.parseInt(parts[2]) / 60f;
                    lastArrival = arr;
                }
            }

            i = j + 1;
        }
    }

    /**
     * Executes the A* search and returns the list of edges representing the shortest path.
     * Prints a message if no path is found.
     * @return List of edges for the path, or null if not found
     */
    public List<Edge> doRequest() {
        List<Edge> pathEdges = aStar.shortestPathEdges();
        if (pathEdges == null || pathEdges.isEmpty()) {
            System.out.println("Aucun chemin trouvé.");
            return null;
        }
        return pathEdges;
    }

    /**
     * Helper to get the scheduled time for a stop in a given edge/trip.
     * @param stopTimeMap Map of tripId to StopTime
     * @param edge The edge (segment) of the path
     * @param stopId The stop ID to look up
     * @return Scheduled time as string, or empty if not found
     */
    private String getEdgeTime(HashMap<String, StopTime> stopTimeMap, Edge edge, String stopId) {
        if (edge.getTripId() == null) return "";
        StopTime stopTime = stopTimeMap.get(edge.getTripId());
        if (stopTime == null) return "";
        for (var entry : stopTime.getItinerary().entrySet()) {
            List<String> stopInfo = entry.getValue();
            if (stopInfo.get(0).equals(stopId)) {
                return stopInfo.get(1);
            }
        }
        return "";
    }
}
