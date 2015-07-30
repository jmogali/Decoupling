import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public class Agent implements Runnable{

	private String m_str_Name;
	private COMM_Interface m_objComm;
	public Thread t;
	
	private HashMap<String , COMM_Interface> m_st_Comm_Neighs; // Contains communicating neighbor names after Min Spanning Tree
	private int m_iVal;
	private long m_dDelay;
	
	Agent(String stName , COMM_Interface Comm , int iValue , long dDelay)
	{
		m_str_Name = stName;
		m_objComm = Comm;
		m_st_Comm_Neighs = new HashMap<String , COMM_Interface>();
		m_iVal = iValue;
		m_dDelay = dDelay;
		
		t = new Thread(this , m_str_Name);
	}
	
	public String GetName()
	{
		return m_str_Name;
	}
	
	public void AddCommNeighbour(String strAgentName,COMM_Interface objNeigh)
	{
		m_st_Comm_Neighs.put(strAgentName , objNeigh);
	}

	public COMM_Interface Get_Contact_Info()
	{
		return m_objComm;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	
		System.out.println("Agent"+m_str_Name+"initialized");
		try {
			ComputeSum();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Experimental Function
	private void ComputeSum() throws InterruptedException
	{
		int iTemp , iSum = m_iVal;
		
		Thread.sleep(m_dDelay);                
				
		for(Map.Entry<String , COMM_Interface> entry : m_st_Comm_Neighs.entrySet())
		{
		    entry.getValue().Insert_Entry(m_str_Name , m_iVal);
		}
		
		Set<String> Neigh_to_respond = m_st_Comm_Neighs.keySet();
		
		while(Neigh_to_respond.size() != 0)
		{
			Iterator<String> iter = Neigh_to_respond.iterator();
			
			while (iter.hasNext()) 
			{
				boolean bNewValue = true;
			    
				try {
			    	iTemp = m_objComm.Get_Value(iter.next());
				  } catch (NoNewUpdateException e) {
					//System.out.println(e);  
					bNewValue = false;
					iTemp = 0;
				} catch (ConnectionNotEstablished e) {
					//System.out.println(e);
					bNewValue = false;
					iTemp = 0;
				}
			    
			    if(bNewValue)
			    {
			    	iter.remove();
			    }
			    
			    iSum = iSum + iTemp;
			}
			
			Thread.sleep(200);			
		}
		
		System.out.println("Agent Name:" + m_str_Name +" sum:"+iSum);
		
	}
	
	
	
}
