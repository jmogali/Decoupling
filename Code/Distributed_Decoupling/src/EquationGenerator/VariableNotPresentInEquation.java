package EquationGenerator;

public class VariableNotPresentInEquation extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String m_strVtx;
	public Equation m_stEqn = null; 
	
	public VariableNotPresentInEquation(String strVtx , Equation stEqn)
	{
		m_strVtx = strVtx;
		m_stEqn = stEqn;
	}
	
	public String toString()
	{
		return "Vertex" +m_strVtx + " Not present in equation: "+m_stEqn.toString();
	}
}
