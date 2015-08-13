package EquationGenerator;

public class Var_Coeff
{
	private String m_StrVtx ;
	private double m_dCoeff ;
	
	Var_Coeff(String strVtx , double dCoeff)
	{
		m_StrVtx = strVtx;
		m_dCoeff = dCoeff;
	}
	
	public String ReturnVarName()
	{
		return m_StrVtx;
	}
	
	public double ReturnCoeff()
	{
		return m_dCoeff;
	}
	
	public boolean isVarPresent(String strVar)
	{
		if(m_StrVtx.equals(strVar))
		{
			return true;
		}
		
		return false;
	}
	
	public String toString()
	{
		return m_dCoeff +" "+m_StrVtx;
	}
}