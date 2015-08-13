package Centralized_Minimum_Spanning_Tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import Agent_Related.Agent;
import Agent_Related.Agent_Type;
import Distributed_Communication.MessagePassingQueue;
import Distributed_Communication.Synch_Object;


public class MST_Wrapper {
	
	private HashMap<String, Integer> m_hp_Agent_Int_Map = null;
	private HashMap<Integer, String> m_hp_Int_Agent_Map = null;
	
	private EdgeWeightedGraph m_graph;
	
	public MST_Wrapper()
	{
		m_hp_Agent_Int_Map = new HashMap<String, Integer>();
		m_hp_Int_Agent_Map = new HashMap<Integer, String>();
	}
	
	public void CreateMSTAndCommObjects( HashMap<String , Agent> hp_Agents )
	{
		CreateIntegerNamesForAgents( hp_Agents );
		
		CreateEdgeWeightedGraph( hp_Agents );		
		
		KruskalMST mst = new KruskalMST(m_graph);
		
		CreateCommunicatingObjects(hp_Agents , mst);
	}
	
	private void CreateIntegerNamesForAgents( HashMap<String , Agent> hp_Agents )
	{
		Iterator<Map.Entry<String, Agent>> it = hp_Agents.entrySet().iterator();
		
		int iName = 0; 
		String strAgName;
		
		while(it.hasNext())
		{
			strAgName = it.next().getKey();
			
			m_hp_Agent_Int_Map.put(strAgName , iName);
			m_hp_Int_Agent_Map.put(iName, strAgName);
			
			iName++;
		}
	}
	
	private void CreateEdgeWeightedGraph( HashMap<String , Agent> hp_Agents )
	{
		int iAgentName , iNeighbour , iCantorRep;
		
		m_graph = new EdgeWeightedGraph( m_hp_Agent_Int_Map.size() );
		
		HashSet<Integer> hp_Edges = new HashSet<Integer>();
		
		Iterator<Map.Entry<String, Agent>> it = hp_Agents.entrySet().iterator();
		
		while(it.hasNext())
		{
			Map.Entry<String, Agent> enVal = it.next();
			
			iAgentName = m_hp_Agent_Int_Map.get(enVal.getKey());
			
			HashSet<String> setNeighbhours = enVal.getValue().GetSTN().ReturnListNeighbours();
			
			Iterator<String> itNeighs = setNeighbhours.iterator();
			
			while(itNeighs.hasNext())
			{
				iNeighbour = m_hp_Agent_Int_Map.get(itNeighs.next());
				
				iCantorRep = GetCantorKey(iAgentName , iNeighbour);
				
				if(true == hp_Edges.add(iCantorRep))
				{
					Edge edge = new Edge(iAgentName, iNeighbour, Math.random());
					m_graph.addEdge(edge);
				}
			}
		}
	}
	
