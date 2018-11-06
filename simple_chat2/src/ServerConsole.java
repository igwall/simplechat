import client.ChatClient;
import common.ChatIF;
import common.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerConsole implements ChatIF {

    final public static int DEFAULT_PORT = 5555;

    EchoServer server;


    public ServerConsole( int port)
    {
        this.server= new EchoServer(port, this);
    }

    public void display(String message)
    {
        System.out.println("!user>" + message);
    }

    public void accept() {

        BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
        String message = "";
        while (true) {
            try{
                message = fromConsole.readLine();
            }catch(IOException eio){
                System.out.println(eio.getStackTrace());
            }
                server.handleMessageFromServerUI(message);
        }
    }

    public static void main(String[] args) {
        int port = 5555;  //The port number
        ServerConsole serverConsole = new ServerConsole(5555);
        serverConsole.display("Server is running : ");

        try
        {
           serverConsole.server.listen(); //Start listening for connections
            serverConsole.display("server is listening");

        }
        catch (Exception ex)
        {
            serverConsole.display("ERROR - Could not listen for clients!");
        }
        serverConsole.accept();  //Wait for console data
    }
}
