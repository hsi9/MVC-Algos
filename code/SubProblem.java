import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
//import src.main.java.com.joptimizer.algebra.*;
//import src.main.java.com.joptimizer.exception.*;
//import src.main.java.com.joptimizer.functions.LinearMultivariateRealFunction;
//import src.main.java.com.joptimizer.optimizers.*;
//import src.main.java.com.joptimizer.solvers.*;
//import src.main.java.com.joptimizer.util.*;
import scpsolver.problems.LinearProgram;
import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;

class SubProblem {
    int lowerBound; // the best solution can be found along this branch
    BoundValue upperBound; // the current best solution size found for the original problem
    int inSolutionNodeNumber; // number of Vetices already included in the current partial solution
    HashSet<Integer> inSolutionNodeSet;
    //HashSet<Integer> excludeNodeSet; // the nodes chosen by the partial solution not to included into final solution
    BnBGraph subGraph; // the subGraph of the original graph used to represent the sub-problem to solve
    HashSet<Integer> bestSolution;

    SubProblem(HashSet<Integer> inSolutionNodeSet, BoundValue upperBound, BnBGraph subGraph, HashSet<Integer> bestSolution) {
        this.inSolutionNodeNumber = inSolutionNodeSet.size();
        this.inSolutionNodeSet = inSolutionNodeSet;
        this.lowerBound = 0;
        this.upperBound = upperBound;
        //this.excludeNodeSet = new HashSet<>(excludeNodeSet);
        this.subGraph = subGraph;
        this.bestSolution = bestSolution;
    }

    /*
     * Expand the sub-problem to a new sub-problem by including the highest-degree-node into current solution
     */
    public SubProblem leftExpandSubProblem() {
        //System.out.println("Begin Left Expansion");
        // find the splitNode as the highest-degree node not in the current exludeNodeSet
        NodeDegree splitNode = this.subGraph.nodeSet.last();
        //System.out.println(splitNode.nodeNumber);

        // find the neighbors of the split node
        HashMap<Integer, Integer> neighbours = new HashMap<>(this.subGraph.getNeighbours(splitNode.nodeNumber));
        HashSet<Integer> deleteNodesSet = new HashSet<>();
        for(Map.Entry<Integer, Integer> neighbour : neighbours.entrySet()) {
            //System.out.println("Degree of neighbor:" + neighbour.getKey() + "-" + this.subGraph.nodeList.get(neighbour.getKey()).degree);
            if(this.subGraph.nodeList.get(neighbour.getKey()).degree == 1) {
                deleteNodesSet.add(neighbour.getKey());
            }

        }

        // get the partial solution for the expanded sub-problem
        HashSet<Integer> leftInSolutionNodeSet = new HashSet<>(this.inSolutionNodeSet);
        leftInSolutionNodeSet.add(splitNode.nodeNumber);
        //int leftInSolutionNodeNumber = this.inSolutionNodeNumber + 1;

        // get the subGraph for the sub-problem
        BnBGraph leftSubGraph = this.subGraph.clone();

        // System.out.println(splitNode.nodeNumber);

        // delete the split node from the subGraph
        leftSubGraph.deleteNode(splitNode.nodeNumber);

        // also delete all the neighbors of split node whose degree is only 1

        for(Integer nodeNumber : deleteNodesSet) {
            leftSubGraph.deleteNode(nodeNumber);
            //System.out.println("Deleting neighbor:" + nodeNumber);
        }

        // build the sub-problem
        SubProblem leftSubProblem = new SubProblem(leftInSolutionNodeSet, this.upperBound, leftSubGraph, this.bestSolution);
        //System.out.println("Result of Left Expansion");
        //leftSubProblem.subGraph.printGraph();
        return leftSubProblem;
    }

    public SubProblem rightExpandSubproblem() {
        //System.out.println("Begin Right Expansion");
        // find the splitNode
        NodeDegree splitNode = this.subGraph.nodeSet.last();

        // find the neighbors of the split node
        HashMap<Integer, Integer> neighbours = this.subGraph.getNeighbours(splitNode.nodeNumber);

        // get partial solution for the expanded sub-problem
        // int rightInSolutionNodeNumber = this.inSolutionNodeNumber + splitNode.degree;
        HashSet<Integer> rightInSolutionNodeSet = new HashSet<>(this.inSolutionNodeSet);
        for(Map.Entry<Integer, Integer> neighbour : neighbours.entrySet()) {
            rightInSolutionNodeSet.add(neighbour.getKey());
        }

        // get the subGraph for the sub-problem
        BnBGraph rightSubGraph = this.subGraph.clone();

        // delete all neighbours from the subGraph
        for(Map.Entry<Integer, Integer> neighbour : neighbours.entrySet()) {
            rightSubGraph.deleteNode(neighbour.getKey());
        }

        // also delete the current split node because it becomes isolated after deleting all its neighbours
        rightSubGraph.deleteNode(splitNode.nodeNumber);

        // build the sub-problem
        SubProblem rightSubProblem = new SubProblem(rightInSolutionNodeSet, this.upperBound, rightSubGraph, this.bestSolution);
        //System.out.println("Result of Right Expansion");
        //rightSubProblem.subGraph.printGraph();
        //System.out.println("Edge Number = " + rightSubProblem.subGraph.numEdges);
        return rightSubProblem;
    }

