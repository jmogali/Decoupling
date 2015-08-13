package Distributed_Communication;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class Synch_Object<T> {

	static final int f_iNUM_OF_CORR_AGENTS = 2;
	private volatile boolean m_bFinalize;
    private int m_iUpdates;
    private int m_iMsgRead;
    
    private T m_tData;
    
    public Synch_Object() {
    	m_bFinalize = false;
    	m_tData = null;
    	m_iUpdates = 0;
		m_iMsgRead = 0;
	}
    
    public synchronized T GetValue()
    {
    	while(!m_bFinalize)
    	{
    		try
    		{
    			wait();
    		}catch(InterruptedException e)
    		{
    			System.out.println("Woken Up");
    		}
    	}
    	    
    	T tVal = T_clone(m_tData);
		
    	m_iMsgRead++;
    	
    	if(f_iNUM_OF_CORR_AGENTS == m_iMsgRead)
    	{
    		ResetObject();
    	}
    	
    	notify();
    	return tVal;
    }
    
    public synchronized void SetValue(T tVal , boolean bFinalSum)
    {
    	if(bFinalSum)
    	{   // I think this step helps the garbage collector
    		m_tData = null;
    		m_tData = T_clone(tVal);
    		m_bFinalize = true;
    		m_iMsgRead++;
    		notify();
    		return;
    	}
    	else
    	{
	    	m_tData = AddVariables(m_tData, tVal);
    		m_iUpdates++;
    	}
    	
    	if(f_iNUM_OF_CORR_AGENTS == m_iUpdates)
    	{
    		m_bFinalize = true;
    		notify();
    	}    	
    }
    
    private void ResetObject()
    {
    	m_bFinalize = false;
    	m_tData = null;
    	m_iUpdates = 0;
    	m_iMsgRead = 0;
    }
    
    @SuppressWarnings({"unchecked" , "rawtypes"})
    private T T_clone(T d){
        try{
          for (Class i: d.getClass().getInterfaces())
            if (i.getName().equals("java.lang.Cloneable"))
              for (java.lang.reflect.Method m : d.getClass().getMethods())
                if (m.getName().equals("clone") &&
                    m.getParameterTypes().length==0 &&
                    m.getReturnType().getName().equals("java.lang.Object"))
                  return (T)m.invoke(d);}
        catch(IllegalAccessException ex){} // This should never happen.
        catch(InvocationTargetException ex){} // This should never happen.
        return d;     
    }
    
    @SuppressWarnings("unchecked")
    private T AddVariables(T v1 , T v2)
    {
    	if(null == v1)
    	{
    		return T_clone(v2);
    	}
    	else if(v2.getClass() == Integer.class)
    	{
    		return (T) (Integer) ((Integer) v1 + (Integer) v2);
    	}
    	else if((v2.getClass() == HashSet.class))
    	{
    		HashSet<String> set_Eqns = new HashSet<String>();
    		
    		set_Eqns.addAll((Set<String>) v1);
    		set_Eqns.addAll((Set<String>) v2);
    		
    		return (T) set_Eqns;
    	}
    	
    	return v2; // temporary, need to change
    }
}