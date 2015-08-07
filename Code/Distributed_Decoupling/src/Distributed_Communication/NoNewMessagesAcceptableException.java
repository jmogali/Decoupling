package Distributed_Communication;

public class NoNewMessagesAcceptableException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String m_stMessageBufferOwner;

	public NoNewMessagesAcceptableException(String stOwner) {
		m_stMessageBufferOwner = stOwner;
	}
	
	public String toString()
	{
		return m_stMessageBufferOwner;
	}
	
}
