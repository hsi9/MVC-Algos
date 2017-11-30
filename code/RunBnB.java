import java.util.*;
public class RunBnB {
    public static void main(String[] args) {
        ////////////////////////////////////////////////////////////////////////
        ////////////////////////// INITIALIZATION //////////////////////////////
        ////////////////////////////////////////////////////////////////////////

        // Get the initial feasible solution using heuristic construction
        BnBGraph graph = BnBGraph.readGraph(args[0]);

        // initialize the sub-problem stack
        Stack<SubProblem> problemStack = new Stack<>();

        // initialize the upperBound and bestSolution
        HashSet<Integer> bestSolution = new HashSet<>();
        //BoundValue upperBound = new BoundValue(getInitSolution(graph).size());
        BoundValue upperBound = new BoundValue(graph.numEdges);

        // create the initial problem
        SubProblem initialProblem = new SubProblem(bestSolution, upperBound, graph, bestSolution);

        // add the initial problem into the stack
        problemStack.push(initialProblem);

        ////////////////////////////////////////////////////////////////////////
        /////////////////////////////// EXECUTION //////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        long timeLimit = Long.parseLong(args[1]) * 1000000000L;
        long startTime = System.nanoTime();
        while((System.nanoTime() - startTime) < timeLimit && !problemStack.isEmpty()) {
            // firstly fetch a sub-problem from the problem stack
            SubProblem currentProblem = problemStack.pop();
            // expand the problem into 2 sub-problems
            SubProblem leftSubProblem = currentProblem.leftExpandSubProblem();
            SubProblem rightSubProblem = currentProblem.rightExpandSubproblem();

            // evaluate the 2 sub-problems
            int leftValue = leftSubProblem.evaluateSubProblem();
            int rightValue = rightSubProblem.evaluateSubProblem();

            // add the sub-problems to stack
            if(leftValue > 0 && rightValue > 0) {
                if(leftValue >= rightValue) {
                    problemStack.push(leftSubProblem);
                    problemStack.push(rightSubProblem);
                } else {
                    problemStack.push(rightSubProblem);
                    problemStack.push(leftSubProblem);
                }
            } else if(leftValue == -1 || rightValue == -1) {
                if(leftValue == -1) {
                    bestSolution = leftSubProblem.bestSolution;
                }
                if(rightValue == -1) {
                    bestSolution = rightSubProblem.bestSolution;
                }
            } else if(leftValue > 0) {
                problemStack.push(leftSubProblem);
            } else if(rightValue > 0) {
                problemStack.push(rightSubProblem);
            }
            System.out.println("\n The current upperbound is: " + upperBound.value);
            System.out.println("problem stack size = " + problemStack.size() + "\n");
        }
        System.out.println("Time used: " + (double) (System.nanoTime() - startTime)/1000000000L + " seconds");
        System.out.println("Opt = " + upperBound.value);
        System.out.println(bestSolution);
    }

    /*
     * This function is used to get initial solution through heuristic construction algorithm
     */
    public static Set<Integer> getInitSolution(BnBGraph graph) {
        ApproxAlgo approxAlgo = new ApproxAlgo(graph);
        approxAlgo.runAlgo(graph);
        return approxAlgo.mvc;
    }
}
