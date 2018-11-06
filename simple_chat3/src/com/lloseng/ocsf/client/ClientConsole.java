package com.lloseng.ocsf.client;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Observable;
import com.lloseng.ocsf.common.*;
public class ClientConsole implements ChatIF {

    private ChatClient client;

    public ClientConsole(){
        this.client = new ChatClient(this);
    }

    @Override
    public void display(String message) {
        System.out.println(message);
    }


    //Instance methods ************************************************

    /**
     * This method waits for input from the console.  Once it is
     * received, it sends it to the client's message handler.
     */
    public void accept() {
        try
        {
            BufferedReader fromConsole =
                    new BufferedReader(new InputStreamReader(System.in));
            String message;

            while (true) {
                message = fromConsole.readLine();
                client.handleMessageFromClientUI(message);
            }
        }
        catch (Exception ex) {
            System.out.println
                    ("Unexpected error while reading from console!");
        }
    }

    /**
     * This method overrides the method in the ChatIF interface.  It
     * displays a message onto the screen.
     *
     * @param message The string to be displayed.
     */


    //Class methods ***************************************************

    /**
     * This method is responsible for the creation of the Client UI.
     * @param args The host to connect to.
     * @param args The port to connect to.
     */


    public static void main(String[] args){
        ClientConsole clientConsole = new ClientConsole();
        clientConsole.accept();
    }
}
//End of ConsoleChat class
