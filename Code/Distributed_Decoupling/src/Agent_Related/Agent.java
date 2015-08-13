package Agent_Related;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

import Distributed_Communication.MessagePassingQueue;
import Distributed_Communication.NoNewMessagesAcceptableException;
import Distributed_Communication.Synch_Object;
import EquationGenerator.Equation_Generator;
import Network_Related.STN;


public class Agent implements Runnable{

	public static final boolean PRINT_MST_MESSAGES = false; 
	public static final boolean SANITY_CHECK = true;
	private String m_str_Name;
	public Thread t;
	private int m_iVal;
	private long m_dDelay;
	private Agent_Type m_enType;
	private Equation_Generator m_STN;
	
	// Communicating members
	private MessagePassingQueue<Integer> m_que_owner = null;
	private HashMap<String , Synch_Object<Integer>> m_hmp_Trans_Msg = null; // Contains communicating neighbor names after Min Spanning Tree
	private HashMap<String , MessagePassingQueue<Integer>> m_hmp_que_Rec_Msg = null;

	//Coupling matrix synchronization
	private MessagePassingQueue<HashSet<String>> m_que_hs_owner = null;
	private HashMap<String , Synch_Object<HashSet<String>>> m_hmp_Trans_hs_Msg = null; // Contains communicating neighbor names after Min Spanning Tree
	private HashMap<String ,MessagePassingQueue<HashSet<String>>> m_hmp_que_Rec_hs_Msg = null;

	public Agent(String strName)
	{
		m_str_Name = strName;
		m_STN = new Equation_Generator();
		
		m_hmp_Trans_Msg = new HashMap<String , Synch_Object<Integer>>();
		m_hmp_que_Rec_Msg = new HashMap<String, MessagePassingQueue<Integer>>();
		
		m_hmp_Trans_hs_Msg = new HashMap<String, Synch_Object<HashSet<String>>>();
		m_hmp_que_Rec_hs_Msg = new HashMap<String, MessagePassingQueue<HashSet<String>>>();
		
		t = new Thread(this , m_str_Name);
	}
	
	public void SetMessagePassing_Integer_Queue_AndSetType(MessagePassingQueue<Integer> Msg_Queue , Agent_Type en_Type)
	{
		m_que_owner = Msg_Queue;
		m_enType = en_Type;
	}
	
	public void SetMessagePassing_B_Matrix_Queue_AndSetType(MessagePassingQueue<HashSet<String>> Msg_Queue , Agent_Type en_Type)
	{
		m_que_hs_owner = Msg_Queue;
		m_enType = en_Type;
	}
	
	public void setValue(int iValue , long dDelay)
	{
		m_iVal = iValue;
		m_dDelay = dDelay;
	}
	
	public String GetName()
	{
		return m_str_Name;
	}
	
	public MessagePassingQueue<Integer> GetMessagePassingQueue()
	{
		return m_que_owner;
	}
	
	public MessagePassingQueue<HashSet<String>> GetMessagePassing_B_Mat_Queue()
	{
		return m_que_hs_owner;
	}
	
	public void AddComm_Integer_Neighbour(String strAgentName, Synch_Object<Integer> objNeigh , MessagePassingQueue<Integer> obNeighTransQueue)
	{
		m_hmp_Trans_Msg.put(strAgentName , objNeigh);
		m_hmp_que_Rec_Msg.put(strAgentName, obNeighTransQueue);
	}
	
	public void AddComm_B_Mat_Neighbour(String strAgentName, Synch_Object<HashSet<String>> objNeigh , MessagePassingQueue<HashSet<String>> obNeighTransQueue)
	{
		m_hmp_Trans_hs_Msg.put(strAgentName , objNeigh);
		m_hmp_que_Rec_hs_Msg.put(strAgentName, obNeighTransQueue);
	}
	
