package request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.Comparator;

import graph.Edge;
import graph.Graph;
import process.Stop;
import process.StopTime;
import process.Road;

/**
 * Implements the A* search algorithm for finding the optimal route in the transport network.
 * Handles preferences and penalties for transport modes, and computes heuristics based on coordinates.
 */
public class Astar {
    Graph graph;
    String startId;
    String goalId;
    float startTime;
    List<String> optionLst;
    HashMap<String, Edge> edgeFrom = new HashMap<>();
    HashMap<String, String> cameFrom = new HashMap<>();

    /**
     * Initializes the A* search with the graph, start/goal names, time, and options.
     * @param graph 
     * @param startName the departure stop
     * @param goalName the goal stop
     * @param startTime the time of the start
     * @param optionLst the list of the option
     */
    public Astar(Graph graph, String startName, String goalName, float startTime, List<String> optionLst) {
        this.graph = graph;
        this.startId = getStopIdsViaName(startName);
        this.goalId = getStopIdsViaName(goalName);
        this.startTime = startTime;
        this.optionLst = optionLst;
    }

    /**
     * Finds the stop ID corresponding to a given stop name (case-insensitive).
     */
    private String getStopIdsViaName(String stopName) {
        for (Stop stop : graph.getStopMap().values()) {
            if (stop.getStopName().equalsIgnoreCase(stopName)) {
                return stop.getStopId();
            }
        }
        return null;
    }

    /**
     * Returns a bonus (negative value) or malus (positive value) for a transport type based on user options.
     */
    private int getTransportBonusMalus(String type) {
        if (optionLst.contains("-" + type.toUpperCase())) {
            return -400;          
        }   
        if (optionLst.contains("-N" + type.toUpperCase())) {
            return 400;
        }
        return 0;
    }

