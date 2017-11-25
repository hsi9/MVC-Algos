import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Local2 {
    public static void main(String[] args)
    {
        ////////////////////////////////////////////////////////////////////////
        ////////////////////////// INITIALIZATION //////////////////////////////
        ////////////////////////////////////////////////////////////////////////

        // Get the initial feasible solution using heuristic construction
        LocalGraph graph = LocalGraph.readGraph(args[0]);
        Set<Integer> tempSolution = getInitSolution(graph);

        // Build initial partial solution
        // drop out a randomly selected vertex from the solution set of vertices
        Random rnd = new Random();
        int i = rnd.nextInt(tempSolution.size());
        tempSolution.remove(i);

        // Build a partial solution instance for the local search algorithm, using the result above
        Local2Solution partialSolution = new Local2Solution(tempSolution, graph);

        // Initialize the taboo list
        List<Node> taboo = new ArrayList<>();


        ////////////////////////////////////////////////////////////////////////
        /////////////////////////////// EXECUTION //////////////////////////////
        ////////////////////////////////////////////////////////////////////////

        long timeLimit = 600 * 1000000000L;
        long startTime = System.nanoTime();
        while(System.nanoTime() - startTime < timeLimit) {
            Node droppedNode = partialSolution.DropNode(graph);
            Node addedNode = partialSolution.AddNode(graph);
            if(partialSolution.checkValidCover()) {
                tempSolution = partialSolution.partialSolution;
                i = rnd.nextInt(tempSolution.size());
                tempSolution.remove(i);
                partialSolution = new Local2Solution(tempSolution, graph);
            }

            if(droppedNode.nodeNumber == addedNode.nodeNumber) {
                // when dropped and added the same node, this means we get stuck in local optima
                graph.refreshEdgeDC(); // recopute DC value for each edges
                partialSolution = new Local2Solution(tempSolution, graph); // reconstruct the current solution
            }
        }

        System.out.println(tempSolution.size());
    }

    /*
     * This function is used to get initial solution through heuristic construction algorithm
     */
    public static Set<Integer> getInitSolution(LocalGraph graph) {
        ApproxAlgo approxAlgo = new ApproxAlgo(graph);
        approxAlgo.runAlgo(graph);
        return approxAlgo.mvc;
    }
}
