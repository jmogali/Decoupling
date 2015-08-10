package DIMACS;

import Agent_Related.Agent;
import Network_Related.Inter_agent_constraint;
import Network_Related.STN;
import Network_Related.STN_Edge;
import Network_Related.STN_Vertex;
import Network_Related.User_Defined_Constraint;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;


public class Loader extends Parser{

	public Loader()
	{
		super();
	}
		
	public boolean ParseFile(String strFilePath) throws IOException 
	{
		boolean bSuccess =  super.ParseFile(strFilePath);		
		m_ss_Agent_Names.remove(m_hp_Vtx_Owner.get("z"));		
		return bSuccess;
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
		Iterator<Map.Entry<String , String>> it = m_hp_Vtx_Owner.entrySet().iterator();
		String strVtxName;
		boolean bShared;
				
		while(it.hasNext())
		{
			Map.Entry<String , String> enVal = it.next();
			
			strVtxName = enVal.getKey();
			
			if(strAgentName.equals(enVal.getValue()) || strVtxName.equals("z"))
			{				
				if(strVtxName.equals("z"))
				{
					bShared = false;
				}
				else
				{
					bShared = m_hp_Vtx_Shared.get(strVtxName);	
				}
				
				agSTN.AddVertex(strVtxName, new STN_Vertex(strVtxName , bShared));
			}
		}
	}
	
	private void PopulateAgentEdges(STN agSTN , String strAgentName)
	{
		Iterator<Map.Entry<String, User_Defined_Constraint>> it = m_hp_Edge_Weights.entrySet().iterator();	
		String strVtx1, strVtx2;
		String strOwner1 , strOwner2;
		double dConstraint;
		String stKey;
		
		while(it.hasNext())
		{
			Map.Entry<String, User_Defined_Constraint> enVal = it.next();
			
			strVtx1 = enVal.getValue().GetVertex1Name();
			strVtx2 = enVal.getValue().GetVertex2Name();
			
			dConstraint = enVal.getValue().GetConstraintValue();
			
			strOwner1 = m_hp_Vtx_Owner.get(strVtx1);						
			strOwner2 = m_hp_Vtx_Owner.get(strVtx2);
			
			boolean bRef = false;
			
			if(strVtx1.equals("z"))
			{
				strOwner1 = strOwner2;
				bRef = true;
			}
			else if(strVtx2.equals("z"))
			{
				strOwner2 = strOwner1;
				bRef = true;
			}
			
			if(bRef)
			{
				stKey = strVtx1 + "_"+strOwner1+"__"+strVtx2+"_"+strOwner2;
			}
			else
			{
				stKey = enVal.getKey();
			}
			
			// Should be rewritten for efficiency
			if(strAgentName.equals(strOwner1) && strAgentName.equals(strOwner2))
			{
				//intra agent edge
				agSTN.AddIntraEdge(stKey, new STN_Edge(strVtx1, strVtx2, dConstraint));				
			}
			else if( strAgentName.equals(strOwner1) || strAgentName.equals(strOwner2))
			{
				//coupling edge
				if(strAgentName.equals(strOwner1))
				{
					agSTN.AddCouplingEdge(stKey, new Inter_agent_constraint(strVtx1, strVtx2, dConstraint, strOwner2, true));
				}
				else
				{
					agSTN.AddCouplingEdge(stKey, new Inter_agent_constraint(strVtx1, strVtx2, dConstraint, strOwner1 , false));	
				}
			}		
		}
		
	}
	
	public int GetNumOfAgents()
	{
		return m_iNum_Agents;
	} 
	
	public SortedSet<String> ReturnAgentNames()
	{
		return m_ss_Agent_Names;
	}
}
