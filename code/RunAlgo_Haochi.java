import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class RunAlgo {

	public static void main (String[] args) {
		RunAlgo ra = new RunAlgo();
		TreeSet<NodeDegree> nodes = new TreeSet<>();
		List<Edge> e = new ArrayList<>();

		NodeDegree node1 = new NodeDegree(1, 3);
		NodeDegree node2 = new NodeDegree(2, 4);
		NodeDegree node3 = new NodeDegree(3, 3);
		NodeDegree node4 = new NodeDegree(4, 2);
		NodeDegree node5 = new NodeDegree(5, 2);
		NodeDegree node6 = new NodeDegree(6, 5);

		nodes.add(node1);
		nodes.add(node2);
		nodes.add(node3);
		nodes.add(node4);
		nodes.add(node5);
		nodes.add(node6);

		e.add(ra.new Edge(node1, node3));
		e.add(ra.new Edge(node1, node4));
		e.add(ra.new Edge(node1, node5));
		e.add(ra.new Edge(node2, node4));
		e.add(ra.new Edge(node3, node1));
		e.add(ra.new Edge(node3, node6));
		e.add(ra.new Edge(node4, node1));
		e.add(ra.new Edge(node4, node2));
		e.add(ra.new Edge(node4, node5));
		e.add(ra.new Edge(node5, node4));
		e.add(ra.new Edge(node5, node1));
		e.add(ra.new Edge(node6, node3));

		List<NodeDegree> result = ra.algorithm(nodes, e);
		System.out.println(result);
	}

	public List<NodeDegree> algorithm(TreeSet<NodeDegree> nodes, List<Edge> e) {

		List<NodeDegree> c = new ArrayList<>(); // Initialize empty list.
		
		while (!e.isEmpty()) { // While E is not empty.

			NodeDegree u = nodes.first(); // Get the first element from treeset u.
			
			List<NodeDegree> neighbours = allNeighbours(u, e); // Find all the neighbours
			// of corresponding  node in the graph.
			
			c.addAll(neighbours); // Add neighbors u to C.
			
			/*
			 * Delete all edges passing from u from E. 
			 */
			for (Iterator<Edge> it = e.iterator(); it.hasNext();) {
				Edge next = it.next();
				if (next.from.nodeNumber == u.nodeNumber ||
				   	next.to.nodeNumber == u.nodeNumber) {				
				it.remove();
				}
			}
			
			/*
			 * Delete all edges passing from N(u) from E.
			 */
			for (NodeDegree nr: neighbours) {															
				for (Iterator<Edge> it = e.iterator(); it.hasNext();) {
					Edge next = it.next();
					if (next.from.nodeNumber == nr.nodeNumber ||
					   	next.to.nodeNumber == nr.nodeNumber) {
					it.remove();
					}
				}
			}
			
			// Delete u from treeset.
			nodes.remove(u);
			
			// Delete N(u) from treeset.
			nodes.removeAll(neighbours);
		}

		return c;
	}

	// Method to find all neighbors of nd.
	private List<NodeDegree> allNeighbours(NodeDegree nd, List<Edge> e) {
		List<NodeDegree> result = new ArrayList<>();
		for (int i = 0; i < e.size(); i ++) {
			if (e.get(i).from.nodeNumber == nd.nodeNumber) {
				result.add(e.get(i).to);
			}
		}
		return result;
	}


	// Class to encapsulate Edge.
	class Edge {
		NodeDegree to;
		NodeDegree from;
		Edge(NodeDegree from, NodeDegree to) {
			this.from = from;
			this.to = to;
		}
		@Override
		public String toString() {
			return from + " : " + to;
		}
	}
}
