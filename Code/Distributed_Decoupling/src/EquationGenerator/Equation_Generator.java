package EquationGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import Network_Related.Inter_agent_constraint;
import Network_Related.STN;
import Network_Related.STN_Edge;
import Network_Related.STN_Vertex;


public class Equation_Generator extends STN{

	private HashMap<Integer, String> m_hp_SlackVars = null;
	private HashMap<Integer, Equation> m_hp_Tri_Con_eqns = null;
	private HashMap<Integer, Equation> m_hp_Coup_Con_eqns = null;
	private HashMap<String , Integer> m_hp_Edge_To_Int = null;
	
	public Equation_Generator() 
	{
		super();
		
		int iVertexCount = m_hp_Vertices.size();
		final int f_iNumEqns = (int)((iVertexCount * (iVertexCount - 1) * (iVertexCount - 2)));
		final int f_iHashMapSize = (int)(4 * f_iNumEqns/3) + 1;   // This is assuming 75% load factor for HashMap
		
		m_hp_SlackVars = new HashMap<Integer, String>(f_iHashMapSize);
		m_hp_Tri_Con_eqns = new HashMap<Integer, Equation>(f_iHashMapSize);
		
		final int f_iCoupEqns = (int)(4 * m_hp_Coupling_Edge.size()/3) + 1;
		m_hp_Coup_Con_eqns = new HashMap<Integer, Equation>(f_iCoupEqns);
		
		final int f_iEdgeCount = (int)(4 * m_hp_Intra_Edge.size()/3) + 1;
		m_hp_Edge_To_Int = new HashMap<String , Integer>(f_iEdgeCount);
		
		GenerateVariableMapping();
	}
	
	private void GenerateVariableMapping()
	{
		Iterator<Map.Entry<String , STN_Edge>> it = m_hp_Intra_Edge.entrySet().iterator();
		
		int iIndex = 0;
		while(it.hasNext())
		{
			m_hp_Edge_To_Int.put( it.next().getKey() , iIndex);			
			iIndex++;
		}
	}
	
	public void GenerateConstraintEquation(String strAgentName , HashSet<String> hp_B_Mat)
	{
		Generate_A_i_Matrix(strAgentName);
		Generate_B_i_Matrix(strAgentName, hp_B_Mat);
	}
	
	private void Generate_A_i_Matrix(String strAgentName)
	{
		Iterator<Map.Entry<String, STN_Vertex>> it1 = m_hp_Vertices.entrySet().iterator();
		String strVtx1 , strVtx2, strVtx3;
		String strEdge12 , strEdge32 , strEdge13;
		Var_Coeff varEdge12 = null, varEdge32 = null , varEdge13 = null, varSlack = null;
		Vector<Var_Coeff> vecCoeffEqns = new Vector<Var_Coeff>(4);
		
		String strSlackName;		
		
		int iSlackIndex = m_hp_SlackVars.size();
		
		while(it1.hasNext())
		{
			Map.Entry<String, STN_Vertex> enVal1 = it1.next();
			strVtx1 = enVal1.getKey();
			
			Iterator<Map.Entry<String, STN_Vertex>> it2 = m_hp_Vertices.entrySet().iterator();
						
			while(it2.hasNext())
			{
				Map.Entry<String, STN_Vertex> enVal2 = it2.next();
				strVtx2 = enVal2.getKey();
				
				if(strVtx2.equals(strVtx1))
				{
					continue;
				}
				
				strEdge12 = strVtx1 + "_"+strAgentName+"__"+strVtx2+"_"+strAgentName;
				
				varEdge12 = new Var_Coeff(strEdge12 , 1);
				
				Iterator<Map.Entry<String, STN_Vertex>> it3 = m_hp_Vertices.entrySet().iterator();
			
				while(it3.hasNext())
				{
					Map.Entry<String, STN_Vertex> enVal3 = it3.next();
					strVtx3 = enVal3.getKey();
					
					if(strVtx3.equals(strVtx2))
					{
						continue;
					}
					else if(strVtx3.equals(strVtx1))
					{
						continue;
					}
					
					strEdge32 = strVtx3 + "_"+strAgentName+"__"+strVtx2+"_"+strAgentName;
					varEdge32 = new Var_Coeff(strEdge32 , -1);
					
					strEdge13 = strVtx1 + "_"+strAgentName+"__"+strVtx3+"_"+strAgentName;
					varEdge13 = new Var_Coeff(strEdge13 , -1);
					
					strSlackName = strEdge12+"<"+strEdge13+"+"+strEdge32;
					varSlack = new Var_Coeff(strSlackName, 1);
					
					if(null !=m_hp_SlackVars.put(iSlackIndex, strSlackName))
					{
						System.out.println("Messed up slack indices for A matrix in Agent: " + strAgentName);
					}
					
					vecCoeffEqns.add(varEdge12);
					vecCoeffEqns.add(varEdge13);
					vecCoeffEqns.add(varEdge32);
					
					m_hp_Tri_Con_eqns.put(iSlackIndex, new Equation(vecCoeffEqns, varSlack, 0));
					
					iSlackIndex++;
				}
			}
		}
	}
	
	private void Generate_B_i_Matrix(String strAgentName , HashSet<String> hp_B_Mat)
	{
		Iterator<String> it = hp_B_Mat.iterator();
		String strVar , strEdge;
		int iRow;
		String strSlackName;	
		String strOtherAgentName;
		Inter_agent_constraint obj = null;
		Var_Coeff varSlack = null;
		
		iRow = 0;
		int iSlackIndex = m_hp_SlackVars.size();
		
		while(it.hasNext())
		{
			String strCoupEdgeName = it.next();
			
			obj = m_hp_Coupling_Edge.get(strCoupEdgeName);
			
			if(null == obj)
			{
				iRow++;
				continue;
			}
			
			if(true == obj.isResponsible())
			{
				strVar = obj.GetVertex_1_Name();
				strEdge = strVar+"_"+strAgentName+"__"+"z"+"_"+strAgentName;
			}
			else
			{
				strVar = obj.GetVertex_2_Name();
				strEdge = "z"+"_"+strAgentName+"__"+strVar+"_"+strAgentName;
			}
			
			strOtherAgentName = obj.GetCoupledAgentName();
			
			Vector<Var_Coeff> vec_STN_edges = new Vector<Var_Coeff>(2);
			vec_STN_edges.add(new Var_Coeff(strEdge, 1));
			
			if(true == obj.isResponsible())
			{
				strSlackName = strEdge +"-"+"z"+"_"+strOtherAgentName+"__"+obj.GetVertex_2_Name()+"_"+strOtherAgentName;	
				varSlack = new Var_Coeff(strSlackName, 1);
				
				if(null != m_hp_SlackVars.put(iSlackIndex, strSlackName))
				{
					System.out.println("Slack indices messed up in B matrix for Agent: "+ strAgentName);
				}
				
				m_hp_Coup_Con_eqns.put(iRow, new Equation(vec_STN_edges, varSlack, obj.GetConstraintValue()));
				
				iSlackIndex++;
			}
			else
			{
				m_hp_Coup_Con_eqns.put(iRow, new Equation(vec_STN_edges, null , 0));
			}
			
			iRow++;			
		}
	}
	
	public int GetNumOfTriConEqns()
	{
		return m_hp_Tri_Con_eqns.size();
	}
	
	public int GetNumOfCouplingEqns()
	{
		return m_hp_Coup_Con_eqns.size();
	}
}
