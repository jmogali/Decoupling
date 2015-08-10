package DIMACS;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import Network_Related.User_Defined_Constraint;


//This class is meant for only for parsing only DIMACS format
public class Parser {

	protected HashMap<Integer, String> m_hp_Int_to_String_Labels = null;
	protected HashMap<String , Integer> m_hp_String_to_Int_Labels = null;
	protected HashMap<String , String> m_hp_Vtx_Owner = null; // First string gives vertex name, Second string gives owner name
	protected HashMap<String, User_Defined_Constraint> m_hp_Edge_Weights = null; // String format: Owner1_Owner2
	protected HashMap<String , Boolean> m_hp_Vtx_Shared = null;
	protected SortedSet<String> m_ss_Agent_Names = null;
	protected int m_iNum_Agents;
	protected int m_iVtxCntChecker , m_iEdgeChecker; 
	
	public Parser()
	{
		m_hp_Int_to_String_Labels = new HashMap<Integer, String>();
		m_hp_String_to_Int_Labels = new HashMap<String, Integer>();
		m_hp_Vtx_Owner = new HashMap<String, String>();
		m_hp_Edge_Weights = new HashMap<String, User_Defined_Constraint>();
		m_hp_Vtx_Shared = new HashMap<String, Boolean>();
		m_ss_Agent_Names = new TreeSet<String>();
		
		m_iNum_Agents= 0;
		m_iVtxCntChecker = -1;
		m_iEdgeChecker = -1;
	}
	
	public boolean ParseFile(String strFilePath) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(strFilePath));
		String line = "";	    
		int iLineNum = 0; 
		
		while((line = br.readLine()) != null)
		{
			String[] words = line.split(" ");
			iLineNum++;
			
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
					SetConstraintValue(words , iLineNum);
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
		
		m_hp_Int_to_String_Labels.put(iLabel, str);
		m_hp_String_to_Int_Labels.put(str, iLabel);
	}
	
	private void SetNumOfAgents(String[] words)
	{
		m_iNum_Agents = Integer.valueOf(words[2]) - 1;  // DIMACS considers reference as another agent, so subtract it
	}
	
	private void AssignOwnerShip(String[] words)
	{
		String strOwnerName = words[words.length - 2];
		String str = words[words.length - 1];
		
		m_hp_Vtx_Owner.put(str, strOwnerName);
		
		m_ss_Agent_Names.add(strOwnerName);
	}
	
	private void SetConstraintValue(String[] words , int iLineNum)
	{
		int iNum1 = Integer.valueOf(words[words.length - 3]);
		int iNum2 = Integer.valueOf(words[words.length - 2]);
		double dConstraint = Double.valueOf(words[words.length - 1]);
		
		String strVtx1 , strVtx2;
		
		strVtx1 = m_hp_Int_to_String_Labels.get(iNum1);
		strVtx2 = m_hp_Int_to_String_Labels.get(iNum2);
		
		String strOwner1 = m_hp_Vtx_Owner.get(strVtx1);
		String strOwner2 = m_hp_Vtx_Owner.get(strVtx2);
		
		String strConsName = String.valueOf(iLineNum);
		
		if(!strOwner1.equals(strOwner2) )
		{
			m_hp_Vtx_Shared.put(strVtx1, true);
			m_hp_Vtx_Shared.put(strVtx2, true);
		}
		
		String stKey = strVtx1+"_"+ strOwner1 + "__"+strVtx2 +"_"+ strOwner2;
		
		m_hp_Edge_Weights.put( stKey, new User_Defined_Constraint(strConsName, strVtx1, strVtx2, dConstraint));
	}
	
	private boolean CheckParsing()
	{
		if( (m_iVtxCntChecker != m_hp_Vtx_Owner.size()) || 
		    (m_iVtxCntChecker != m_hp_Int_to_String_Labels.size()) ||
		    (m_iVtxCntChecker != m_hp_String_to_Int_Labels.size()) )
		{
			System.out.println("m_iVtxCntChecker:" + m_iVtxCntChecker);
			
			System.out.println("Returned 1");
			return false;
		}
		else if(m_iEdgeChecker != m_hp_Edge_Weights.size())
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
	
	protected int GetNumOfAgents()
	{
		return m_iNum_Agents;
	}
}
