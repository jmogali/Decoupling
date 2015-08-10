package Network_Related;

public class Inter_agent_constraint extends STN_Edge{

	private final String m_str_Other_Agent;
	private final boolean m_bResponsible;
	
	public Inter_agent_constraint(String strVtx1, String strVtx2 , String strAgentName , boolean bResp) 
	{
		super(strVtx1, strVtx2);
		m_str_Other_Agent = strAgentName;
		m_bResponsible = bResp;
	}	
	
	public Inter_agent_constraint(String strVtx1, String strVtx2 , double dConstraint , String strAgentName , boolean bResp) 
	{
		super(strVtx1, strVtx2 , dConstraint);
		m_str_Other_Agent = strAgentName;
		m_bResponsible = bResp;
	}	

	public String GetCoupledAgentName()
	{
		return m_str_Other_Agent;
	}
	
	public boolean isResponsible()
	{
		return m_bResponsible;
	}
}
