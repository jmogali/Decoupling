
public class Synch_Object {

	final int f_iNUM_OF_CORR_AGENTS = 2;
	private int m_iVal;
    private volatile boolean m_bFinalize;
    private int m_iUpdates;
    private int m_iMsgRead;
    
    public Synch_Object() {
		m_bFinalize = false;
		m_iVal = 0;
		m_iUpdates = 0;
		m_iMsgRead = 0;
	}
    
    public synchronized int GetValue()
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
    	    	
    	int iVal = m_iVal;
    	m_iMsgRead++;
    	
    	if(f_iNUM_OF_CORR_AGENTS == m_iMsgRead)
    	{
    		ResetObject();
    	}
    	
    	notify();
    	return iVal;
    }
    
    public synchronized void SetValue(int iVal , boolean bFinalSum)
    {
    	if(bFinalSum)
    	{
    		m_iVal = iVal;
    		m_bFinalize = true;
    		m_iMsgRead++;
    		notify();
    		return;
    	}
    	else
    	{
	    	m_iVal = m_iVal + iVal;
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
    	m_iVal = 0;
    	m_iUpdates = 0;
    	m_iMsgRead = 0;
    }
    
}