import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SubProblem {
    int lowerBound; // the best solution can be found along this branch
    int upperBound; // the current best solution size found for the original problem
    int inSolutionNodeNumber; // number of Vetices already included in the current partial solution
    HashSet<Integer> excludeNodeSet; // the nodes chosen by the partial solution not to included into final solution
    BnBGraph subGraph; // the subGraph of the original graph used to represent the sub-problem to solve

    SubProblem(int inSolutionNodeNumber, HashSet<Integer> excludeNodeSet, BnBGraph subGraph) {
        this.inSolutionNodeNumber = inSolutionNodeNumber;
        this.lowerBound = 0;
        this.upperBound = inSolutionNodeNumber + subGraph.numEdges;
        this.excludeNodeSet = new HashSet<>(excludeNodeSet);
        this.subGraph = subGraph;
    }

    /*
     * Expand the sub-problem to a new sub-problem by including the highest-degree-node into current solution
     */
    public SubProblem leftExpandSubProblem() {

        // find the splitNode as the highest-degree node not in the current exludeNodeSet
        NodeDegree splitNode = this.subGraph.nodeSet.last();
        while(this.excludeNodeSet.contains(splitNode.nodeNumber)) {
            splitNode = this.subGraph.nodeSet.lower(splitNode);
        }

        // find the neighbors of the split node
        HashMap<Integer, Integer> neighbours = this.subGraph.getNeighbours(splitNode.nodeNumber);

        // get the size of partial solution for the expanded sub-problem
        int leftInSolutionNodeNumber = this.inSolutionNodeNumber + 1;

        // get the excludeNodeSet for the sub-problem
        HashSet<Integer> leftExcludeNodeSet = new HashSet<>(excludeNodeSet);

        // add all the neighbors of split node whose degree is only 1 to exclude node set
        for(Map.Entry<Integer, Integer> neighbour : neighbours.entrySet()) {
            if(neighbour.getValue() == 1) {
                leftExcludeNodeSet.add(neighbour.getKey());
            }
        }

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
        SubProblem leftSubProblem = new SubProblem(leftInSolutionNodeNumber, leftExcludeNodeSet, leftSubGraph);
        return leftSubProblem;
    }

    public SubProblem rightExpandSubproblem() {
        // find the splitNode as the highest-degree node not in the current exludeNodeSet
        NodeDegree splitNode = this.subGraph.nodeSet.last();
        while(this.excludeNodeSet.contains(splitNode.nodeNumber)) {
            splitNode = this.subGraph.nodeSet.lower(splitNode);
        }

        // find the neighbors of the split node
        HashMap<Integer, Integer> neighbours = this.subGraph.getNeighbours(splitNode.nodeNumber);

        // get the size of partial solution for the expanded sub-problem
        int rightInSolutionNodeNumber = this.inSolutionNodeNumber;
        for(Map.Entry<Integer, Integer> neighbour : neighbours.entrySet()) {
            if(this.excludeNodeSet.contains(neighbour.getKey())) {
                // if one of the neighbors is in the exclude set,
                // we cannot expand the sub-problem by exclude the split node
                // return null
                return null;
            }
            rightInSolutionNodeNumber++;
        }

        // get the excludeNodeSet for the sub-problem and add the split node to it
        HashSet<Integer> rightExcludeNodeSet = new HashSet<>(excludeNodeSet);
        rightExcludeNodeSet.add(splitNode.nodeNumber);

        // get the subGraph for the sub-problem
        BnBGraph rightSubGraph = this.subGraph.clone();

        // delete all neighbours from the subGraph
        for(Map.Entry<Integer, Integer> neighbour : neighbours.entrySet()) {
            rightSubGraph.deleteNode(neighbour.getKey());
        }

        // also delete the current split node because it becomes isolated after deleting all its neighbours
        rightSubGraph.deleteNode(splitNode.nodeNumber);

        // build the sub-problem
        SubProblem rightSubProblem = new SubProblem(rightInSolutionNodeNumber, rightExcludeNodeSet, rightSubGraph);
        return rightSubProblem;
    }

}
