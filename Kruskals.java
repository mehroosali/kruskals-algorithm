import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 @author Mehroos Ali (mxa200089)
 */
public class Kruskals {

    //Map of every city in the adjacency list with its unique number.
    private final HashMap<String, Integer> cityMap = new HashMap<>();
    //2-D list to store all the edges of the graph from the adjacency list.
    private final List<List<Edge>> adjacency_edges = new LinkedList<>();
    private final List<Edge> mst_edges = new ArrayList<>(); //list to store all the MST edges
    private int TOTAL_VERTICES; //Total number of vertices of MST graph

    /**
     * private inner class to represent an edge of the graph.
     */
    private static class Edge {
        String startCity;
        String endCity;
        int distance;
        Edge(String startCity, String endCity, int distance) {
            this.startCity = startCity;
            this.endCity = endCity;
            this.distance = distance;
        }
    }

    /**
     * method to read adjacency list from CSV file line by line and build a 2D list of adjacency edges.
     * @param fileName name of the input file.
     */
    private void readAndBuildGraph(String fileName) {
        String line;
        int count = 0;
        try {
           FileReader fr = new FileReader(fileName);
           BufferedReader br = new BufferedReader(fr);

           while ((line = br.readLine()) != null) {
               String[] currLineArr = line.split(",");
               int len = currLineArr.length;
               if(len <= 0) {
                   break;
               }
               List<Edge> list = new LinkedList<>();
               String initCity = currLineArr[0];
               cityMap.put(initCity, count);
               for(int i = 1; i < len; i += 2) {
                   Edge edge = new Edge(initCity, currLineArr[i], Integer.parseInt(currLineArr[i + 1]));
                   list.add(edge);
               }
               adjacency_edges.add(list);
               count++;
           }
            TOTAL_VERTICES = cityMap.size();
            br.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * method to find MST using Kruskals algorithm.
     */
    private void findMST() {

        int accepted_edges = 0;
        DisjSets ds = new DisjSets(TOTAL_VERTICES);

        //Create a priority queue and sort it on the basis of distances
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.distance));
        buildPriorityQueue(pq); //Populate the priority queue
        Edge e;

        while(accepted_edges < TOTAL_VERTICES - 1) {
            e = pq.poll(); //Get the edge with minimum distance
            assert e != null;
            int startParent = ds.find(cityMap.get(e.startCity));
            int endParent = ds.find(cityMap.get(e.endCity));
            if(startParent != endParent) {
                //New edge for spanning tree found!
                accepted_edges++;
                ds.union(startParent, endParent);
                mst_edges.add(e); //Add the edge to MST
            }
        }
    }

    /**
     * method to build a min priority queue based on city distance.
     */
    private void buildPriorityQueue(PriorityQueue<Edge> pq) {
        for (List<Edge> adjacency_edge : adjacency_edges) {
            for (Edge edge : adjacency_edge) {
                Edge reverseEdge = new Edge(edge.endCity, edge.startCity, edge.distance);
                if (!pq.contains(reverseEdge)) {
                    //Only add unique edges
                    pq.offer(edge);
                }
            }
        }
    }

    /**
     * method to print MST.
     */
    private void printMST() {
        int totalDistance = 0;
        for (Edge mst_edge : mst_edges) {
            totalDistance += mst_edge.distance;
            System.out.println(mst_edge.startCity + " -> " + mst_edge.endCity + " " + mst_edge.distance);
        }
        System.out.println("Total Distance: " + totalDistance);
    }

    public static void main(String[] args) {
        String fileName = "assn9_data.csv";
        Kruskals k = new Kruskals();
        k.readAndBuildGraph(fileName);
        k.findMST();
        k.printMST();
    }

}
