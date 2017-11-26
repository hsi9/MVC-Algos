import java.util.*;

public class Local2Solution {
    TreeSet<Node> insideSolution;
    TreeSet<Node> outsideSolution;
    Double uncoveredDC;
    Set<Integer> partialSolution;
    int uncoveredEdge;

    /*
     * construct a partial solution from an existing solution
     */
    Local2Solution(Set<Integer> tempSolution, LocalGraph graph) {

        // initialization
        this.insideSolution = new TreeSet<>();
        this.outsideSolution = new TreeSet<>();
        this.partialSolution = tempSolution;
        this.uncoveredEdge = 0;

        // add each node inside tempSolution to insideSolution
        // add each node outside tempSolution to outsideSolution
        for(int i = 0; i < graph.numNodes; i++) {
            if(tempSolution.contains(i)) {
                // if the (i+1)-th node of the current graph is in tempSolution
                // compute the cost value for this node
                graph.nodeList.get(i).cost = 0.0;
                //graph.nodeList.get(i).maxCost = 0.0;
                for(Map.Entry<Integer, Integer> neighbour : graph.getNeighbours(i).entrySet()) {
                    if(!tempSolution.contains(neighbour.getKey())) {
                        // if the neighbour is not inside the current solution
                        // remove the current node will make edge <current_node, current_neighbor> uncovered
                        // increment the cost of current node by the DC value of edge <current_node, current_neighbor>
                        graph.nodeList.get(i).cost += graph.edgeTable.get(Edge.toString(i,neighbour.getKey())).getDC();
                        graph.nodeList.get(i).costEdge++;
                        //graph.nodeList.get(i).maxCost = Math.max(graph.nodeList.get(i).maxCost, graph.edgeTable.get(Edge.toString(i,neighbour.getKey())).getDC());
                    }
                }
                // add the node to insideSolution
                this.insideSolution.add(graph.nodeList.get(i));
            } else {
                // if the (i+1)-th node of the current graph is not in tempSolution
                // compute the cost value for this node
                graph.nodeList.get(i).cost = 0.0;
                //graph.nodeList.get(i).maxCost = 0.0;
                for(Map.Entry<Integer, Integer> neighbour : graph.getNeighbours(i).entrySet()) {
                    if(!tempSolution.contains(neighbour.getKey())) {
                        // if the neighbour is not inside the current solution
                        // add the current node will make edge <current_node, current_neighbor> covered
                        // decrease the cost of current node by the DC value of edge <current_node, current_neighbor>
                        graph.nodeList.get(i).cost -= graph.edgeTable.get(Edge.toString(i,neighbour.getKey())).getDC();
                        graph.nodeList.get(i).costEdge--;
                        //graph.nodeList.get(i).maxCost = -Math.max(Math.abs(graph.nodeList.get(i).maxCost), graph.edgeTable.get(Edge.toString(i,neighbour.getKey())).getDC());
                    }
                }
                // add the node to outsideSolution
                this.outsideSolution.add(graph.nodeList.get(i));
            }
        }

        // compute the total DC value of uncovered edges
        int endNode1;
        int endNode2;
        this.uncoveredDC = 0.0;
        for(Map.Entry<String, Edge> wrappedEdge : graph.edgeTable.entrySet()) {
            endNode1 = wrappedEdge.getValue().node1;
            endNode2 = wrappedEdge.getValue().node2;
            if (!tempSolution.contains(endNode1) && !tempSolution.contains(endNode2)) {
                // if the current edge is uncovered edge, add its DC value to total DC
                this.uncoveredDC += wrappedEdge.getValue().getDC();
                this.uncoveredEdge++;
            }
        }
    }

