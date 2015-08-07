import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import Agent_Related.Agent;
import Agent_Related.Agent_Type;
import DIMACS.Parser;
import Distributed_Communication.MessagePassingQueue;
import Distributed_Communication.Synch_Object;


public class Main {

	public static void main(String[] args) throws InterruptedException
	{
		
		Parser myParser = new Parser();
		String strFilePath = "E:\\Research\\STN\\Version_Controller\\Decoupling\\Data\\BDH_Problem_Instance\\bdh-agent-problems\\16_10_50_750_45.dimacs";
		try {
			boolean bSuccess = myParser.ParseFile(strFilePath);
			System.out.println(bSuccess);			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Failed to parse");
		}
		
		
		Set<String> keys_neighs_A = new HashSet<String>();
		keys_neighs_A.add("C");
		
		Set<String> keys_neighs_B = new HashSet<String>();
		keys_neighs_B.add("C");
		
		Set<String> keys_neighs_C = new HashSet<String>();
		keys_neighs_C.add("A");
		keys_neighs_C.add("B");
		keys_neighs_C.add("D");
		keys_neighs_C.add("E");
		
		Set<String> keys_neighs_D = new HashSet<String>();
		keys_neighs_D.add("C");
		keys_neighs_D.add("F");
		
		Set<String> keys_neighs_E = new HashSet<String>();
		keys_neighs_E.add("C");
		
		Set<String> keys_neighs_F = new HashSet<String>();
		keys_neighs_F.add("D");
		keys_neighs_F.add("G");
		
		Set<String> keys_neighs_G = new HashSet<String>();
		keys_neighs_G.add("F");
		
		MessagePassingQueue MsgQue_A = new MessagePassingQueue(0, "A", keys_neighs_A); 
		Agent a = new Agent("A", 7, 2000, MsgQue_A , Agent_Type.LEAF );
		
		MessagePassingQueue MsgQue_B = new MessagePassingQueue(0, "B", keys_neighs_B); 
		Agent b = new Agent("B", 11, 100, MsgQue_B , Agent_Type.LEAF );
		
		MessagePassingQueue MsgQue_C = new MessagePassingQueue(3, "C", keys_neighs_C);
		Agent c = new Agent("C", 13, 1200, MsgQue_C , Agent_Type.NON_LEAF );
		
		MessagePassingQueue MsgQue_D = new MessagePassingQueue(1, "D", keys_neighs_D);
		Agent d = new Agent("D", 2, 2200, MsgQue_D , Agent_Type.NON_LEAF );
		
		MessagePassingQueue MsgQue_E = new MessagePassingQueue(0, "E", keys_neighs_E);
		Agent e = new Agent("E", 91, 2200, MsgQue_E , Agent_Type.LEAF );
		
		MessagePassingQueue MsgQue_F = new MessagePassingQueue(1, "F", keys_neighs_F);
		Agent f = new Agent("F", 19, 900, MsgQue_F , Agent_Type.NON_LEAF );
		
		MessagePassingQueue MsgQue_G = new MessagePassingQueue(0, "G", keys_neighs_G);
		Agent g = new Agent("G", 16, 1700, MsgQue_G , Agent_Type.LEAF );
		
		Synch_Object obj_ac = new Synch_Object();
		Synch_Object obj_bc = new Synch_Object();
		Synch_Object obj_cd = new Synch_Object();
		Synch_Object obj_ce = new Synch_Object();
		Synch_Object obj_df = new Synch_Object();
		Synch_Object obj_fg = new Synch_Object();
		
		a.AddCommNeighbour("C", obj_ac, MsgQue_C);
		
		b.AddCommNeighbour("C", obj_bc, MsgQue_C);
		
		c.AddCommNeighbour("A", obj_ac, MsgQue_A);
		c.AddCommNeighbour("B", obj_bc, MsgQue_B);
		c.AddCommNeighbour("D", obj_cd, MsgQue_D);
		c.AddCommNeighbour("E", obj_ce, MsgQue_E);
		
		d.AddCommNeighbour("C", obj_cd, MsgQue_C);
		d.AddCommNeighbour("F", obj_df, MsgQue_F);
		
		e.AddCommNeighbour("C", obj_ce, MsgQue_C);
		
		f.AddCommNeighbour("D", obj_df, MsgQue_D);
		f.AddCommNeighbour("G", obj_fg, MsgQue_G);
		
		g.AddCommNeighbour("F", obj_fg, MsgQue_F);
		
		
		a.t.start();
		b.t.start();
		c.t.start();
		d.t.start();
		e.t.start();
		f.t.start();
		g.t.start();
				
		try{
			a.t.join();
			b.t.join();
			c.t.join();
			d.t.join();
			e.t.join();
			f.t.join();
			g.t.join();			
		}catch(InterruptedException exp){
			System.out.println(exp);
		}		
	}
}
