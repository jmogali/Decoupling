package Network_Related;

public class STN_Vertex 
{
	final private String m_str_Name;
	final private boolean m_bShared;
		
	public STN_Vertex(String str_Name , boolean bShared) 
	{
		m_str_Name = str_Name;
		m_bShared = bShared;
	}
	
	public String Get_Name()
	{
		return m_str_Name;
	}
	
	public boolean isShared()
	{
		return m_bShared;
	}

}
