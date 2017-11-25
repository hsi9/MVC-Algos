import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;

public class RunAlgo
{
	
	public static void main(String[] args)
	{
		Graph graph = null;
		BufferedReader br=null;
		BufferedWriter bw = null;
		String line=null;
		String[] tokens=null;
		ApproxAlgo approxAlgo =null;
		try
		{
			br=new BufferedReader(new FileReader(args[0]));
			line=br.readLine();
			tokens=line.split(" ");
			graph=new Graph(Integer.parseInt(tokens[0]));
			int i=0;
			
			while((line=br.readLine())!=null)
			{	
				
				
				if(line.equals(""))
					continue;
				
				tokens=line.split(" ");
				for(String token:tokens)
				{
					graph.addEdge(i, Integer.parseInt(token)-1,1);
				}
				
				
				i++;
				//graph.printGraph();
				//System.out.println(line);
				
			}
			//graph.printGraph();

			br.close();
			approxAlgo = new ApproxAlgo(graph);
			approxAlgo.runAlgo(graph);

			//bw.close();
		}
		catch(Exception e)
		{
			System.out.println("Check the addEdge method or the printGraph method.")	;
		}
	}
}