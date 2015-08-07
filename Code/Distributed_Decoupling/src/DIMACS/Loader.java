package DIMACS;

import Agent_Related.Agent;
import Network_Related.Inter_agent_constraint;
import Network_Related.STN;
import Network_Related.STN_Edge;
import Network_Related.STN_Vertex;
import Network_Related.User_Defined_Constraint;

import java.util.Iterator;
import java.util.Map;


public class Loader extends Parser{

	public Loader()
	{
		super();
	}
	
	public void PopulateAgentSTN(Agent obAgent)
	{
		STN agSTN = obAgent.GetSTN();
		
		PopulateAgentVertices(agSTN , obAgent.GetName());
		
		PopulateAgentEdges(agSTN , obAgent.GetName());	
		
	}
	
	//Populate Vertices
	private void PopulateAgentVertices(STN agSTN , String strAgentName)
	{
		Iterator<Map.Entry<String , String>> it = m_hmp_Vtx_Owner.entrySet().iterator();
		String strVtxName;
		boolean bShared;
				
		while(it.hasNext())
		{
			Map.Entry<String , String> enVal = it.next();
			
			strVtxName = enVal.getKey();
			bShared = m_hmp_Vtx_Shared.get(strVtxName);
			
			if(strAgentName.equals(enVal.getValue()))
			{
				agSTN.AddVertex(strVtxName, new STN_Vertex(strVtxName , bShared));
			}
		}
	}
	
	private void PopulateAgentEdges(STN agSTN , String strAgentName)
	{
		Iterator<Map.Entry<String, User_Defined_Constraint>> it = m_hmp_Edge_Weights.entrySet().iterator();	
		String strVtx1, strVtx2;
		String strOwner1 , strOwner2;
		
		while(it.hasNext())
		{
			Map.Entry<String, User_Defined_Constraint> enVal = it.next();
			
			strVtx1 = enVal.getValue().GetVertex1Name();
			strVtx2 = enVal.getValue().GetVertex2Name();
			
			strOwner1 = m_hmp_Vtx_Owner.get(strVtx1);
			strOwner2 = m_hmp_Vtx_Owner.get(strVtx2);
			
			// Should be rewritten for efficiency
			
			if(strAgentName.equals(strOwner1) && strAgentName.equals(strOwner2))
			{
				//intra agent edge
				agSTN.AddIntraEdge(enVal.getKey(), new STN_Edge(strVtx1, strVtx2));				
			}
			else if( strAgentName.equals(strOwner1) || strAgentName.equals(strOwner2))
			{
				//coupling edge
				if(strAgentName.equals(strOwner1))
				{
					agSTN.AddCouplingEdge(enVal.getKey(), new Inter_agent_constraint(strVtx1, strVtx2, strOwner2));
				}
				else
				{
					agSTN.AddCouplingEdge(enVal.getKey(), new Inter_agent_constraint(strVtx1, strVtx2, strOwner1));	
				}
			}		
		}
		
	}		
}