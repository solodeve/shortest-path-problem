import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import graph.Graph;
import process.ParseData;
import process.Road;
import process.Stop;
import process.StopTime;

import request.Request;

class Main {
    public static void main(String[] args) {

        if (args.length < 3) {
            System.out.println("Usage: java Main <start> <goal> <heure> [options]");
            System.exit(1);
        }

        String start = args[0];
        String goal = args[1];
        String horraire = args[2];

        List<String> optionLst = new ArrayList<>();

        for (int i = 3; i < args.length; i++) {
            optionLst.add(args[i]);
        }

        HashMap<String, Road> roadMap = new HashMap<>();
        HashMap<String, String> tripMap = new HashMap<>(); 
        HashMap<String, Stop> stopMap = new HashMap<>();
        HashMap<String, StopTime> stopTimeMap = new HashMap<>();

        String[] deLijn = {"GTFS/DELIJN/routes.csv", 
                           "GTFS/DELIJN/stop_times.csv", 
                           "GTFS/DELIJN/stops.csv", 
                           "GTFS/DELIJN/trips.csv"};
        
        String[] stib = {"GTFS/STIB/routes.csv", 
                         "GTFS/STIB/stop_times.csv", 
                         "GTFS/STIB/stops.csv", 
                         "GTFS/STIB/trips.csv"};

        String[] sncb = {"GTFS/SNCB/routes.csv", 
                         "GTFS/SNCB/stop_times.csv", 
                         "GTFS/SNCB/stops.csv", 
                         "GTFS/SNCB/trips.csv"};

        String[] tec = {"GTFS/TEC/routes.csv", 
                         "GTFS/TEC/stop_times.csv", 
                         "GTFS/TEC/stops.csv", 
                         "GTFS/TEC/trips.csv"};

        System.out.println("-------------------------------------\n  Start of data object conversion\n-------------------------------------");
        long startTime = System.nanoTime();

        ParseData parselesdatas = new ParseData();
        parselesdatas.parseMain(deLijn, roadMap, tripMap, stopMap, stopTimeMap, horraire);
        parselesdatas.parseMain(stib, roadMap, tripMap, stopMap, stopTimeMap, horraire);
        parselesdatas.parseMain(sncb, roadMap, tripMap, stopMap, stopTimeMap, horraire);
        parselesdatas.parseMain(tec, roadMap, tripMap, stopMap, stopTimeMap, horraire);

        long endTime = System.nanoTime();    
        long duration = endTime - startTime; 
        
        double durationInSec = duration / 1_000_000_000;
        double durationInMilli = duration / 1_000_000;

        System.out.printf("- Data parsed in %.9f sec.\n", durationInSec);
        System.out.printf("- Data parsed in %.9f miliSec.\n", durationInMilli);

        System.out.println("-------------------------------------\n Start of the creation of the graph\n-------------------------------------");
        startTime = System.nanoTime();

        Graph graph = new Graph(roadMap, tripMap, stopMap, stopTimeMap);
        graph.createGraph();

        endTime = System.nanoTime();    
        duration = endTime - startTime; 
        
        durationInSec = duration / 1_000_000_000;
        durationInMilli = duration / 1_000_000;

        System.out.printf("- Graph created in %.9f sec.\n", durationInSec);
        System.out.printf("- Graph created in %.9f miliSec.\n", durationInMilli);

        System.out.println("-------------------------------------\n       Start of the request\n-------------------------------------");
        startTime = System.nanoTime();

        Request request = new Request(start, goal, horraire, graph, optionLst);
        List<graph.Edge> path = request.doRequest();
        
        endTime = System.nanoTime();    
        duration = endTime - startTime; 
        
        durationInSec = duration / 1_000_000_000;
        durationInMilli = duration / 1_000_000;

        System.out.printf("- Request done in %.9f sec.\n", durationInSec);
        System.out.printf("- Request done in %.9f miliSec.\n", durationInMilli);

        System.out.println("-------------------------------------\n            Shortest path\n-------------------------------------");
        request.view(path);

        System.out.println(" ");
    }
}