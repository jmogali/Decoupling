import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class MessagePassingQueue {

	private HashMap<String, Integer> m_hmpMesages;
	private Set<String> m_keys_neighs;
	
	final int f_iCapacity ;
	private String m_stAgOwner;
	
	public class Value_Slow_Agent
	{
		private int m_iValue;
		private String m_stAgent;
		
		Value_Slow_Agent(int iValue , String stAgent)
		{
			m_iValue = iValue;
			m_stAgent = stAgent;			
		}
		
		public int GetValue()
		{
			return m_iValue;
		}
		
		public String GetSlowAgent()
		{
			return m_stAgent;
		}
	}
	
	public MessagePassingQueue(int c_iSize , String stOwner ,Set<String> keys_neighs) 
	{
		m_hmpMesages = new HashMap<String, Integer>(c_iSize);
		f_iCapacity = c_iSize;
		m_stAgOwner = stOwner;
		m_keys_neighs = keys_neighs;
	}
	
	public synchronized Value_Slow_Agent GetSum()
	{
		while(m_hmpMesages.size() < f_iCapacity)
		{
			try{
				wait();
			}catch(InterruptedException e){
				System.out.println("Thread " + m_stAgOwner + " Awakened");
			}
		}
		
		int iSum = 0;
		
		Set<String> keyNeighs = new HashSet<String>();
		keyNeighs.addAll(m_keys_neighs);
		
		Set<String> keySet = m_hmpMesages.keySet();
		Iterator<String> iter = keySet.iterator();
		
		String stKey;
		
		while(iter.hasNext())
		{
			stKey = iter.next();
		
			iSum = iSum + m_hmpMesages.get(stKey);
			keyNeighs.remove(stKey);
		}
		
		return new Value_Slow_Agent(iSum , keyNeighs.iterator().next());
	}
	
	public synchronized void AddElement(String stAgent , int iValue) throws NoNewMessagesAcceptableException
	{
		if(m_hmpMesages.size() == f_iCapacity)
		{
			notify();
			throw new NoNewMessagesAcceptableException(m_stAgOwner);
		}
		
		m_hmpMesages.put(stAgent, iValue);
		notify();
	}
}
