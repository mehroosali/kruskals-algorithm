import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Kruskal algorithm implementation to get a minimum spanning tree among the cities of Texas
 * @author Anshul Pardhi
 *
 */
public class Kruskals2 {

    private static final String DELIMITER = ",";
    private static HashMap<String, Integer> cityMap = new HashMap<>(); //Stores every city name and maps it to a unique integral value
    //because union and find of DisjSets require an integer argument
    private static List<List<Edge>> edges = new LinkedList<List<Edge>>(); //Stores all the edges of the graph from the adjacency list
    private static List<Edge> mstEdges = new ArrayList<>(); //Stores all the MST edges
    private static int NUM_VERTICES; //Total vertices of the graph

    /**
     * Inner class to represent an edge of the graph
     * @author Anshul Pardhi
     *
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
     * Reads a file to get dictionary words
     * @param filePath the file path
     * @return a BufferedReader object
     * @throws FileNotFoundException
     */
    public static BufferedReader readFile(String filePath) throws FileNotFoundException {

        InputStream fis = new FileInputStream(filePath);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        return new BufferedReader(isr);
    }

    /**
     * Parses CSV to create a list of edges and a map
     * @param br Input buffered reader object
     * @throws IOException
     */
    public void insert(BufferedReader br) throws IOException {

        String line;
        int count = 0;
        while ((line = br.readLine()) != null) {
            String[] currLineArr = line.split(DELIMITER);
            int len = currLineArr.length;
            if(len <= 0) {
                break;
            }
            List<Edge> list = new LinkedList<>();
            String initCity = currLineArr[0];
            cityMap.put(initCity, count);
            for(int i = 1; i < len; i += 2) {
                Edge edge = new Edge(initCity, currLineArr[i], Integer.parseInt(currLineArr[i + 1])); //Create a new edge
                list.add(edge);
            }
            edges.add(list);
            count++;
        }
        NUM_VERTICES = cityMap.size();
    }

    /**
     * Kruskal algorithm implementation using Priority Queue and Disjoint Set
     */
    public void kruskal() {

        int edgesAccepted = 0;
        DisjSets ds = new DisjSets(NUM_VERTICES);

        //Create a priority queue and sort it on the basis of distances
        PriorityQueue<Edge> pq = new PriorityQueue<>(new Comparator<Edge>() {
            @Override
            public int compare(Edge e1, Edge e2) {
                return e1.distance - e2.distance;
            }
        });
        getEdges(pq); //Populate the priority queue

        Edge e;

        while(edgesAccepted < NUM_VERTICES - 1) {
            e = pq.poll(); //Get the edge with minimum distance
            int startParent = ds.find(cityMap.get(e.startCity));
            int endParent = ds.find(cityMap.get(e.endCity));
            if(startParent != endParent) {
                //New edge for spanning tree found!
                edgesAccepted++;
                ds.union(startParent, endParent);
                mstEdges.add(e); //Add the edge to MST
            }
        }
    }

    /**
     * Prints the generated Minimum Spanning Tree
     */
    private void printMST() {

        int totalDistance = 0;
        int len = mstEdges.size();
        for(int i=0; i<len; i++) {
            totalDistance += mstEdges.get(i).distance;
            System.out.println(mstEdges.get(i).startCity + " -> " + mstEdges.get(i).endCity + " " + mstEdges.get(i).distance);
        }
        System.out.println("Total Distance: " + totalDistance);
    }

    /**
     * Populate the priority queue with unique edges
     * @param pq priority queue to populate
     */
    private void getEdges(PriorityQueue<Edge> pq) {

        int rows = edges.size();

        for(int i=0; i<rows; i++) {
            int cols = edges.get(i).size();
            for(int j=0; j<cols; j++) {
                Edge edge = edges.get(i).get(j);
                Edge reverseEdge = new Edge(edge.endCity, edge.startCity, edge.distance);
                if(!pq.contains(reverseEdge)) {
                    //Only add unique edges
                    pq.offer(edge);
                }
            }
        }
    }

    public static void main(String[] args) {

        Kruskals2 k = new Kruskals2();
        try {
            //Change the file path to your file location
            String filePath = "assn9_data.csv";
            BufferedReader br = readFile(filePath);
            k.insert(br); //Parse CSV, create edges and form a graph 
            br.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Kruskal algorithm to find the minimum spanning tree
        k.kruskal();
        k.printMST(); //Print the generated Minimum Spanning Tree
    }

}