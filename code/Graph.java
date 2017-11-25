import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;

class Graph
{
	HashMap<Integer, HashMap<Integer, Integer>> graph;
	int numNodes = 0;
	Graph(int n)
	{
		numNodes=n;
		graph = new HashMap<Integer, HashMap<Integer, Integer>>();
		for(int i=0;i<n;i++)
			graph.put(i, new HashMap<Integer, Integer>());
	}

	public void addEdge(int node1, int node2, int weight) 
	{
		//graph[node1][node2]=weight;
		HashMap<Integer, Integer> first = graph.get(node1);
		//System.out.println("Printing first: "+first);
		first.put(node2, weight);

		//graph[node2][node1]=weight;
		HashMap<Integer, Integer> second = graph.get(node2);
		//System.out.println("Printing Second :"+second);
		second.put(node1, weight);
	}

	public int getNumNodes()
	{
		if(graph!=null)
			return numNodes;
		else
			return -1;
	}	


	public void printGraph()
	{
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for(int i=0;i<numNodes;i++)
		{
			map = graph.get(i);
			System.out.println(i+" : "+map);
		}
	}

	public HashMap<Integer, Integer> getNeighbours(int i)
	{
		return graph.get(i);
	}

	public int getEdgeWeight(int node1, int node2)
	{
		//return graph[node1][node2];
		HashMap<Integer, Integer> node1Neighbours = graph.get(node1);
		if(node1Neighbours==null)
			return -1;
		Integer weight = node1Neighbours.get(node2);
		if(weight==null)
			return -1;
		else
			return weight;
	}
}