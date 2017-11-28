import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalGraph extends Graph {
    int numEdges;
    int totalWeight;
    List<Node> nodeList;
    HashMap<String, Edge> edgeTable;

    LocalGraph(int n, int m) {
        super(n);
        this.numEdges = m;
        this.totalWeight = m;
        this.nodeList = new ArrayList<>(n);
        this.edgeTable = new HashMap<>(); // hash table used to store DC value of each edge

        // initialize the nodeList with all degree be 0
        for(int i = 0; i < n; i++) {
            this.nodeList.add(i, new Node(i));
        }
    }

    @Override
    public void addEdge(int node1, int node2, int weight) {

        // get the adjacency list of two endpoints of this edge
        HashMap<Integer, Integer> first = graph.get(node1);
        HashMap<Integer, Integer> second = graph.get(node2);

        // add an edge to edge table if not exist in the edge table
        String edgeString = Edge.toString(node1, node2);
        if(!this.edgeTable.containsKey(edgeString)) {
            Edge edge = new Edge(node1, node2);
            edge.setProbChosen(1.0 / this.numEdges);
            this.edgeTable.put(edgeString, edge);
            // if they already exist in each other's adjacency list, do nothing
            // if not, add them to each other's adjacency list and update their degree
            if(!first.containsKey(node2)) {
                first.put(node2, weight);
                this.addNodeDegree(node1, 1);
                this.addNodeProbChosen(node1, edge.getProbChosen());

            }
            if(!second.containsKey(node1)) {
                second.put(node1, weight);
                this.addNodeDegree(node2, 1);
                this.addNodeProbChosen(node2, edge.getProbChosen());
            }
        }
    }

    public void addNodeDegree(int nodeNumber, int increment) {
        this.nodeList.get(nodeNumber).degree += increment;
    }

    public void addNodeProbChosen(int nodeNumber, double increment) {
        if(this.nodeList.get(nodeNumber).sumProbChosen == Double.NaN) {
            this.nodeList.get(nodeNumber).sumProbChosen = increment;
        } else {
            this.nodeList.get(nodeNumber).sumProbChosen += increment;
        }
    }

    /*
     * initialize the DC value of each edge
     */
    public void initEdgeDC() {
        for(Map.Entry<String, Edge> wrappedEdge : this.edgeTable.entrySet()) {
            Edge edge = wrappedEdge.getValue();
            Node node1 = this.nodeList.get(edge.getNode1());
            Node node2 = this.nodeList.get(edge.getNode2());
            double DC = 1 - (node1.getSumProbChosen() + node2.getSumProbChosen()) * 0.5;
            edge.setDC(DC);
        }
    }

    public void refreshEdgeDC(int totalChosenTime) {
        // reset the sumProbChosen for each node in nodeList
        for(int i = 0; i < this.numNodes; i++) {
            this.nodeList.get(i).sumProbChosen = 0.0;
        }

        // recompute the value of ProbChosen for each Edge
        Edge edge;
        for(Map.Entry<String, Edge> wrappedEdge: this.edgeTable.entrySet()) {
            edge = wrappedEdge.getValue();
            edge.setProbChosen((double) edge.weight / this.totalWeight);
            //edge.setDC(-edge.getDC());
            edge.setDC(1 - ((double) edge.weight / totalChosenTime));
            //this.nodeList.get(edge.getNode1()).sumProbChosen += edge.probChosen;
            //this.nodeList.get(edge.getNode2()).sumProbChosen += edge.probChosen;
        }

        // recompute DC value for each edges
        //this.initEdgeDC();
    }

    public static LocalGraph readGraph(String graphFilePath) {

        LocalGraph graph = null;
        BufferedReader br;
        String line;
        String[] tokens;
        try
        {
            br = new BufferedReader(new FileReader(graphFilePath));
            line = br.readLine();
            tokens = line.split(" ");
            graph = new LocalGraph(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
            int i = 0;
            while((line = br.readLine())!= null)
            {
                if(line.equals(""))
                    continue;

                tokens = line.split(" ");
                for(String token:tokens)
                {
                    graph.addEdge(i, Integer.parseInt(token)-1,1);
                }
                i++;
                //graph.printGraph();
                //System.out.println(line);
            }

            //graph.printGraph();
            br.close();
            //bw.close();
            graph.initEdgeDC();
        }

        catch(Exception e)
        {
            System.out.println("Check the addEdge method or the printGraph method.")	;
        }

        return graph;
    }

    @Override
    public void printGraph() {
        System.out.println("node1\tnode2\tweight\tprobChosen\tDC");
        for(Map.Entry<String, Edge> wrappedEdge : this.edgeTable.entrySet()) {
            Edge edge = wrappedEdge.getValue();
            System.out.print(Integer.toString(edge.getNode1()) + "\t");
            System.out.print(Integer.toString(edge.getNode2()) + "\t");
            System.out.print(Integer.toString(edge.getWeight()) + "\t");
            System.out.print(Double.toString(edge.getProbChosen()) + "\t");
            System.out.println(Double.toString(edge.getDC()));
        }
    }
}

class Node implements Comparable<Node> {
    int nodeNumber;
    int degree;
    Double sumProbChosen;
    Double cost; // namely, summation of DC values of its uncovered adjacent edges
    int costEdge;

    Node(int nodeNumber) {
        this.nodeNumber = nodeNumber;
        this.degree = 0;
        this.sumProbChosen = 0.0;
        this.cost = Double.NaN;
        this.costEdge = 0;
    }

    @Override
    public String toString() {
        return nodeNumber + "";
    }

    @Override
    public int compareTo(Node arg0){
        int compare=Double.compare(this.cost, arg0.cost);
        if(compare==0){
            compare=Double.compare(this.sumProbChosen, arg0.sumProbChosen);
        }
        return compare;
    }

    public double getSumProbChosen() {
        return this.sumProbChosen;
    }
}

class Edge {
    int node1;
    int node2;
    int weight; // weight value;
    double probChosen; // probability of being chosen to cover
    double DC; //  Difficulty of Covering

    Edge(int node1, int node2) {
        this.node1 = node1;
        this.node2 = node2;
        this.weight = 1;
        this.probChosen = 0.0;
        this.DC = -1;
    }

    public static String toString(int node1, int node2) {
        String nodeString1 = Integer.toString(Math.min(node1, node2));
        String nodeString2 = Integer.toString(Math.max(node1, node2));
        String edgeString = nodeString1 + "#" + nodeString2;
        return edgeString;
    }

    public void setProbChosen(double probChosen) {
        this.probChosen = probChosen;
    }

    public void setDC(double DC) {
        this.DC = DC;
    }

    public double getProbChosen() {
        return this.probChosen;
    }

    public int getNode1() {
        return this.node1;
    }

    public int getNode2() {
        return this.node2;
    }

    public int getWeight() {
        return this.weight;
    }

    public double getDC() {
        return this.DC;
    }
}