    /**
     * Computes the shortest path from start to goal using A*.
     * Returns a list of stop IDs representing the path.
     */
    public List<String> shortestPath() {
        if (startId == null || goalId == null) return null;
        if (startId.equals(goalId)) {
            return List.of(startId);
        }

        HashSet<String> closeLst = new HashSet<>();
        HashMap<String, Float> gScore = new HashMap<>();
        HashMap<String, Float> fScore = new HashMap<>();
        PriorityQueue<String> openLst = new PriorityQueue<>(Comparator.comparingDouble(id -> fScore.getOrDefault(id, Float.POSITIVE_INFINITY)));
        HashSet<String> openSet = new HashSet<>();

        gScore.put(startId, this.startTime);
        fScore.put(startId, calculHeuristic(startId, goalId));
        openLst.add(startId);
        openSet.add(startId);

        while (!openLst.isEmpty()) {
            String currentId = openLst.poll();
            openSet.remove(currentId);

            if (currentId == null) break;

            if (goalId.equals(currentId)) return findPath(goalId, cameFrom);

            closeLst.add(currentId);

            List<Edge> neighbors = graph.getGraph().get(currentId);
            if (neighbors == null) continue;

            for (Edge edge : neighbors) {
                String neighborId = edge.getArrivalStopId();

                if (closeLst.contains(neighborId)) continue;

                float currentTime = gScore.getOrDefault(currentId, this.startTime);

                // Retrieve edge and trip information
                String roadId = edge.getRoadId();
                String tripId = edge.getTripId();
                StopTime stopTime = graph.getStopTimeMap().get(tripId);
                float edgeDepartureTime = -1;
                if (stopTime != null) {
                    String horaireStr = stopTime.getStopIdToTime().get(currentId);
                    if (horaireStr != null) {
                        edgeDepartureTime = common.Common.HoraireToFloat(horaireStr);
                    }
                }

                // Compute waiting time for public transport, or 0 for walking
                float waitingTime = 0f;
                if ("WALK".equals(roadId) || edgeDepartureTime == -1f) {
                    waitingTime = 0f;
                } else {
                    if (edgeDepartureTime < currentTime) continue;
                    waitingTime = edgeDepartureTime - currentTime;
                }

                // Total suposedly cost to reach the goal
                float tentativeG = currentTime + waitingTime + edge.getDuration();

                // Add of a bonus or a malus if the user prefer use a certain transport and avoid other
                String type = null;
                if (roadId != null && !"WALK".equals(roadId)) {
                    String routeId = graph.getTripMap().get(tripId);
                    if (routeId != null) {
                        Road road = graph.getRoadMap().get(routeId);
                        if (road != null) {
                            type = road.getTransportType();
                        }
                    }
                }

                // Apply user preferences (bonus/malus)
                int bonusMalus = 0;
                if (type != null) {
                    bonusMalus += getTransportBonusMalus(type);
                }

                // If this path to neighbor is better, record it
                if (tentativeG < gScore.getOrDefault(neighborId, Float.POSITIVE_INFINITY)) {
                    cameFrom.put(neighborId, currentId);
                    edgeFrom.put(neighborId, edge);
                    gScore.put(neighborId, tentativeG);
                    fScore.put(neighborId, tentativeG + calculHeuristic(neighborId, goalId) + bonusMalus);

                    if (!openSet.contains(neighborId)) {
                        openLst.add(neighborId);
                        openSet.add(neighborId);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Reconstructs the path from start to goal using the cameFrom map.
     */
    private List<String> findPath(String goalId, HashMap<String, String> cameFrom) {
        List<String> path = new ArrayList<>();
        String current = goalId;
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Returns a list of transport types available from a given stop.
     */
    public List<String> getTransportTypes(String stopId) {
        List<String> transportTypes = new ArrayList<>();
        List<Edge> edges = graph.getGraph().get(stopId);
        if (edges == null) {
            return transportTypes; 
        }
        for (Edge edge : edges) {
            String tripId = edge.getTripId();
            String roadId = graph.getTripMap().get(tripId);
            if (roadId != null) {
                String transportType = graph.getRoadMap().get(roadId).getTransportType();
                if (!transportTypes.contains(transportType)) {
                    transportTypes.add(transportType);
                }
            }
        }
        return transportTypes;
    }

    /**
     * Returns the fastest transport type from a list, based on a fixed priority.
     */
    public String getfastestTransportType(List<String> transportTypes) {
        List<String> priority = List.of("TRAIN", "METRO", "BUS", "TRAM");
        for (String preferredType : priority) {
            if (transportTypes.contains(preferredType)) {
                return preferredType;
            }
        }
        return "UNKNOWN"; 
    }

    /**
     * Returns the speed (in km/h) for a given transport type.
     */
    public int getSpeed(String type) {
        switch (type) {
            case "TRAIN":
                return 100;
            case "METRO":
                return 72;
            case "BUS":
                return 30;
            case "TRAM":
                return 20;
            default:
                return -1;
        }
    }

    /**
     * Heuristic function: estimates the time (in minutes) between two stops using their coordinates and the fastest available transport.
     */
    public float calculHeuristic(String departureId, String arrivalId) {
        List<String> transportTypes = getTransportTypes(departureId);
        String fastestTransportType = getfastestTransportType(transportTypes);

        int speed = getSpeed(fastestTransportType);

        if (speed == -1) return -1;

        Stop arrival = graph.getStopMap().get(arrivalId);
        Stop departure = graph.getStopMap().get(departureId);

        float arrivalLat = arrival.getStopLat(); 
        float arrivalLon = arrival.getStopLon();

        float departureLat = departure.getStopLat(); 
        float departureLon = departure.getStopLon();

        float d = (float) Math.sqrt(Math.pow((arrivalLat - departureLat), 2) + Math.pow((arrivalLon - departureLon), 2));
        float h = d / speed;

        return h;
    }

    /**
     * Returns the list of edges representing the shortest path found by A*.
     */
    public List<Edge> shortestPathEdges() {
        List<String> path = shortestPath();

        if (path == null || path.size() < 2) return null;

        List<Edge> correctEdges = new ArrayList<>();
        String current = path.get(path.size() - 1);

        while (cameFrom.containsKey(current)) {
            Edge edge = edgeFrom.get(current);

            if (edge != null) {
                correctEdges.add(edge);
            }
            current = cameFrom.get(current);
        }
        Collections.reverse(correctEdges);
        return correctEdges;
    }
}