	public STN GetSTN()
	{
		return m_STN;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		if(SANITY_CHECK)
		{
			System.out.println("Agent: "+m_str_Name+" initialized");
		}
		
//		try {
//			ComputeSum();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		try {
			SynchroizeToForm_B_Matrix();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Distributed Summation Function
	private void ComputeSum() throws Exception 
	{
		int iSum = 0;
		
		try {
			Thread.sleep(m_dDelay);
		} catch (InterruptedException e) {
			System.out.println("Agent : " + m_str_Name + "Spuriously Awoken");
		}                
			
		if(Agent_Type.LEAF == m_enType)
		{			
			if(m_hmp_Trans_Msg.size() >1)
			{
				System.out.println("Mistake in Minimum Spanning Tree");
			}
			
			Iterator<Map.Entry<String ,MessagePassingQueue<Integer>>> it = m_hmp_que_Rec_Msg.entrySet().iterator();
			
			while(it.hasNext())
			{
				Map.Entry<String, MessagePassingQueue<Integer>> enVal = (Map.Entry<String, MessagePassingQueue<Integer>>) it.next();
				try {
					enVal.getValue().AddElement(m_str_Name, m_iVal);
					
					if(PRINT_MST_MESSAGES)
					{
						System.out.println("Agent: "+ m_str_Name + " Entered try1 for:" + enVal.getKey());
					}
					
					iSum = m_hmp_Trans_Msg.get(enVal.getKey()).GetValue();
					
					if(PRINT_MST_MESSAGES)
					{
						System.out.println("Agent: "+ m_str_Name + " Left try1");
					}
					
				} catch (NoNewMessagesAcceptableException e) {
					String stAgent = e.toString(); 
					
					if(PRINT_MST_MESSAGES)
					{
						System.out.println("Agent: "+ m_str_Name + " Entered catch1 for:"+stAgent);
					}
					
					m_hmp_Trans_Msg.get(stAgent).SetValue(m_iVal , false);
					iSum = m_hmp_Trans_Msg.get(stAgent).GetValue();				
					
					if(PRINT_MST_MESSAGES)
					{
						System.out.println("Agent: "+ m_str_Name + " Left catch1");
					}
				}	
			}
		}
		else
		{
			// This step waits till n-1 neighbors update their values and figures the slow agent
			MessagePassingQueue<Integer>.Value_Slow_Agent obValAgent= m_que_owner.CombineOperation();
			
			iSum = obValAgent.GetValue() + m_iVal;
			
			try{
				MessagePassingQueue<Integer> enVal = m_hmp_que_Rec_Msg.get(obValAgent.GetSlowAgent());
				enVal.AddElement(m_str_Name, iSum);
				
				if(PRINT_MST_MESSAGES)
				{
					System.out.println("Agent: "+ m_str_Name + " Entered try2 for:"+obValAgent.GetSlowAgent());
				}
				
				iSum = m_hmp_Trans_Msg.get(obValAgent.GetSlowAgent()).GetValue();
				
				if(PRINT_MST_MESSAGES)
				{
					System.out.println("Agent: "+ m_str_Name + " Left try2");
				}
				
			}catch(NoNewMessagesAcceptableException e)
			{
				//Transmits its estimate to the slow agent
				m_hmp_Trans_Msg.get(obValAgent.GetSlowAgent()).SetValue(iSum , false);
				
				if(PRINT_MST_MESSAGES)
				{
					System.out.println("Agent: "+ m_str_Name + " Entered catch2 for:"+obValAgent.GetSlowAgent());
				}
				//Gets back all the values from the slow agent
				iSum = m_hmp_Trans_Msg.get(obValAgent.GetSlowAgent()).GetValue();
				
				if(PRINT_MST_MESSAGES)
				{
					System.out.println("Agent: "+ m_str_Name + " Left catch2");
				}
			}
			
			//Dispatch back all the values
			Iterator<String> it = m_hmp_Trans_Msg.keySet().iterator();
			 
			while(it.hasNext())
			{
				String strNeigh = it.next();
				if(obValAgent.GetSlowAgent() == strNeigh) 
				{continue;}
				
				m_hmp_Trans_Msg.get(strNeigh).SetValue(iSum , true);
			}
		}		
		
		System.out.println("Agent Name:" + m_str_Name +" sum:"+iSum);		
	}
	
	private void SynchroizeToForm_B_Matrix() throws Exception
	{
		HashSet<String> setEqnNames = m_STN.ReturnCouplingEdgesResponsibleFor();
		
		if(Agent_Type.LEAF == m_enType)
		{			
			if(m_hmp_Trans_hs_Msg.size() >1)
			{
				System.out.println("Mistake in Minimum Spanning Tree");
			}
			
			Iterator<Map.Entry<String ,MessagePassingQueue<HashSet<String>>>> it = m_hmp_que_Rec_hs_Msg.entrySet().iterator();
			
			while(it.hasNext())
			{
				Map.Entry<String, MessagePassingQueue<HashSet<String>>> enVal = (Map.Entry<String, MessagePassingQueue<HashSet<String>>>) it.next();
				try {
					enVal.getValue().AddElement(m_str_Name, setEqnNames);
					
					if(PRINT_MST_MESSAGES)
					{
						System.out.println("Agent: "+ m_str_Name + " Entered try1 for:" + enVal.getKey());
					}
					
					setEqnNames = null;
					setEqnNames = m_hmp_Trans_hs_Msg.get(enVal.getKey()).GetValue();
					
					if(PRINT_MST_MESSAGES)
					{
						System.out.println("Agent: "+ m_str_Name + " Left try1");
					}
					
				} catch (NoNewMessagesAcceptableException e) {
					String stAgent = e.toString(); 
					
					if(PRINT_MST_MESSAGES)
					{
						System.out.println("Agent: "+ m_str_Name + " Entered catch1 for:"+stAgent);
					}
					
					m_hmp_Trans_hs_Msg.get(stAgent).SetValue(setEqnNames , false);
					setEqnNames = m_hmp_Trans_hs_Msg.get(stAgent).GetValue();				
					
					if(PRINT_MST_MESSAGES)
					{
						System.out.println("Agent: "+ m_str_Name + " Left catch1");
					}
				}	
			}
		}
		else
		{
			// This step waits till n-1 neighbors update their values and figures the slow agent
			MessagePassingQueue<HashSet<String>>.Value_Slow_Agent obValAgent= m_que_hs_owner.CombineOperation();
			
			//iSum = obValAgent.GetValue() + m_iVal;
			setEqnNames.addAll(obValAgent.GetValue());
			
			try{
				MessagePassingQueue<HashSet<String>> enVal = m_hmp_que_Rec_hs_Msg.get(obValAgent.GetSlowAgent());
				enVal.AddElement(m_str_Name, setEqnNames);
				
				if(PRINT_MST_MESSAGES)
				{
					System.out.println("Agent: "+ m_str_Name + " Entered try2 for:"+obValAgent.GetSlowAgent());
				}
				
				setEqnNames = m_hmp_Trans_hs_Msg.get(obValAgent.GetSlowAgent()).GetValue();
				
				if(PRINT_MST_MESSAGES)
				{
					System.out.println("Agent: "+ m_str_Name + " Left try2");
				}
				
			}catch(NoNewMessagesAcceptableException e)
			{
				//Transmits its estimate to the slow agent
				m_hmp_Trans_hs_Msg.get(obValAgent.GetSlowAgent()).SetValue(setEqnNames , false);
				
				if(PRINT_MST_MESSAGES)
				{
					System.out.println("Agent: "+ m_str_Name + " Entered catch2 for:"+obValAgent.GetSlowAgent());
				}
				//Gets back all the values from the slow agent
				setEqnNames = m_hmp_Trans_hs_Msg.get(obValAgent.GetSlowAgent()).GetValue();
				
				if(PRINT_MST_MESSAGES)
				{
					System.out.println("Agent: "+ m_str_Name + " Left catch2");
				}
			}
			
			//Dispatch back all the values
			Iterator<String> it = m_hmp_Trans_hs_Msg.keySet().iterator();
			 
			while(it.hasNext())
			{
				String strNeigh = it.next();
				if(obValAgent.GetSlowAgent() == strNeigh) 
				{continue;}
				
				m_hmp_Trans_hs_Msg.get(strNeigh).SetValue(setEqnNames , true);
			}
		}	
		
		
		m_STN.GenerateConstraintEquation(m_str_Name , setEqnNames);
		
		if(SANITY_CHECK)
		{
			if(false == SanityCheckFor_B_Matrix(setEqnNames))
			{
				System.out.println("B Matrix failed in communication");
			}
						
			final int f_iNumVertices = m_STN.GetNumOfVertics();
			final int f_iNumTriangulatConstraints = f_iNumVertices * (f_iNumVertices - 1) * (f_iNumVertices-2);
			
			if(f_iNumTriangulatConstraints !=  m_STN.GetNumOfTriConEqns())
			{
				System.out.println("Agent: "+m_str_Name+" vertices: "+m_STN.GetNumOfVertics());				
				System.out.println("Agent: "+m_str_Name+", Number of triangular Consistency eqns: " + m_STN.GetNumOfTriConEqns());
			}
		}
		
		
		
		
	}
	
	private boolean SanityCheckFor_B_Matrix(HashSet<String> setEqnNames)
	{
		return m_STN.SanityCheckFor_B_Matrix(setEqnNames);		
	}
}
