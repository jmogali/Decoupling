package Distributed_Communication;

public class NoNewUpdateException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String m_Sender;
	
	NoNewUpdateException(String strAgent)
	{
		m_Sender = strAgent;
	}

	public String toString()
	{
		return "No New Updates from : " + m_Sender;
	}
}
