package simplechat;

import common.ChatIF;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;

public class ClientFX extends Application implements ChatIF {

    //Class variables *************************************************

    /**
     * The default port to connect on.
     */
    final public static int DEFAULT_PORT = 5555;

    //Instance variables **********************************************

    /**
     * The instance of the client that created this ConsoleChat.
     */
    ChatClient client;

    int nbMessage = 0;
    ScrollPane scrollPane;
    GridPane gridChat;


    //Constructors ****************************************************

    /**
     * Constructs an instance of the simplechat.ClientConsole UI.
     */
    public ClientFX()
    {
        /*
        try
        {
            client= new ChatClient(host, port, this);
        }
        catch(IOException exception)
        {
            System.out.println("Error: Can't setup connection!"
                    + " Terminating client.");
            System.exit(1);
        }
        */
    }



    //Instance methods ************************************************

    //Create window
    public void start(Stage primaryStage) throws Exception{


        gridChat = new GridPane();
        gridChat.setAlignment(Pos.TOP_LEFT);
        gridChat.setHgap(10);
        gridChat.setVgap(10);
        gridChat.setPadding(new Insets(25, 25, 40, 25));

        scrollPane = new ScrollPane();
        scrollPane.setContent(gridChat);

        BorderPane mainPane = new BorderPane();
        HBox hbox = new HBox();

        TextField messageTextField = new TextField();

        messageTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                if (key.getCode().equals(KeyCode.ENTER)) {
                    client.handleMessageFromClientUI(messageTextField.getText());
                    messageTextField.setText("");
                }
            }
        });

        hbox.getChildren().add(messageTextField);

        mainPane.setCenter(scrollPane);
        mainPane.setBottom(messageTextField);

        Scene chatScene = new Scene(mainPane, 450, 600);

        /*
        Text scenetitle2 = new Text("Welcome to Simplechat 4");
        scenetitle2.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        gridChat.add(scenetitle2, 0, 0, 2, 1);
        */


        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 450, 350);


        Text scenetitle = new Text("Welcome to Simplechat 4");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        //User
        Label userName = new Label("Username:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        // Host
        Label pw = new Label("Host:");
        grid.add(pw, 0, 2);

        TextField host = new TextField();
        grid.add(host, 1, 2);

        // Port
        Label portLabel = new Label("Port:");
        grid.add(portLabel, 0, 3);

        TextField port = new TextField();
        grid.add(port, 1, 3);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        ChatIF clientUI = this;

        btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    client = new ChatClient(host.getText(), Integer.parseInt(port.getText()), clientUI);
                    client.handleMessageFromClientUI("#login " + userTextField.getText());
                    primaryStage.setScene(chatScene);
                } catch (IOException e) {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Can't connect to the server...");
                }
            }
        });


        userTextField.setText("igwall");
        port.setText("5555");
        host.setText("localhost");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * This method waits for input from the console.  Once it is
     * received, it sends it to the client's message handler.
     */
    public void accept()
    {
        try
        {
            BufferedReader fromConsole =
                    new BufferedReader(new InputStreamReader(System.in));
            String message;

            while (true)
            {
                message = fromConsole.readLine();
                client.handleMessageFromClientUI(message);
            }
        }
        catch (Exception ex)
        {
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
    public void display(String message)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Text textMessage = new Text(message);
                gridChat.add(textMessage, 1, nbMessage);
                nbMessage++;
                scrollPane.setVvalue(1.0);
            }
        });
    }


    //Class methods ***************************************************

    /**
     * This method is responsible for the creation of the Client UI.
     *
     * @param args[0] The host to connect to.
     */
    public static void main(String[] args)
    {
        String host = "";
        int port = 0;  //The port number

        try
        {
            host = args[0];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            host = "localhost";
        }
        ClientFX chat= new ClientFX();
        launch(args);
        chat.accept();  //Wait for console data
    }



}
