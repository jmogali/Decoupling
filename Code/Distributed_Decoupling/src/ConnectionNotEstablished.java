
public class ConnectionNotEstablished extends Exception{

	private static final long serialVersionUID = 1L;
	private String m_Sender;
	
	ConnectionNotEstablished(String stAgent)
	{
		m_Sender = stAgent;
	}

	public String toString()
	{
		return "No Connection from : " + m_Sender;
	}

}
