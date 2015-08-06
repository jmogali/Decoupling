package DIMACS_Parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;


//This class is meant for only for parsing only DIMACS format
public class Parser {

	private class IntegerPair
	{
		private int m_iNum1 , m_iNum2;
		
		public IntegerPair(int iNum1 , int iNum2) 
		{
			m_iNum1 = iNum1;
			m_iNum2 = iNum2;
		}
		
		public int GetInt1(){return m_iNum1;}
		
		public int GetInt2(){return m_iNum2;}
		
		 public boolean equals(IntegerPair o)
		 {
		     if(o == null)  
		     {return false;}
			 
			 if( (this.m_iNum1 != o.m_iNum1) || (this.m_iNum2 != o.m_iNum2) )
			 {
				 return false;
			 }
			 
			 return true;
		 }
		 
		 public int hashCode()
		 {
			 return Integer.valueOf(String.valueOf(m_iNum1)+String.valueOf(m_iNum2)+String.valueOf(Math.abs(m_iNum1 - m_iNum2)) );
		 }
	}
	
	private HashMap<Integer, String> m_hmp_Int_to_String_Labels = null;
	private HashMap<String , Integer> m_hmp_String_to_Int_Labels = null;
	private HashMap<String , Integer> m_hmp_Vtx_Owner = null;
	private HashMap<IntegerPair, Double> m_hmp_Edge_Weights = null;
	private int m_iNum_Agents;
	private int m_iVtxCntChecker , m_iEdgeChecker; 
	
	public Parser()
	{
		m_hmp_Int_to_String_Labels = new HashMap<Integer, String>();
		m_hmp_String_to_Int_Labels = new HashMap<String, Integer>();
		m_hmp_Vtx_Owner = new HashMap<String, Integer>();
		m_hmp_Edge_Weights = new HashMap<Parser.IntegerPair, Double>();
		
		m_iNum_Agents= 0;
		m_iVtxCntChecker = -1;
		m_iEdgeChecker = -1;
	}
	
	public boolean ParseFile(String strFilePath) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(strFilePath));
		String line = "";	    
		
		while((line = br.readLine()) != null)
		{
			String[] words = line.split(" ");
			
			if(words.length < 2)
			{continue;}
			
			if(words[0].equals("c"))
			{
				if(words[1].equals("<label>"))
				{
					SetLabel(words);
				}
				else if( (words[1].equals("<num_agents>")) && words.length == 3 )
				{
					SetNumOfAgents(words);
				}
				else if(words[1].equals("<own>"))
				{
					AssignOwnerShip(words);
				}
				else if(words[1].equals("<refine>"))
				{
					SetConstraintValue(words);
				}
				else if( (words[1].equals("<num_refinements>")) && (words.length == 3) )
				{
					m_iEdgeChecker = Integer.valueOf(words[2]);
				}
				else if(words.length >= 5)
				{
					if((words[2].equals("vertex")) && (words[3].equals("labels")) && (words[4].equals("follow:")))
					{
						m_iVtxCntChecker = Integer.valueOf(words[1]);
					}
				}
			}
		}
		
		br.close();
		
		if(false == CheckParsing())
		{
			return false;
		}
		
		return true;
	}
	
	
	private void SetLabel(String[] words)
	{
		int iLabel = Integer.valueOf(words[words.length - 2]);
		String str = words[words.length - 1];
		
		m_hmp_Int_to_String_Labels.put(iLabel, str);
		m_hmp_String_to_Int_Labels.put(str, iLabel);
	}
	
	private void SetNumOfAgents(String[] words)
	{
		m_iNum_Agents = Integer.valueOf(words[2]) - 1;  // DIMACS considers reference as another agent, so subtract it
	}
	
	private void AssignOwnerShip(String[] words)
	{
		int iOwner = Integer.valueOf(words[words.length - 2]);
		String str = words[words.length - 1];
		
		m_hmp_Vtx_Owner.put(str, iOwner);
	}
	
	private void SetConstraintValue(String[] words)
	{
		int iNum1 = Integer.valueOf(words[words.length - 3]);
		int iNum2 = Integer.valueOf(words[words.length - 2]);
		double dConstraint = Double.valueOf(words[words.length - 1]);
		
		m_hmp_Edge_Weights.put( new IntegerPair(iNum1, iNum2), dConstraint);
	}
	
	private boolean CheckParsing()
	{
		if( (m_iVtxCntChecker != m_hmp_Vtx_Owner.size()) || 
		    (m_iVtxCntChecker != m_hmp_Int_to_String_Labels.size()) ||
		    (m_iVtxCntChecker != m_hmp_String_to_Int_Labels.size()) )
		{
			System.out.println("m_iVtxCntChecker:" + m_iVtxCntChecker);
			
			System.out.println("Returned 1");
			return false;
		}
		else if(m_iEdgeChecker != m_hmp_Edge_Weights.size())
		{
			System.out.println("Returned 2");
			return false;
		}	  
		else if(-1 == m_iEdgeChecker)
		{
			System.out.println("Returned 3");
			return false;
		}
		else if(-1 == m_iVtxCntChecker)
		{
			System.out.println("Returned 4");
			return false;
		}
			
		return true;
	}
}
