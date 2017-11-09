import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class NodeDegree implements Comparable<NodeDegree> {

	public NodeDegree(int nodeNumber, int degree) {
		this.nodeNumber = nodeNumber;
		this.degree = degree;
	}



	int nodeNumber;
	
	int degree;
	
	@Override
	public String toString() {
		return nodeNumber + "";
	}

	@Override
	public int compareTo(NodeDegree arg0) {
		int compare = Integer.compare(degree, arg0.degree);
		if (compare == 0) {
			compare = Integer.compare(nodeNumber, arg0.nodeNumber);
		}
		return compare;
	}	
	
}
