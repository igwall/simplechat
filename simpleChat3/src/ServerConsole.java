import common.ChatIF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerConsole implements ChatIF 
{
	//Class variables *************************************************
 
	/**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;

	/**
	 * Instance of the EchoServer to connect to.
	 */
	EchoServer server;
	
	//Constructors ****************************************************

	/**
	 * Create the server console.
	 * @param port The port to start the server on.
	 */
	public ServerConsole(int port) 
	{
		this.server = new EchoServer(port, this);
	}

	//Instance methods ************************************************
	
	/**
	 * Display a message on the server UI.
	 * @param message The message to display.
	 */
	public void display(String message) 
	{
		System.out.println(message);
	}

	/**
	 * Accept input from the server UI.
	 */
	protected void accept() 
	{
		BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
		String message = "";
		
		while (true) 
		{
			try 
			{
				message = fromConsole.readLine();
			} catch (IOException e) {
				System.out.println(e.getStackTrace());
			}
			
			server.handleMessageFromServerUI(message);
		}
	}

	/**
	 * This method is responsible for the creation of the Server UI.
	 *
	 * @param args[0] The port to connect to.
	 */
	public static void main(String[] args) {
		int port = DEFAULT_PORT; // The port number

		try {
			port = Integer.parseInt(args[0]);
		} catch (ArrayIndexOutOfBoundsException e) { }

		ServerConsole serverConsole = new ServerConsole(port);

		try {
			serverConsole.server.listen(); // Start listening for connections
		} catch (Exception ex) {
			serverConsole.display("ERROR - Could not listen for clients!");
		}

		serverConsole.accept(); // Wait for console data
	}
}