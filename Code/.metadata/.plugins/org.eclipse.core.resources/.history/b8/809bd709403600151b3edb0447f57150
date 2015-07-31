
public class NoNewUpdateException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String m_Receiver;
	private String m_Sender;
	
	NoNewUpdateException(String stAgent_agent)
	{
		String parts[] = stAgent_agent.split("_");
		m_Receiver = parts[0];
		m_Sender = parts[1];
	}

	public String toString()
	{
		return "No New Updates from : " + m_Sender + " to: "+ m_Receiver;
	}
}
