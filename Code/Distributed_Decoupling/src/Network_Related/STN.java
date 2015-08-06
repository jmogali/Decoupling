package Network_Related;

import java.util.HashMap;

public class STN {

	private HashMap<String, STN_Vertex> m_hp_Vertices = null;   // Map containing all vertices
	private HashMap<String, STN_Edge> m_hp_Edges = null;  // Map containing all edges
	boolean m_bModified; // Used to check if any constraints are modified
	
	public STN()
	{
		m_hp_Vertices = new HashMap<String, STN_Vertex>();
		m_hp_Edges = new HashMap<String, STN_Edge>();
		m_bModified = true;
	}
	
	
}