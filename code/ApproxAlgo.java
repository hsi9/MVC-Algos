import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

class ApproxAlgo 
{
	// Ideal data structure to use is priority queue, however deletion can take O(n) time,
	// so we use TreeSet where insertion and deletion both take O(logn) time.
	TreeSet<NodeDegree> nodeDegrees = new TreeSet<>();
	HashSet<String> edges = new HashSet<>();
	Set<Integer> mvc = new HashSet<>();
	NodeDegree[] pointer=null; 
	
	ApproxAlgo(Graph g)
	{
		//System.out.println("Inside the constructor");
		pointer = new NodeDegree[g.getNumNodes()];
		initializeNodesWithDegree(g, nodeDegrees, pointer);
		//System.out.println("Initialized nodes with degree");
		initializeEdgesSet(g, edges);
		//System.out.println("Initialized edges");
	}

	public void initializeNodesWithDegree(Graph g, TreeSet<NodeDegree> treeSetNodes, NodeDegree[] pointers)
	{
		HashMap<Integer, Integer> nodes =null;
		NodeDegree temp = null;
		for(int i=0;i<g.getNumNodes();i++)
		{
			nodes = g.getNeighbours(i);
			temp = new NodeDegree(i, nodes.size());
			pointers[i] = temp;
			treeSetNodes.add(new NodeDegree(i, nodes.size()));
		}
	}

	public void initializeEdgesSet(Graph g, HashSet<String> edges)
	{
		HashMap<Integer, Integer> nodes = null;
		Set<Integer> keys = null;
		for(int i=0;i<g.getNumNodes();i++)
		{
			nodes = g.getNeighbours(i);
			keys = nodes.keySet();
			for(int k:keys)
			{
				edges.add(i+"_"+k);
				edges.add(k+"_"+i);		
			}
			
		} 
	}

	public void runAlgo(Graph g)
	{
		
		Set<Integer> neighbours = null;
		Set<Integer> nextNeighbours = null;
		NodeDegree u=null;
		HashMap<Integer, Integer> node=null;
		while(!edges.isEmpty())
		{
			u = nodeDegrees.first();
			node = g.getNeighbours(u.nodeNumber);
			neighbours = node.keySet();
			//System.out.println("Selected Node - "+u.nodeNumber);
			//System.out.println("Neighbours -"+neighbours.toString());
			// add all the edges.
			mvc.addAll(neighbours);
			//delete all the edges between u and its neighbours;
			for(int neighbour:neighbours)
			{
				edges.remove(u.nodeNumber+"_"+neighbour);
				edges.remove(neighbour+"_"+u.nodeNumber);

				//System.out.println(edges.remove(u.nodeNumber+"_"+neighbour));
				//System.out.println(edges.remove(neighbour+"_"+u.nodeNumber));
			}
			//delete all edges incident on neighbours of u.
			for(int neighbour:neighbours)
			{
				node = g.getNeighbours(neighbour);
				nextNeighbours = node.keySet();
				for(int nextNeighbour:nextNeighbours)
				{
					edges.remove(neighbour+"_"+nextNeighbour);
					edges.remove(nextNeighbour+"_"+neighbour);
				}
			}

			// delete from node degrees.
			//System.out.println("removed edges");
			for(int neighbour: neighbours)
			{
				if(pointer[neighbour]!=null)
				{
					nodeDegrees.remove(pointer[neighbour]);
					pointer[neighbour]=null;
				}

			}
			//System.out.println("removed neighbours");
			nodeDegrees.remove(u);
			//System.out.println("removed node");

		}
		System.out.println(mvc.size());
		//System.out.println(mvc.toString());
		
	}
		
}

