package control_structure;

public class LoginException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2792817236121371120L;
	public LoginException() { super(); }
	public LoginException(String message) { super(message); }
	public LoginException(String message, Throwable cause) { super(message, cause); }
	public LoginException(Throwable cause) { super(cause); }
}
