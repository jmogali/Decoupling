package Distributed_Communication;

public class GenericClassNotSupportedException<T>{

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private final T target;
	
	public GenericClassNotSupportedException(T t) throws Exception{
		this.target = t;
		throw new Exception(toString());
	}
	
	public String toString()
	{
		return "Unsupported class " + target.getClass() + " and type " + target.getClass().getComponentType() ;
	}
}
