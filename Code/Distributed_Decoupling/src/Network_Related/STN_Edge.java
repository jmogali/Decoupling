package Network_Related;

public class STN_Edge {

	public static final double f_Infty = Double.POSITIVE_INFINITY;
	
	protected String m_Vtx1 = null, m_Vtx2 = null;
	protected double m_dConstraint;
	
	public STN_Edge(String strVtx1 , String strVtx2 )
	{
		m_Vtx1 = strVtx1;
		m_Vtx2 = strVtx2;
		m_dConstraint = f_Infty;
	}
	
	public STN_Edge(String strVtx1 , String strVtx2 , double dConstrtVal )
	{
		m_Vtx1 = strVtx1;
		m_Vtx2 = strVtx2;
		m_dConstraint = dConstrtVal;
	}
	
	public double GetConstraintValue()
	{
		return m_dConstraint;
	}
	
	public void SetValue(double dConstraint)
	{
		m_dConstraint = dConstraint;
	}
	
	public String GetVertex_1_Name()
	{
		return m_Vtx1;
	}
	
	public String GetVertex_2_Name()
	{
		return m_Vtx2;
	}
	
	public String GetKey()
	{
		return m_Vtx1 + "_" + m_Vtx2;
	}
}
