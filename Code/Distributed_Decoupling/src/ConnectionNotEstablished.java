
public class ConnectionNotEstablished extends Exception{

	private static final long serialVersionUID = 1L;
	private String m_Sender;
	
	ConnectionNotEstablished(String strAgent)
	{
		m_Sender = strAgent;
	}

	public String toString()
	{
		return "No Connection from : " + m_Sender;
	}

}
