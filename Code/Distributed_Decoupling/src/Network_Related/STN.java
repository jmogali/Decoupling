package Network_Related;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class STN {

	protected HashMap<String, STN_Vertex> m_hp_Vertices = null;   // Map containing all vertices
	protected HashMap<String, STN_Edge> m_hp_Intra_Edge = null;  // Map containing all intra-agent edges
	protected HashMap<String, Inter_agent_constraint> m_hp_Coupling_Edge = null;  // Map containing all intra-agent edges
	protected HashSet<String> m_set_Neighbhours = null;
	
	public STN()
	{
		m_hp_Vertices = new HashMap<String, STN_Vertex>();
		m_hp_Intra_Edge = new HashMap<String, STN_Edge>();
		m_hp_Coupling_Edge = new HashMap<String, Inter_agent_constraint>();
		m_set_Neighbhours = new HashSet<String>();
	}
	
	public void AddVertex(String str , STN_Vertex obVtx)
	{
		m_hp_Vertices.put(str, obVtx);
	}
	
	public boolean AddIntraEdge(String str , STN_Edge obEdge)
	{
		String stVtx1 = obEdge.GetVertex_1_Name();
		String stVtx2 = obEdge.GetVertex_2_Name();
		
		if( ( null == m_hp_Vertices.get(stVtx1) ) || ( null == m_hp_Vertices.get(stVtx2) ) )
		{ 
			return false;
		}
		
		m_hp_Intra_Edge.put(str, obEdge);
		
		return true;
	}
	
	public boolean AddCouplingEdge(String str , Inter_agent_constraint obEdge)
	{
		String stVtx1 = obEdge.GetVertex_1_Name();
		String stVtx2 = obEdge.GetVertex_2_Name();
		
		if( ( null == m_hp_Vertices.get(stVtx1) ) && ( null == m_hp_Vertices.get(stVtx2) ) )
		{ 
			return false;
		}
		
		m_hp_Coupling_Edge.put(str, obEdge);
		
		return true;
	}
	
	public boolean isVertexShared(String strVtx)
	{
		return m_hp_Vertices.get(strVtx).isShared();
	}
	
	public int GetNumOfCouplingEdges()
	{
		return m_hp_Coupling_Edge.size();
	}
	
	//Apply Floyd Warshall Algorithm to establish FPC 
	public boolean EstablishFPCIntraAgentConsistency(String strAgentName)
	{
		double d_ij , d_ik , d_kj;
		boolean b_ij = false;
		STN_Edge st_ei_ej = null;
		
		String stKey;
		
		final int f_iVtxHpSize = (int)(m_hp_Vertices.size() * 4 /3 ) + 1;
		HashMap<String, Double> hp_Vtx_Vtx_dist = new HashMap<String, Double>(f_iVtxHpSize);
	    Iterator<Map.Entry<String, STN_Vertex>> it = m_hp_Vertices.entrySet().iterator();
		
	    while(it.hasNext())
	    {
	    	hp_Vtx_Vtx_dist.put(it.next().getKey(), STN_Edge.f_Infty);
	    }
		
		Iterator<Map.Entry<String, STN_Vertex>> it_k = m_hp_Vertices.entrySet().iterator();
		
		while(it_k.hasNext()) //k
		{
			String strVtx_k = it_k.next().getKey();
			
			Iterator<Map.Entry<String, STN_Vertex>> it_i = m_hp_Vertices.entrySet().iterator();
						
			while(it_i.hasNext()) //i
			{				
				String strVtx_i = it_i.next().getKey(); 
						
				Iterator<Map.Entry<String, STN_Vertex>> it_j = m_hp_Vertices.entrySet().iterator();
				
				if(strVtx_k.equals(strVtx_i))
				{
					d_ik = hp_Vtx_Vtx_dist.get(strVtx_i);
				}
				else
				{
					stKey = strVtx_i+"_"+strAgentName+"__"+strVtx_k+"_"+strAgentName;
					
					STN_Edge st_ei_ek = m_hp_Intra_Edge.get(stKey);
					
					if(null == st_ei_ek)
					{
						m_hp_Intra_Edge.put(stKey, new STN_Edge(strVtx_i, strVtx_k));
						d_ik = STN_Edge.f_Infty;
					}
					else
					{
						d_ik= st_ei_ek.GetConstraintValue();
					}
				}
				
				while(it_j.hasNext()) //j
				{
					String strVtx_j = it_j.next().getKey();
					
					if(strVtx_k.equals(strVtx_j))
					{
						d_kj = hp_Vtx_Vtx_dist.get(strVtx_k);
					}
					else
					{
						stKey = strVtx_k+"_"+strAgentName+"__"+strVtx_j+"_"+strAgentName;
						
						STN_Edge st_ek_ej = m_hp_Intra_Edge.get(stKey);
						
						if(null == st_ek_ej)
						{
							m_hp_Intra_Edge.put(stKey , new STN_Edge(strVtx_k, strVtx_j));
							d_kj = STN_Edge.f_Infty;
						}
						else
						{
							d_kj= st_ek_ej.GetConstraintValue(); 
						}
					}
					
					if(strVtx_i.equals(strVtx_j))
					{
						d_ij = hp_Vtx_Vtx_dist.get(strVtx_i);
						b_ij = true;
					}
					else
					{
						stKey = strVtx_i+"_"+strAgentName+"__"+strVtx_j+"_"+strAgentName;
						st_ei_ej = m_hp_Intra_Edge.get(stKey);
						
						if(null == st_ei_ej)
						{
							st_ei_ej = new STN_Edge(strVtx_i, strVtx_j);
							m_hp_Intra_Edge.put(stKey, st_ei_ej);
							d_ij = STN_Edge.f_Infty;
						}
						else
						{
							d_ij= st_ei_ej.GetConstraintValue();
						}
						 
						b_ij = false;
					}					
					
					if(d_ij > d_ik + d_kj) 
					{
						d_ij = d_ik + d_kj;
						
						if(b_ij)
						{
							hp_Vtx_Vtx_dist.put(strVtx_i, d_ij);
							
							if(d_ij < 0) {return false;}
						}
						else
						{
							st_ei_ej.SetValue(d_ij); // Note we are reusing the object here 
						}	
					}										
				}
			}
		}
		
		if(false == SanityCheckForFPC())
		{
			return false;
		}
		
		return true;	
	}
	
	public boolean SanityCheckForFPC()
	{
		Iterator<Map.Entry<String, STN_Edge>> it_check = m_hp_Intra_Edge.entrySet().iterator();
		
		while(it_check.hasNext())
		{
			STN_Edge obj = it_check.next().getValue();
			
			//System.out.println(obj.GetKey() + " " + obj.GetConstraintValue());
			
			if(STN_Edge.f_Infty == obj.GetConstraintValue())
			{
				System.out.println(obj.GetKey() + "Error Detected");
				return false;
			}
		}
		
		int iVtxNum = m_hp_Vertices.size(); 
		
		int iDisparity =  m_hp_Intra_Edge.size() - ( iVtxNum * (iVtxNum - 1) );
		
		if(iDisparity != 0)
		{
			System.out.println("Mismatch between edges and expected:" + iDisparity );
			return false;
		}
		
		return true;
	}
	
	public HashSet<String> ReturnListNeighbours()
	{		
		if(m_set_Neighbhours.isEmpty())
		{
			Iterator<Map.Entry<String , Inter_agent_constraint>> it =  m_hp_Coupling_Edge.entrySet().iterator();
			
			while(it.hasNext())
			{
				m_set_Neighbhours.add(it.next().getValue().GetCoupledAgentName());
			}
			
			return m_set_Neighbhours;
		}
		else
		{
			return m_set_Neighbhours;
		}				
	}
	
	public HashSet<String> ReturnCouplingEdgesResponsibleFor()
	{
		HashSet<String> setCouplingEqnNames = new HashSet<String>();
		
		Set<String> setKeys = m_hp_Coupling_Edge.keySet();
		Iterator<String> it = setKeys.iterator();
		
		String strEqnName;
		
		while(it.hasNext())
		{
			strEqnName = it.next();
			Inter_agent_constraint stEqn = m_hp_Coupling_Edge.get(strEqnName);
			
			if(!stEqn.isResponsible())
			{
				continue;
			}
			
			setCouplingEqnNames.add(strEqnName);
		}
		
		return setCouplingEqnNames;
	}
	
	public boolean SanityCheckFor_B_Matrix(HashSet<String> setEqnNames)
	{
		Iterator<String> it = m_hp_Coupling_Edge.keySet().iterator();
		
		while(it.hasNext())
		{
			if(false == setEqnNames.contains(it.next()))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public int GetNumOfVertics()
	{
		return m_hp_Vertices.size();
	}
}
