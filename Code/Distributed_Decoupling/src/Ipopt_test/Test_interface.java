package Ipopt_test;

public class Test_interface {

	public static void main(String args[])
	{
		  Example hs071 = new Example();
		  
		  // solve the problem
		  int status = hs071.OptimizeNLP();
		  
		  // print the status and optimal value
		  System.out.println("Status    = " + status);
		  System.out.println("Obj Value = " + hs071.getObjectiveValue());
	}
}