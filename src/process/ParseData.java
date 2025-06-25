package process;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import common.Common;

public class ParseData {
    /*
    * Parses a single line from a CSV file, taking into account quoted fields with commas
    */
    private String[] parseCsvLine(String line) {
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replaceAll("^\"|\"$", "");
        }
    
        return parts;
    }

    /*
    * Parses a CSV file containing road data and fills the roadMap with Road objects
    */
    void parseRoad(String path, HashMap<String, Road> roadMap) { 
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = parseCsvLine(line);
                
                try {
                    Road road = new Road(data[0], data[1], data[2], data[3]);
                    roadMap.put(data[0], road);
                } catch (NumberFormatException e) {
                    System.err.println("Road : Erreur de format pour les coordonnées dans la ligne : " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  
        }
    }

    /*
    * Parses a CSV file containing stop_times data and fills the stopTimeMap with StopTime objects
    */
    void parseStopTime(String path, HashMap<String, StopTime> stopTimeMap, float horraire) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                try {
                    int stopSequence = Integer.parseInt(data[3]);
                    String tripId = data[0];
                    String stopId = data[2];
                    String departureTime = data[1];

                    // Skip stops before the given time threshold
                    if (Common.HoraireToFloat(departureTime) < horraire) continue;

                    // Retrieve or create StopTime object and update it
                    StopTime stopTime = stopTimeMap.get(tripId);
                    if (stopTime == null) {
                        stopTime = new StopTime(tripId, departureTime, stopId);
                        stopTimeMap.put(tripId, stopTime);
                    } 
                    
                    stopTime.addToItinerary(stopSequence, stopId, departureTime);
                } catch (NumberFormatException e) {
                    System.err.println("StopTime : Erreur de format pour les coordonnées dans la ligne : " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * Parses a CSV file containing stop data and fills the stopMap with Stop objects
    */
    void parseStop(String path, HashMap<String, Stop> stopMap) { 
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            br.readLine(); // Skip header line
            
            while ((line = br.readLine()) != null) {
                String[] data = parseCsvLine(line);
                
                try {
                    float stopLat = Float.parseFloat(data[2]);
                    float stopLon = Float.parseFloat(data[3]);
                    Stop stop = new Stop(data[0], data[1], stopLat, stopLon);
                    stopMap.put(data[0], stop);
                } catch (NumberFormatException e) {
                    System.err.println("Stop : Erreur de format pour les coordonnées dans la ligne : " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  
        }
    }

    /*
    * Parses a CSV file containing trip data and fills the tripMap
    */
    void parseTrip(String path, HashMap<String, String> tripMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                
                try {
                    tripMap.put(data[0], data[1]);
                } catch (NumberFormatException e) {
                    System.err.println("Trip : Erreur de format pour les coordonnées dans la ligne : " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  
        }
    }

    // Main method to parse all data files in the correct order
    public void parseMain(String[] paths, HashMap<String, Road> roadMap, HashMap<String, String> tripMap, 
        HashMap<String, Stop> stopMap, HashMap<String, StopTime> stopTimeMap, String horraire) {
        parseRoad(paths[0], roadMap);
        parseStop(paths[2], stopMap);
        parseTrip(paths[3], tripMap);
        parseStopTime(paths[1], stopTimeMap, Common.HoraireToFloat(horraire));
    }
    
}
