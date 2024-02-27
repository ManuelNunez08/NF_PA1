import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    // Initialize socket and input/output streams
    private Socket connectionSocket = null;
    private DataInputStream consoleInput = null;
    private DataInputStream serverInput = null;
    private DataOutputStream serverOutput = null;

    // Constructor with server address and port
    public Client(String serverAddress, int serverPort) {
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
        while (!serverResponse.equals("disconnected")) {
            try {
                userInput = consoleInput.readLine();

                if (!serverResponse.equals("disconnected")) {
                    serverOutput.writeUTF(userInput);
                    serverResponse = serverInput.readUTF();
                    System.out.println(serverResponse);
                }

            } catch (IOException i) {
                System.out.println(i);
            }
        }
        System.out.println("exit.");

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
        int port = 4010;
        new Client("127.0.0.1", port);
    }
}
