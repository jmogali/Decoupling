package Distributed_Communication;
import java.util.HashMap;


// Inter-agent shared memory class
public class COMM_Interface {

	private HashMap<String, Integer> m_hmp_Sync_Objects;
	private HashMap<String, Boolean> m_hmp_Sync_Objects_Change;
	
	public COMM_Interface() 
	{		
		m_hmp_Sync_Objects = new HashMap<String, Integer>();
		m_hmp_Sync_Objects_Change = new HashMap<String, Boolean>();
	}
	
	public synchronized int Get_Value(String strAgent) throws NoNewUpdateException, ConnectionNotEstablished 
	{
		try
		{
			if(!m_hmp_Sync_Objects_Change.get(strAgent))
			{
				throw new NoNewUpdateException(strAgent);				 	
			}
		}catch(NullPointerException e){
			
			throw new ConnectionNotEstablished(strAgent);	
		}
		
		m_hmp_Sync_Objects_Change.put(strAgent, false);
		int iVal = m_hmp_Sync_Objects.get(strAgent);
		
	    return iVal;
	}
	
	public synchronized void Insert_Entry(String strAgent_agent , Integer iVal)
	{
		// Old value is just replaced with new Value in case of HashMaps
		
		m_hmp_Sync_Objects.put(strAgent_agent, iVal); 	
		m_hmp_Sync_Objects_Change.put(strAgent_agent, true);
	
	}	
}
