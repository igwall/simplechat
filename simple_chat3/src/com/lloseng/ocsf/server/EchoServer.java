package com.lloseng.ocsf.server;

import java.io.IOException;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

import java.io.*;

import com.lloseng.ocsf.client.ObservableClient;
import com.lloseng.ocsf.common.ChatIF;
import com.lloseng.ocsf.server.*;

import java.util.Observable;
import java.util.Observer;

/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer implements Observer {
    //Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;
    ChatIF serverUI;
    ObservableServer observableServer;
    public int port;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port, ChatIF serverUI)
    {
        this.observableServer = new ObservableServer(port);
        this.observableServer.addObserver(this);
        this.serverUI = serverUI;
        this.port = port;
    }


    //Instance methods ************************************************

    public void cmdHandler(String cmd, ConnectionToClient client){
        switch (cmd) {
            case ("logout"):
                try {
                   client.close();
                } catch (IOException e) {
                    e.getStackTrace();
                }
                break;
        }
    }



    public void handleMessageFromServerUI(String message)
    {
        if(message.charAt(0) == ChatIF.COMMAND_SYMBOL){
            String cmd = message.split(message, ChatIF.COMMAND_SYMBOL)[1];
            switch (cmd) {
                case "quit":
                    observableServer.sendToAllClients("!disconnect");
                    try{
                        observableServer.close();
                    }
                    catch(IOException e1){
                        System.out.println("Error while disconnecting clients. ");
                    }
                case "stop" :
                    observableServer.stopListening();
                    serverStopped();
                case "close" :
                    observableServer.sendToAllClients("!disconnected");
                    observableServer.stopListening();
                    try{ observableServer.close(); }catch(IOException e){ e.getStackTrace();}
                    serverStopped();
                case "setport" :
                    observableServer.setPort(Integer.parseInt(cmd));
                case "getport" :
                    System.out.println(observableServer.getPort());
                case "start" :
                    if(observableServer.isListening()){
                        System.out.println("Couldn't start the server, already running !");
                    }
                    else{
                        try {
                            observableServer.listen();
                        }
                        catch(IOException e){
                            e.getStackTrace();
                        }
                    }
            }
        }else{
            try
            {
                observableServer.sendToAllClients(message);
            }
            catch(Exception e){
                serverUI.display("Could not send message to all the clients...");
                e.getStackTrace();
            }
        }

    }

    /**
     * This method handles any messages received from the client.
     *
     * @param msg The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient (Object msg, ConnectionToClient client) {
        String message = (String) msg;
        if(message.charAt(0) == ChatIF.COMMAND_SYMBOL){
            cmdHandler(message.split(message, ChatIF.COMMAND_SYMBOL)[1], client);
        } else {
            System.out.println("Message received: " + msg + " from " + client);
            observableServer.sendToAllClients(msg);
        }
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server starts listening for connections.
     */
    protected void serverStarted()
    {
        System.out.println
                ("Server listening for connections on port " + observableServer.getPort());
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    protected void serverStopped()
    {
        System.out.println
                ("Server has stopped listening for connections.");
    }

    public void clientConnected(ConnectionToClient client){
        System.out.println("Un nouveau client s'est connecté");
    }

    public void clientDisconnected(ConnectionToClient client){
        System.out.println("Client disconnected");
    }

    //Class methods ***************************************************

    /**
     * This method is responsible for the creation of
     * the server instance (there is no UI in this phase).
     *
     * @param args The port number to listen on.  Defaults to 5555
     *          if no argument is entered.
     */
    public static void main(String[] args)
    {
        int port = 0; //Port to listen on

        try
        {
            port = Integer.parseInt(args[0]); //Get port from command line
        }
        catch(Throwable t)
        {
            port = DEFAULT_PORT; //Set port to 5555
        }

        ChatIF serverUI = new ServerConsole(port);
        EchoServer sv = new EchoServer(port, serverUI);

        try
        {
            sv.observableServer.listen(); //Start listening for connections
        }
        catch (Exception ex)
        {
            System.out.println("ERROR - Could not listen for clients!");
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
//End of EchoServer class