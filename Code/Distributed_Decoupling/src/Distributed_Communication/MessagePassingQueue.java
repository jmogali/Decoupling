package Distributed_Communication;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class MessagePassingQueue<T> {

	private HashMap<String, T> m_hm_Mesages;
	private HashSet<String> m_keys_neighs;
	
	final int f_iCapacity ;
	private String m_stAgOwner;
	
	public class Value_Slow_Agent
	{
		private T m_tVal;
		private String m_stAgent;
		
		Value_Slow_Agent(T tVal , String stAgent)
		{
			m_tVal = T_clone(tVal);
			m_stAgent = stAgent;			
		}
		
		public T GetValue()
		{
			return T_clone(m_tVal);
		}
		
		public String GetSlowAgent()
		{
			return m_stAgent;
		}
	}
	
	public MessagePassingQueue(int c_iSize , String stOwner , HashSet<String> keys_neighs) 
	{
		m_hm_Mesages = new HashMap<String, T>(c_iSize);
		f_iCapacity = c_iSize;
		m_stAgOwner = stOwner;
		m_keys_neighs = keys_neighs;
	}
	
	public synchronized Value_Slow_Agent GetSum() throws Exception
	{
		while(m_hm_Mesages.size() < f_iCapacity)
		{
			try{
				wait();
			}catch(InterruptedException e){
				System.out.println("Thread " + m_stAgOwner + " Awakened");
			}
		}
		
		HashSet<String> keyNeighs = new HashSet<String>();
		keyNeighs.addAll(m_keys_neighs);
		
		T tVal = null;
		tVal = PerformOperation(m_hm_Mesages , keyNeighs);
		
		return new Value_Slow_Agent(tVal , keyNeighs.iterator().next());
	}
	
	public synchronized void AddElement(String stAgent , T tVal) throws NoNewMessagesAcceptableException
	{
		if(m_hm_Mesages.size() == f_iCapacity)
		{
			notify();
			throw new NoNewMessagesAcceptableException(m_stAgOwner);
		}
		
		m_hm_Mesages.put(stAgent, tVal);
		notify();
	}
	
	@SuppressWarnings({"unchecked" , "rawtypes"})
	private T PerformOperation(HashMap<String, T> hm_Mesages , Set<String> keyNeighs) throws Exception
	{
		if(hm_Mesages.isEmpty())
		{
			throw new HashMapEmptyException();
		}
		
		String[] vecKeys = hm_Mesages.keySet().toArray(new String[hm_Mesages.size()]);
		
		Class clName = hm_Mesages.get(vecKeys[0]).getClass();
		
		if(clName == Integer.class)
		{	
			Integer iSum = 0;
			
			for(int iCount = 0; iCount < vecKeys.length ; iCount++)
			{
				iSum = iSum + (Integer)hm_Mesages.get(vecKeys[iCount]);
				keyNeighs.remove(vecKeys[iCount]);
			}
			
			return T_clone( (T) iSum);
		}
		else if( (clName == Set.class) && (clName.getTypeName().equals(String.class.getName())) )
		{
			Set<String> set_Eqns = new HashSet<String>();
			
			for(int iCount = 0; iCount < vecKeys.length ; iCount++)
			{
				set_Eqns.addAll((Set<String>)hm_Mesages.get(vecKeys[iCount]) );
				keyNeighs.remove(vecKeys[iCount]);
			}
			
			return (T)set_Eqns;
		}
		else
		{
			return (T) new GenericClassNotSupportedException(hm_Mesages.get(vecKeys[0]));
		}
	}
	
	@SuppressWarnings({"unchecked" , "rawtypes"})
	private T T_clone(T tVal){
        try{
          for (Class i: tVal.getClass().getInterfaces())
            if (i.getName().equals("java.lang.Cloneable"))
              for (java.lang.reflect.Method m : tVal.getClass().getMethods())
                if (m.getName().equals("clone") &&
                    m.getParameterTypes().length==0 &&
                    m.getReturnType().getName().equals("java.lang.Object"))
                  return (T)m.invoke(tVal);}
        catch(IllegalAccessException ex){} // This should never happen.
        catch(InvocationTargetException ex){} // This should never happen.
        return tVal;     
    }
}