	private void CreateCommunicatingObjects( HashMap<String , Agent> hp_Agents  , KruskalMST mst) 
	{
		HashMap<String, HashSet<String>> hp_MST_Agnt_Neighs = new HashMap<String, HashSet<String>>();
		
		HashMap<Integer, Synch_Object<Integer>> hp_MST_Synch_Objects = new HashMap<Integer, Synch_Object<Integer>>();
		HashMap<Integer, Synch_Object<HashSet<String>>> hp_MST_Synch_hs_Objects = new HashMap<Integer, Synch_Object<HashSet<String>>>();
		
		int v, w , iCantorKey;
		String strAgent1 , strAgent2;
		
		for(Edge e : mst.edges())
		{
			v = e.either();
			w = e.other(v);
			
			strAgent1 = m_hp_Int_Agent_Map.get(v);
			strAgent2 = m_hp_Int_Agent_Map.get(w);
			
			if(false == hp_MST_Agnt_Neighs.containsKey(strAgent1))
			{
				hp_MST_Agnt_Neighs.put(strAgent1, new HashSet<String>());
			}
			
			if(false == hp_MST_Agnt_Neighs.containsKey(strAgent2))
			{
				hp_MST_Agnt_Neighs.put(strAgent2, new HashSet<String>());
			}
			
			iCantorKey = GetCantorKey(v, w);
			
			if(false == hp_MST_Synch_Objects.containsKey(iCantorKey))
			{
				hp_MST_Synch_Objects.put(iCantorKey, new Synch_Object<Integer>());
				hp_MST_Synch_hs_Objects.put(iCantorKey, new Synch_Object<HashSet<String>>());
			}
			
			hp_MST_Agnt_Neighs.get(strAgent1).add(strAgent2);
			hp_MST_Agnt_Neighs.get(strAgent2).add(strAgent1);
		}
		
		Set<String> setKeys = hp_Agents.keySet();
		Iterator<String> it = setKeys.iterator();
		String stKey;
 		int iNeighSize;
 		Agent_Type enType;
 		
		while(it.hasNext())
		{
			stKey = it.next();
			iNeighSize = hp_MST_Agnt_Neighs.get(stKey).size();
	 		
			if(1 == iNeighSize)
			{
				enType = Agent_Type.LEAF;
			}
			else
			{
				enType = Agent_Type.NON_LEAF;
			}
			
			HashSet<String> keys_neighs = hp_MST_Agnt_Neighs.get(stKey);
			
			MessagePassingQueue<Integer> msgQueue = new MessagePassingQueue<Integer>(iNeighSize - 1, stKey, keys_neighs); 
			hp_Agents.get(stKey).SetMessagePassing_Integer_Queue_AndSetType(msgQueue, enType);
			
			MessagePassingQueue<HashSet<String>> msg_hs_Queue = new MessagePassingQueue<HashSet<String>>(iNeighSize - 1, stKey, keys_neighs); 
			hp_Agents.get(stKey).SetMessagePassing_B_Matrix_Queue_AndSetType(msg_hs_Queue, enType);
		}
		
		Iterator<Map.Entry<String, HashSet<String>>> itComm = hp_MST_Agnt_Neighs.entrySet().iterator();
		
		while(itComm.hasNext())
		{
			Map.Entry<String, HashSet<String>> enVal = itComm.next();
			
			Set<String> stNeighs = enVal.getValue();
			
			Iterator<String> itNeighs = stNeighs.iterator();
			
			String strCurrAgentName , strNeighName;
			
			strCurrAgentName = enVal.getKey();
			
			Agent agent = hp_Agents.get(strCurrAgentName);
			
			v = m_hp_Agent_Int_Map.get(strCurrAgentName);
			
			while(itNeighs.hasNext())
			{
				strNeighName = itNeighs.next();
				w =  m_hp_Agent_Int_Map.get(strNeighName);
				
				iCantorKey = GetCantorKey(v, w);
						
				MessagePassingQueue<Integer> obNeighTransQueue = hp_Agents.get(strNeighName).GetMessagePassingQueue();
				MessagePassingQueue<HashSet<String>> obNeigh_hs_TransQueue = hp_Agents.get(strNeighName).GetMessagePassing_B_Mat_Queue();
				
				Synch_Object<Integer> objSynchNeigh = hp_MST_Synch_Objects.get(iCantorKey);
				Synch_Object<HashSet<String>> objSynch_hs_Neigh = hp_MST_Synch_hs_Objects.get(iCantorKey);
				
				agent.AddComm_Integer_Neighbour(strNeighName, objSynchNeigh, obNeighTransQueue);
				agent.AddComm_B_Mat_Neighbour(strNeighName, objSynch_hs_Neigh, obNeigh_hs_TransQueue);
			}			
		}
		
		hp_MST_Agnt_Neighs = null;
		hp_MST_Synch_Objects = null;
		hp_MST_Synch_hs_Objects = null;
	}
	
	private int GetCantorKey(int iNum1 , int iNum2)
	{
		if(iNum1 > iNum2)
		{
			int iTemp = iNum1;
			iNum1 = iNum2;
			iNum2 = iTemp;
		}
		
		return (int) ( 0.5 * ((iNum1 + iNum2) * (iNum1 + iNum2 + 1)) ) + iNum2;
	}
}
