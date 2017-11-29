import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class BnBGraph extends Graph implements Cloneable {
    int numEdges;
    TreeSet<NodeDegree> nodeSet;
    List<NodeDegree> nodeList;
    HashMap<String, Edge> edgeTable;

    BnBGraph(int n, int m) {
        super(n);
        this.numEdges = m;
        this.nodeSet = new TreeSet<>();
        this.nodeList = new ArrayList<>(n);
        this.edgeTable = new HashMap<>(); // hash table used to store DC value of each edge

        // initialize the nodeList with all degree be 0
        // also initialize the nodeSet
        NodeDegree tempNode;
        for(int i = 0; i < n; i++) {
            tempNode = new NodeDegree(i, 0);
            this.nodeList.add(i, tempNode);
            this.nodeSet.add(tempNode);
        }
    }

    @Override
    public BnBGraph clone() {
        //System.out.println("the graph to clone");
        //this.printGraph();
        //System.out.println("/////////");
        BnBGraph newGraph = new BnBGraph(this.numNodes, this.numEdges);
        newGraph.graph = new HashMap<>();
        newGraph.nodeSet = new TreeSet<>();
        newGraph.nodeList = new ArrayList<>();
        for(int i = 0; i < this.nodeList.size(); i++) {
            newGraph.nodeList.add(new NodeDegree(i, 0));
        }
        for(Integer nodeNumber : this.graph.keySet()) {
            newGraph.graph.put(nodeNumber, new HashMap<>());
            this.nodeSet.add(nodeList.get(nodeNumber));
        }
        for(Map.Entry<String, Edge> edge : this.edgeTable.entrySet()) {
            Edge edgeItem = edge.getValue();
            newGraph.addEdge(edgeItem.getNode1(), edgeItem.getNode2(), edgeItem.getWeight());
        }
        return newGraph;
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
            this.edgeTable.put(edgeString, edge);
            // if they already exist in each other's adjacency list, do nothing
            // if not, add them to each other's adjacency list and update their degree
            if(!first.containsKey(node2)) {
                first.put(node2, weight);
                this.nodeSet.remove(nodeList.get(node1));
                this.addNodeDegree(node1, 1);
                this.nodeSet.add(nodeList.get(node1));
            }


            if(!second.containsKey(node1)) {
                second.put(node1, weight);
                this.nodeSet.remove(nodeList.get(node2));
                this.addNodeDegree(node2, 1);
                this.nodeSet.add(nodeList.get(node2));
            }
        }
        //System.out.println("Cloned Graph ########");
        //this.printGraph();
    }

    public void deleteEdge(int node1, int node2) {
        // firstly check whether the edge exists in the graph
        // if not, print error messages
        // else, delete the edge from edge table
        String edgeString = Edge.toString(node1, node2);
        if(!this.edgeTable.containsKey(edgeString)) {
            System.out.println("Error in deleteEdge: the edge to delete does not exist!");
        } else {
            this.edgeTable.remove(edgeString);
            this.numEdges--;
        }

        // if the node exists, get the adjacency list of two endpoints of this edge
        // also delete from each other's adjacency list if exist
        // also delete the node degree by 1
        if(this.graph.containsKey(node1)) {
            HashMap<Integer, Integer> first = this.graph.get(node1);
            if(first.containsKey(node2)) {
                first.remove(node2);
                this.nodeSet.remove(nodeList.get(node1));
                this.decreaseNodeDegree(node1, 1);
                this.nodeSet.add(nodeList.get(node1));
            }
        }

        if(this.graph.containsKey(node2)) {
            HashMap<Integer, Integer> second = this.graph.get(node2);
            if(second.containsKey(node1)) {
                second.remove(node1);
                this.nodeSet.remove(nodeList.get(node2));
                this.decreaseNodeDegree(node2, 1);
                this.nodeSet.add(nodeList.get(node2));
            }
        }
    }

    /*
     * Delete from the current graph a node which is specified by its node number
     */
    public NodeDegree deleteNode(int nodeNumber) {
        // first check whether the node exists in the graph
        //this.printGraph();
        if(!this.graph.containsKey(nodeNumber)) {
            System.out.println("Error in deleteNode: the node to delete does not exist");
        }

        // firsly delete all edges incident to this node in the graph
        HashMap<Integer, Integer> neighbours = this.graph.get(nodeNumber);
        HashSet<Integer> neighborNumbers = new HashSet<>();
        for(Map.Entry<Integer, Integer> neighbor : neighbours.entrySet()) {
            neighborNumbers.add(neighbor.getKey());
        }
        for(Integer neighborNumber : neighborNumbers) {
            this.deleteEdge(nodeNumber, neighborNumber);
        }

        // then delete the node from the graph's NodeList and graph
        // also decrease the number of nodes by 1
        //this.printGraph();
        this.numNodes--;
        this.graph.remove(nodeNumber);
        this.nodeSet.remove(nodeList.get(nodeNumber));
        //this.printGraph();
        //System.out.println(this.nodeSet);
        //System.out.println(this.nodeList);
        return this.nodeList.get(nodeNumber);
    }

    public void addNodeDegree(int nodeNumber, int increment) {

        this.nodeList.get(nodeNumber).degree += increment;
    }

    public void decreaseNodeDegree(int nodeNumber, int decrement) {

        this.nodeList.get(nodeNumber).degree -= decrement;
    }

    public static BnBGraph readGraph(String graphFilePath) {

        BnBGraph graph = null;
        BufferedReader br;
        String line;
        String[] tokens;
        try
        {
            br = new BufferedReader(new FileReader(graphFilePath));
            line = br.readLine();
            tokens = line.split(" ");
            graph = new BnBGraph(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
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
            }

            br.close();
        }

        catch(Exception e)
        {
            System.out.println("Check the addEdge method or the printGraph method.")	;
        }

        return graph;
    }

    @Override
    public void printGraph()
    {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for(Map.Entry<Integer,HashMap<Integer,Integer>> node : this.graph.entrySet())
        {
            map = node.getValue();
            System.out.println(node.getKey()+" : "+map);
        }
    }
}


