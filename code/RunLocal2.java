import java.util.*;

public class RunLocal2 {
    public static void main(String[] args)
    {
        ////////////////////////////////////////////////////////////////////////
        ////////////////////////// INITIALIZATION //////////////////////////////
        ////////////////////////////////////////////////////////////////////////

        // Get the initial feasible solution using heuristic construction
        LocalGraph graph = LocalGraph.readGraph(args[0]);
        //graph.printGraph();
        Set<Integer> tempOpt = getInitSolution(graph);
        Set<Integer> tempSolution = new HashSet<>(tempOpt);
        System.out.println(tempOpt.size());

        // Initialize the taboo list
        Set<Integer> tabooSet = new HashSet<>();
        Set<Node> tabooNodeSet = new HashSet<>();

        // Build initial partial solution
        // drop out a randomly selected vertex from the solution set of vertices
        Random rnd = new Random();
        int i = rnd.nextInt(tempSolution.size());
        tempSolution.remove(tempSolution.toArray()[i]);
        tabooSet.add(i);
        System.out.println(tempOpt.size());

        // Build a partial solution instance for the local search algorithm, using the result above
        Local2Solution partialSolution = new Local2Solution(tempSolution, graph);




        ////////////////////////////////////////////////////////////////////////
        /////////////////////////////// EXECUTION //////////////////////////////
        ////////////////////////////////////////////////////////////////////////

        long timeLimit = Long.parseLong(args[1]) * 1000000000L;
        long startTime = System.nanoTime();
        int iterTime = 0;
        //int dropNum = tempOpt.size() / 2;
        System.out.println(timeLimit);
        while(System.nanoTime() - startTime < timeLimit) {
            iterTime++;
            Double lastUncoveredDC = partialSolution.uncoveredDC;
            Node droppedNode = partialSolution.dropFirst(graph, tabooNodeSet);//DropNode(graph);
            //tabooNodeSet.add(droppedNode);
            Node addedNode = partialSolution.addFirst(graph, tabooNodeSet);//AddNode(graph);
            //tabooNodeSet.clear();
            //tabooNodeSet.add(droppedNode);
            //tabooNodeSet.add(addedNode);

            if(partialSolution.uncoveredDC == 0.0 && partialSolution.uncoveredEdge == 0) {
                tempOpt = partialSolution.partialSolution;
                tempSolution = new HashSet<>(partialSolution.partialSolution);
                while(tempSolution.size() == tempOpt.size()) {
                    i = rnd.nextInt(tempSolution.size());
                    tempSolution.remove(tempSolution.toArray()[i]);
                }

                tabooSet = new HashSet<>();
                tabooSet.add(i);
                partialSolution = new Local2Solution(tempSolution, graph);
            }

            if(droppedNode.nodeNumber == addedNode.nodeNumber || (partialSolution.uncoveredDC >= lastUncoveredDC && lastUncoveredDC != 0)) {
                // when dropped and added the same node, this means we get stuck in local optima
                if(tabooSet.size() == tempOpt.size()|| tabooSet.size() > 500) {
                    graph.refreshEdgeDC(iterTime); // recopute DC value for each edges
                    tabooSet = new HashSet<>();
                    partialSolution = new Local2Solution(partialSolution.partialSolution, graph);
                    int dropNum = tempOpt.size() / 2;
                    while(dropNum > 0) {
                        partialSolution.dropRandom(graph, rnd);
                        partialSolution.addRandom(graph, rnd);
                        dropNum--;
                        //System.out.println(partialSolution.partialSolution.size());
                    }
                } else {
                    tempSolution = new HashSet<>(tempOpt);
                    while(tempSolution.size() == tempOpt.size()) {
                        i = rnd.nextInt(tempSolution.size());
                        if(tabooSet.contains(i)) continue;
                        tempSolution.remove(tempSolution.toArray()[i]);
                    }

                    tabooSet.add(i);
                    //System.out.println("Taboo Size = "+tabooSet.size());
                    partialSolution = new Local2Solution(tempSolution, graph); // reconstruct the current solution
                    //System.out.println("Local Minima");
                    //graph.printGraph();
                }

            }
            //System.out.println(partialSolution.uncoveredDC);
             System.out.println("Time: " + (double) (System.nanoTime() - startTime) / 1000000000L + "\t Opt = " +  tempOpt.size() + "\t DC = " + partialSolution.uncoveredDC);
            // System.out.println("Uncovered Edge Number: " + partialSolution.uncoveredEdge);
        }

        System.out.println("Opt = " +  tempOpt.size());
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
