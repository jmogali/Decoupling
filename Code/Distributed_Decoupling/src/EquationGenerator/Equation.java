package EquationGenerator;

import java.util.Vector;

public class Equation {
	
	private Vector<Var_Coeff> m_vec_Coeffs = null;
	private Var_Coeff m_cl_Slack = null;
	private double m_dConstraint;
	
	public Equation( Vector<Var_Coeff> vec_STN_edges , Var_Coeff stSlack , double dConstraint )
	{
		m_vec_Coeffs = vec_STN_edges;
		m_cl_Slack = stSlack;
		m_dConstraint = dConstraint;
	}

	public double VarCoeff(String strVarName) throws VariableNotPresentInEquation
	{
		for( int iCount = 0; iCount < m_vec_Coeffs.size(); iCount++ )
		{
			Var_Coeff stVar = m_vec_Coeffs.get(iCount);
			
			if(stVar.isVarPresent(strVarName))
			{
				return stVar.ReturnCoeff();
			}
		}
		
		if(m_cl_Slack.isVarPresent(strVarName))
		{
			return m_cl_Slack.ReturnCoeff();
		}
		
		throw new VariableNotPresentInEquation(strVarName , this);
	}
	
	public double ReturnEqnValue()
	{
		return m_dConstraint;
	}
	
	public String toString()
	{
		String strEqn = "";
		
		for( int iCount = 0; iCount < m_vec_Coeffs.size(); iCount++ )
		{
			Var_Coeff stVar = m_vec_Coeffs.get(iCount);
			strEqn = strEqn + stVar.toString() + "  ";
		}
		
		strEqn = strEqn + m_cl_Slack.toString();
		strEqn = strEqn + " = "+m_dConstraint;
		
		return strEqn;
	}
	
	//returned variables includes slack
	public Vector<Var_Coeff> GetAllCoeffs()
	{
		Vector<Var_Coeff> vecEqCoeffs = new Vector<Var_Coeff>(m_vec_Coeffs.size() + 1);
		
		vecEqCoeffs.addAll(m_vec_Coeffs);
		vecEqCoeffs.add(m_cl_Slack);
		
		return vecEqCoeffs;
	}
	
	public Var_Coeff ReturnSlack()
	{
		return m_cl_Slack; 
	}
}