    /*
     * based on the current partial solution, check whether the subproblem deserves exploration
     * If the computed lower bound of the sub-problem is higher than the upperbound, then discard the subproblem
     * If the computed lower bound of the sub-problem is lower than the upperbound, then the sub-prorblem is promising
     * If the current sub-problem is
     */
    public int evaluateSubProblem() {
        // firstly update the upperBound and lowerbound
        boolean upBoundState = this.checkUpperBound();
        //System.out.println("Edge Number = " + this.subGraph.numEdges);
        if(this.subGraph.numEdges == 0) {
            //  current partial solution is already a feasible solution to MVC, do not add it into stack
            if(upBoundState == true) {
                // in this case, you need to update the current bestSolution with its super-problem
                this.bestSolution = this.inSolutionNodeSet;
                return -1;
            } else {
                // in this case, you do nothing
                return 0;
            }
        }

        this.setLowerBound();
        if(this.lowerBound > this.upperBound.value) {
            return 0; // the sub-problem is not a promising one, do not add it into stack
        }

        // the sub-problem is promising and return its lowerbound for futher comparison
        return this.lowerBound;
    }

    /* based on the current partial solution, compute the upperbound of the problem
     * if the upperbound is lower than the current upperbound, update the global upperbound
     * if not, do nothing
     * if upper bound is updated, return true, else return false
     */
    public boolean checkUpperBound() {
        if(this.computeUpperBound() < this.upperBound.value) {
            this.upperBound.value = this.computeUpperBound();
            return true;
        }
        return false;
    }

    public int computeUpperBound() {

        return this.inSolutionNodeNumber + this.subGraph.numEdges;
    }
    public void setLowerBound() {

        this.lowerBound = this.computeLowerBound();
    }

    /* This function is used to compute the lower bound of the current sub-problem
     *
     */
    public int computeLowerBound() {
        //this.subGraph.printGraph();
        //System.out.println("degree = " + this.subGraph.nodeSet.last().degree);
        //return this.inSolutionNodeNumber + this.subGraph.numEdges / this.subGraph.nodeSet.last().degree + 1;
        //return this.inSolutionNodeNumber + this.findMinimalMatching();
        //return this.inSolutionNodeNumber + this.newLowerBound();
        //return Math.max(this.inSolutionNodeNumber + this.findMinimalMatching(), this.inSolutionNodeNumber + this.newLowerBound());
        return this.inSolutionNodeNumber + this.findLPSolution();
        //return this.inSolutionNodeNumber + Math.max(this.findLPSolution(), this.findMinimalMatching());
    }

//    public static void main(String[] args) {
//        int sol = findLPSolution();
//        System.out.println(Arrays.toString(sol));
//    }

    public int findMinimalMatching() {
        BnBGraph tempGraph = this.subGraph.clone();
        int numVertices = 0;
        while(tempGraph.numEdges > 0) {
            int node1 = -1;
            int node2 = -1;
            for(Map.Entry<String,Edge> edgeItem : tempGraph.edgeTable.entrySet()) {
                node1 = edgeItem.getValue().node1;
                node2 = edgeItem.getValue().node2;
                break;
            }
            tempGraph.deleteNode(node1);
            tempGraph.deleteNode(node2);
            numVertices += 2;
        }
        return numVertices / 2;
    }

    public int findLPSolution() {

        // firstly initialize the double arrays for objective functions and constraints
        double[] toolArray = new double[this.subGraph.nodeList.size()];
        double[] objectiveFunction = new double[this.subGraph.nodeList.size()];
        for(int index = 0; index < this.subGraph.numNodes; index++) {
            toolArray[index] = 0.0;
            objectiveFunction[index] = 1.0;
        }

        // initialize the Linear Problem
        LinearProgram lp = new LinearProgram(objectiveFunction);

        // add values to constraints
        Integer constraintNumber = 1;
        for(Map.Entry<String, Edge> edgeItems : this.subGraph.edgeTable.entrySet()) {
            double[] edgeConstraint = toolArray.clone();
            Edge edge = edgeItems.getValue();
            edgeConstraint[edge.getNode1()] = 1.0;
//            System.out.println(edge.getNode2());
//            System.out.println("///////////////");
//            System.out.println(this.subGraph.numNodes);
            edgeConstraint[edge.getNode2()] = 1.0;
            lp.addConstraint(new LinearBiggerThanEqualsConstraint(edgeConstraint, 1.0, "c" + constraintNumber.toString()));
            constraintNumber++;
        }

        // add variable-wise constraints x_i in [0,1]
        for(int index = 0; index < this.subGraph.numNodes; index++) {
            double[] variableConstraint = toolArray.clone();
            variableConstraint[index] = 1.0;
            lp.addConstraint(new LinearBiggerThanEqualsConstraint(variableConstraint, 0.0, "c" + constraintNumber.toString()));
            lp.addConstraint(new LinearSmallerThanEqualsConstraint(variableConstraint, 1.0, "c" + constraintNumber.toString()));
            constraintNumber++;
        }

        lp.setMinProblem(true);

        // solve the linear problem and get the solution
        LinearProgramSolver solver  = SolverFactory.newDefault();
        double[] sol = solver.solve(lp);

        // add the solution to get the value
        double lowerBound = 0.0;
        int intLowerBound = 0;
        for(int index = 0; index < this.subGraph.numNodes; index++) {
            lowerBound += sol[index];
            intLowerBound += Math.round(sol[index]);
        }

        //return (int) Math.floor(lowerBound) + 1;
        return intLowerBound / 2;
    }


    public int newLowerBound() {
        int tempNumEdge = this.subGraph.numEdges;
        int lowerBound = 0;
        NodeDegree tempVertex = this.subGraph.nodeSet.last();
        while(tempNumEdge > 0) {
            tempNumEdge -= tempVertex.degree;
            lowerBound++;
            tempVertex = this.subGraph.nodeSet.lower(tempVertex);
        }
        return lowerBound;
    }


}

class BoundValue {
    int value;
    BoundValue(int value) {
        this.value = value;
    }
}


