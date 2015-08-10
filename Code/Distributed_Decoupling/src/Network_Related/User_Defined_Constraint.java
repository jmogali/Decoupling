package Network_Related;

public class User_Defined_Constraint {

	// m_clVtx1 - m_clVtx2 + slack = m_dVal
	protected final String m_strVtx1 , m_strVtx2;
	protected final double m_dVal;
	
	protected final String m_strName;
	
	public User_Defined_Constraint(String strName , String strVtx1 , String strVtx2 , double dVal)
	{
		m_strVtx1 = strVtx1;
		m_strVtx2 = strVtx2;
		m_dVal = dVal;
		m_strName = strName;
	}
	
	public String GetVertex1Name()
	{
		return m_strVtx1;
	}
	
	public String GetVertex2Name()
	{
		return m_strVtx2;
	}
	
	public String GetName()
	{
		return m_strName;
	}
	
	public double GetConstraintValue()
	{
		return m_dVal;
	}
}
