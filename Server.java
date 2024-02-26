import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
    // initialize socket and input streams
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    // constructor with port
    public server(int port) {



        // Get the directories of the joke files
        // Directory was: "C:/Users/Nash/IdeaProjects/CIS 4912 Networking/src/Joke1.txt"
        String Joke1_File = "src/Joke1.txt";
        String Joke2_File = "src/Joke2.txt";
        String Joke3_File = "src/Joke3.txt";



       // starts server and waits for a connection
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = server.accept();
            System.out.println("Client accepted");

            // takes input from the client socket
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            // takes input in from the client
            out = new DataOutputStream(socket.getOutputStream());

            String line = "";

            // reads client messages until "bye" is sent
            while (!line.equals("STOP")) {
                //Sets the actual joke stings from the txt files into variables
                BufferedReader reader = new BufferedReader(new FileReader(Joke1_File));
                String  Joke1 = reader.readLine();
                reader = new BufferedReader(new FileReader(Joke2_File));
                String Joke2 = reader.readLine();
                reader = new BufferedReader(new FileReader(Joke3_File));
                String Joke3 = reader.readLine();
                reader.close();

                //Receives user input and sends corresponding joke based on what user inputs
                try {
                    line = in.readUTF();

                    if (line.equals("Joke 1")) {
                        System.out.print("Request for Joke 1 Received: Sending Joke1.txt ...\n");
                        out.writeUTF(Joke1);
                    } else if (line.equals("Joke 2")) {
                        System.out.print("Request for Joke 2 Received: Sending Joke2.txt ...\n");
                        out.writeUTF(Joke2);
                    } else if (line.equals("Joke 3")) {
                        System.out.print("Request for Joke 3 Received: Sending Joke3.txt ...\n");
                        out.writeUTF(Joke3);
                    } else if (line.equals("bye")) {
                        //If the user types in break, the server input loop breaks and will then type "disconnected."
                        //out.writeUTF("disconnected.\n");
                        break;
                    } else {
                        out.writeUTF("Invalid Request. Please try again");
                    }

                } catch (IOException i) {
                    System.out.println(i);
                }
            }
            System.out.println("disconnected");

            // close connection
            socket.close();
            in.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String args[]) {
        // int port = Integer.valueOf(args[0]);
        int port = 4010;
        server server = new server(port);
    }
}
