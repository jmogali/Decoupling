import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;

import Agent_Related.Agent;
import Centralized_Minimum_Spanning_Tree.MST_Wrapper;
import DIMACS.Loader;

public class Main {

	public static void main(String[] args) throws InterruptedException
	{
		Loader myParser = new Loader();
		String strFilePath = "E:\\Research\\STN\\Version_Controller\\Decoupling\\Data\\BDH_Problem_Instance\\bdh-agent-problems\\20_10_50_950_33.dimacs";
		
		boolean bSuccess;
		
		try {
			bSuccess = myParser.ParseFile(strFilePath);
			System.out.println("Parsing: " + bSuccess);			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Failed to parse");
		}
		
		int iNumOfAgents = myParser.GetNumOfAgents();
		SortedSet<String> ssAgentNames = myParser.ReturnAgentNames();
		
		HashMap<String, Agent> hp_Agents = new HashMap<String, Agent>();

		Iterator<String> it = ssAgentNames.iterator();
		String str;
		
		int iCouplingEdgeCount = 0;
		
		for(int iCount = 0; iCount < iNumOfAgents ; iCount++)
		{
			str = String.valueOf(it.next());			
			hp_Agents.put(str, new Agent(str));
			
			Agent a = hp_Agents.get(str);
			a.setValue(23, (long)(1200 + 5 * Math.random()));
			myParser.PopulateAgentSTN(a);
			
			if(Agent.SANITY_CHECK)
			{
				bSuccess = a.GetSTN().EstablishFPCIntraAgentConsistency(a.GetName());
				
				if(false == bSuccess)
				{
					System.out.println("Agent :" + a.GetName() + " FPC Consistency: "+bSuccess);
				}
			}
			
			iCouplingEdgeCount = iCouplingEdgeCount + a.GetSTN().GetNumOfCouplingEdges();
		}
		
		if(Agent.SANITY_CHECK)
		{
			System.out.println("Number Of coupling Edges: " + (iCouplingEdgeCount/2));
		}
		
		System.out.println("Done");
		
		MST_Wrapper stMSTWrapper = new MST_Wrapper();
		stMSTWrapper.CreateMSTAndCommObjects(hp_Agents);
		
		it = ssAgentNames.iterator();
		
		while(it.hasNext())
		{
			hp_Agents.get(it.next()).t.start();
		}
						
		try{
			it = ssAgentNames.iterator();			
			while(it.hasNext())
			{
				hp_Agents.get(it.next()).t.join();
			}			
		}catch(InterruptedException exp){
			System.out.println(exp);
		}		
	}
}