    public Node DropNode(LocalGraph graph) {
        // drop the node with the lowest cost and store it in "node"
        Node node = this.insideSolution.pollFirst();

        // add the node to outsideSolution
        this.outsideSolution.add(node);

        // update the avgUncoveredDC and partial solution
        //this.uncoveredDC += node.cost;
        //this.uncoveredEdge += node.costEdge;
        this.partialSolution.remove(node.nodeNumber);

        // update the cost of the current node
        node.cost = -node.cost;
        node.costEdge = -node.costEdge;
        //node.maxCost = -node.maxCost;

        // update the cost value for the neighbors of the dropped node
        for(Map.Entry<Integer, Integer> neighbour : graph.getNeighbours(node.nodeNumber).entrySet()) {
            if(this.partialSolution.contains(neighbour.getKey())) {
                // if the neighbor is inside the current solution
                // add the cost of this neighbour by the DC value of edge <node.nodeNumber, neighbour>
                this.insideSolution.remove(graph.nodeList.get(neighbour.getKey()));
                graph.nodeList.get(neighbour.getKey()).cost += graph.edgeTable.get(Edge.toString(node.nodeNumber,neighbour.getKey())).getDC();
                graph.nodeList.get(neighbour.getKey()).costEdge++;
                this.insideSolution.add(graph.nodeList.get(neighbour.getKey()));
            } else {
                // if the neighbor is not in the current solution
                // subtract the cost of this neighbour by the DC value of edge <node.nodeNumber, neighbour>
                this.outsideSolution.remove(graph.nodeList.get(neighbour.getKey()));
                graph.nodeList.get(neighbour.getKey()).cost -= graph.edgeTable.get(Edge.toString(node.nodeNumber,neighbour.getKey())).getDC();
                graph.nodeList.get(neighbour.getKey()).costEdge--;
                this.outsideSolution.add(graph.nodeList.get(neighbour.getKey()));
                this.uncoveredEdge++;
                this.uncoveredDC += graph.edgeTable.get(Edge.toString(node.nodeNumber,neighbour.getKey())).getDC();
            }
        }

        // return the dropped node
        return node;
    }

    public Node AddNode(LocalGraph graph) {
        // drop the node with the lowest cost and store it in "node"
        Node node = this.outsideSolution.pollFirst();

        // add the node to insideSolution
        this.insideSolution.add(node);

        // update the avgUncoveredDC and partial solution
        //this.uncoveredDC += node.cost;
        //this.uncoveredEdge += node.costEdge;
        this.partialSolution.add(node.nodeNumber);

        // update the cost of the current node
        node.cost = -node.cost;
        node.costEdge = -node.costEdge;

        // update the cost value for the neighbors of the dropped node
        // also update the weight value for each chosen edge
        for(Map.Entry<Integer, Integer> neighbour : graph.getNeighbours(node.nodeNumber).entrySet()) {
            if(this.partialSolution.contains(neighbour.getKey())) {
                // if the neighbor is inside the current solution
                // subtract the cost of this neighbour by the DC value of edge <node.nodeNumber, neighbour>
                this.insideSolution.remove(graph.nodeList.get(neighbour.getKey()));
                graph.nodeList.get(neighbour.getKey()).cost -= graph.edgeTable.get(Edge.toString(node.nodeNumber,neighbour.getKey())).getDC();
                graph.nodeList.get(neighbour.getKey()).costEdge--;
                this.insideSolution.add(graph.nodeList.get(neighbour.getKey()));
            } else {
                // if the neighbor is not in the current solution
                // add the cost of this neighbour by the DC value of edge <node.nodeNumber, neighbour>
                this.outsideSolution.remove(graph.nodeList.get(neighbour.getKey()));
                graph.nodeList.get(neighbour.getKey()).cost += graph.edgeTable.get(Edge.toString(node.nodeNumber,neighbour.getKey())).getDC();
                graph.nodeList.get(neighbour.getKey()).costEdge++;
                this.outsideSolution.add(graph.nodeList.get(neighbour.getKey()));
                this.uncoveredEdge--;
                this.uncoveredDC -= graph.edgeTable.get(Edge.toString(node.nodeNumber,neighbour.getKey())).getDC();
            }
            // update the weight value for each chosen edge
            graph.edgeTable.get(Edge.toString(node.nodeNumber,neighbour.getKey())).weight++;
            graph.totalWeight++;
//            if(graph.edgeTable.get(Edge.toString(node.nodeNumber,neighbour.getKey())).weight > graph.maxWeight) {
//                graph.maxWeight = graph.edgeTable.get(Edge.toString(node.nodeNumber,neighbour.getKey())).weight;
//            }

            // update sumProbChosen value for each node
        }

        // return the added node
        return node;
    }

    /*
     * This method used to check if current solution is a valid vertex cover
     */
    public Boolean checkValidCover() {
        if(this.uncoveredEdge == 0) {
            return true;
        } else {
            return false;
        }
    }
}

