import java.io.BufferedReader;
import java.io.FileReader;

public class Local2 {
    public static void main(String[] args)
    {
        LocalGraph graph = LocalGraph.readGraph(args[0]);
        //BufferedWriter bw = null;
        graph.printGraph();

    }
}
