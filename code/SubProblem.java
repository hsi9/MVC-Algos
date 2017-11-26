import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SubProblem {
    int lowerBound; // the best solution can be found along this branch
    BoundValue upperBound; // the current best solution size found for the original problem
    int inSolutionNodeNumber; // number of Vetices already included in the current partial solution
    //HashSet<Integer> excludeNodeSet; // the nodes chosen by the partial solution not to included into final solution
    BnBGraph subGraph; // the subGraph of the original graph used to represent the sub-problem to solve

    SubProblem(int inSolutionNodeNumber, BoundValue upperBound, BnBGraph subGraph) {
        this.inSolutionNodeNumber = inSolutionNodeNumber;
        this.lowerBound = 0;
        this.upperBound = upperBound;
        //this.excludeNodeSet = new HashSet<>(excludeNodeSet);
        this.subGraph = subGraph;
    }

    /*
     * Expand the sub-problem to a new sub-problem by including the highest-degree-node into current solution
     */
    public SubProblem leftExpandSubProblem() {

        // find the splitNode as the highest-degree node not in the current exludeNodeSet
        NodeDegree splitNode = this.subGraph.nodeSet.last();

        // find the neighbors of the split node
        HashMap<Integer, Integer> neighbours = this.subGraph.getNeighbours(splitNode.nodeNumber);

        // get the size of partial solution for the expanded sub-problem
        int leftInSolutionNodeNumber = this.inSolutionNodeNumber + 1;

        // get the subGraph for the sub-problem
        BnBGraph leftSubGraph = this.subGraph.clone();

        // delete the split node from the subGraph
        leftSubGraph.deleteNode(splitNode.nodeNumber);

        // also delete all the neighbors of split node whose degree is only 1
        for(Map.Entry<Integer, Integer> neighbour : neighbours.entrySet()) {
            if(neighbour.getValue() == 1) {
                leftSubGraph.deleteNode(neighbour.getKey());
            }
        }

        // build the sub-problem
        SubProblem leftSubProblem = new SubProblem(leftInSolutionNodeNumber, this.upperBound, leftSubGraph);
        return leftSubProblem;
    }

    public SubProblem rightExpandSubproblem() {
        // find the splitNode
        NodeDegree splitNode = this.subGraph.nodeSet.last();

        // find the neighbors of the split node
        HashMap<Integer, Integer> neighbours = this.subGraph.getNeighbours(splitNode.nodeNumber);

        // get the size of partial solution for the expanded sub-problem
        int rightInSolutionNodeNumber = this.inSolutionNodeNumber + splitNode.degree;

        // get the subGraph for the sub-problem
        BnBGraph rightSubGraph = this.subGraph.clone();

        // delete all neighbours from the subGraph
        for(Map.Entry<Integer, Integer> neighbour : neighbours.entrySet()) {
            rightSubGraph.deleteNode(neighbour.getKey());
        }

        // also delete the current split node because it becomes isolated after deleting all its neighbours
        rightSubGraph.deleteNode(splitNode.nodeNumber);

        // build the sub-problem
        SubProblem rightSubProblem = new SubProblem(rightInSolutionNodeNumber, this.upperBound, rightSubGraph);
        return rightSubProblem;
    }

    /* based on the current partial, compute the upperbound of the problem
     * if the upperbound is lower than the current upperbound, update the global upperbound
     * if not, do nothing
     */
    public void checkUpperBound() {
        if(this.inSolutionNodeNumber + this.subGraph.numEdges < this.upperBound.value) {
            this.upperBound.value = this.inSolutionNodeNumber + this.subGraph.numEdges;
        }
    }

    public static void main(String[] args) {
        BoundValue a = new  BoundValue(1);
        BoundValue b = a;
        b.value = 2;
        System.out.println(a.value);
    }

}

class BoundValue {
    int value;
    BoundValue(int value) {
        this.value = value;
    }
}


