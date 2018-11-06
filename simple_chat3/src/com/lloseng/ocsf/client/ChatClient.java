package com.lloseng.ocsf.client;

import com.lloseng.ocsf.common.ChatIF;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class ChatClient implements Observer {

    private ChatIF clientUI;
    private ObservableClient observableClient;
    private String name;
    private String host;
    private int port;

    // On enregistre l'observer dans l'observable
    public ChatClient(String host, int port, ChatIF chatUI){
        this.observableClient = new ObservableClient(host,port);
        this.observableClient.addObserver(this);
        this.clientUI = chatUI;
        this.host = host;
        this.port = port;
    }


    @Override
    public void update(Observable o, Object arg) {

        if(arg instanceof Exception){
            this.connectionException();
        } else {
            String msg = (String) arg;
            if(msg.equals( ObservableClient.CONNECTION_CLOSED)){
                connectionClosed();
            }
            else if(msg.equals(ObservableClient.CONNECTION_ESTABLISHED)){
                System.out.println("Connection established OH MY GOD");
            }
        }
        handleMessageFromServer(arg);
    }

    public void connectionClosed() {
        clientUI.display("Server disconnected! Terminating client.");
    }

    public void connectionException() {
        clientUI.display("Connection had an error! Terminating client.");
    }

    /**
     * This method handles all data that comes in from the server.
     *
     * @param msg The message from the server.
     */
    public void handleMessageFromServer(Object msg)
    {
        String str = (String) msg;
        clientUI.display(str);
        if(str.charAt(0) == '!'){
            cmdServerHandler(str.split(str, ChatIF.COMMAND_SYMBOL)[1]);
        }
        else if(str.charAt(0) == '&'){
            str.substring(1);
            clientUI.display("Server MSG> " + str);
        }
        else{
            clientUI.display(str);
        }
    }

    /**
     * This method handles all data coming from the UI
     *
     * @param message The message from the UI.
     */
    public void handleMessageFromClientUI(String message)
    {
        // Si le message commence par un #, on traite la commande
        if(message.charAt(0) == ChatIF.COMMAND_SYMBOL){
            cmdClientHandler(message.split(message, ChatIF.COMMAND_SYMBOL)[1]);
        } else if (observableClient.isConnected()) {
            try {

                this.observableClient.sendToServer(message);
            } catch (IOException e) {
                clientUI.display
                        ("Could not send message to server.  Terminating client.");
            }
        }
    }

    public void setName(String name){
        this.name = name;
    }

    public void cmdClientHandler(String cmd){
        switch (cmd){
            case ("quit") :
                // We ask the server for a clean quit
                askToDisconnect();
                break;

            case "login":
                if(observableClient.isConnected()){
                    System.out.println("Already connected to a server, please disconnect before");
                } else{
                    String[] args = cmd.split(" ");
                    this.setName(args[1]);
                    this.setHost(args[2]);
                    this.setPort(Integer.parseInt(args[3]));

                    try{
                        observableClient.openConnection();
                    } catch(IOException ex){
                        System.out.println(ex.getStackTrace());
                    }

                }

            break;
        }
    }

    public void setPort(int port){
        this.port = port;
    }

    public void setHost(String host){
        this.host = host;

    }

    public void cmdServerHandler(String cmd) {
        switch (cmd) {
            case "disconnect":
                quit();
                break;
        }
    }


    /**
     * This method terminates the client.
     */
    public void quit() {
        try
        {
            observableClient.closeConnection();
        }
        catch(IOException e) {}
        System.exit(0);
    }


    /**
     * Ask the server for a nice disconnection.s
     */
    public void askToDisconnect(){
        try{
            observableClient.sendToServer("#logout");
        } catch (IOException e1){
            e1.getStackTrace();
        }
    }
}
