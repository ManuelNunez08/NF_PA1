import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class client {
    // Initialize socket and input/output streams
    private Socket connectionSocket = null;
    private DataInputStream consoleInput = null;
    private DataInputStream serverInput = null;
    private DataOutputStream serverOutput = null;

    // Constructor with server address and port
    public client(String serverAddress, int serverPort) {
        // Attempt to establish a connection
        try {
            connectionSocket = new Socket(serverAddress, serverPort);

            // Input from terminal
            consoleInput = new DataInputStream(System.in);

            // Output to the server
            serverOutput = new DataOutputStream(connectionSocket.getOutputStream());

            // Input from the server
            serverInput = new DataInputStream(new BufferedInputStream(connectionSocket.getInputStream()));

        } catch (UnknownHostException u) {
            System.out.println(u);
            return;
        } catch (IOException i) {
            System.out.println(i);
            return;
        }

        // Initial message from server
        String welcomeMessage = "";
        try {
            welcomeMessage = serverInput.readUTF();
            System.out.println(welcomeMessage);
        } catch (IOException i) {
            System.out.println(i);
            return;
        }

        // Communication variables
        String userInput = "";
        String serverResponse = "";

        // Continue communication until "disconnected" is received
        while (!serverResponse.equals("exit")) {
            try {
                userInput = consoleInput.readLine();

                if (!serverResponse.equals("exit")) {
                    serverOutput.writeUTF(userInput);
                    serverResponse = serverInput.readUTF();
                    System.out.println(serverResponse);
                }

            } catch (IOException i) {
                System.out.println(i);
            }
        }

        // Close all connections
        try {
            consoleInput.close();
            serverOutput.close();
            connectionSocket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Please provide valid initilization arguments.");
        } else {
            int port = Integer.valueOf(args[0]);
            //For local comms, IP = 127.0.0.1
            String IP = args[1];
            new client(IP, port);
            
        }
    }
}
