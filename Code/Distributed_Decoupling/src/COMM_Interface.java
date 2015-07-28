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
	
	public synchronized int Get_Value(String stAgent_agent) throws NoNewUpdateException 
	{
		if(!m_hmp_Sync_Objects_Change.get(stAgent_agent))
		{
			try{
				wait(200);
					
				if(!m_hmp_Sync_Objects_Change.get(stAgent_agent))
				{
					throw new NoNewUpdateException(stAgent_agent);	
				}
			}catch(InterruptedException e){
				System.out.println("Thanks for waking me up!!!");
			}		
		}
		
		m_hmp_Sync_Objects_Change.put(stAgent_agent, false);
		return m_hmp_Sync_Objects.get(stAgent_agent);
	}
	
	public synchronized void Insert_Entry(String stAgent_agent , Integer iVal)
	{
		// Old value is just replaced with new Value in case of HashMaps
		
		m_hmp_Sync_Objects.put(stAgent_agent, iVal); 	
		m_hmp_Sync_Objects_Change.put(stAgent_agent, true);
		
		notify();
	}	
}
