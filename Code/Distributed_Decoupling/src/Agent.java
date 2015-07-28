import java.util.HashSet;
import java.util.Set;


public class Agent implements Runnable{

	private String m_str_Name;
	private Set<String> m_st_Comm_Neigh_Names; // Contains communicating neighbor names after Min Spanning Tree
	private COMM_Interface objComm;	
	
	Agent(String stName , COMM_Interface Comm)
	{
		m_str_Name = stName;
		objComm = Comm;
		m_st_Comm_Neigh_Names = new HashSet<String>();
	}
	
	public String GetName()
	{
		return m_str_Name;
	}
	
	public void AddCommNeighbour(String strNeigh)
	{
		m_st_Comm_Neigh_Names.add(strNeigh);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
