import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    // Initialize server socket and client connection
    private Socket clientSocket = null;
    private ServerSocket jokeServer = null;
    private DataInputStream clientInput = null;
    private DataOutputStream clientOutput = null;

    // Constructor with server port
    public Server(int port) {
        // Joke files location
        String Joke1Path = "Joke1.txt";
        String Joke2Path = "Joke2.txt";
        String Joke3Path = "Joke3.txt";

        // Start server and wait for a connection
        try {
            jokeServer = new ServerSocket(port);
            System.out.println("JokeServer started");

            System.out.println("Waiting for a client ...");

            clientSocket = jokeServer.accept();

            // Input from client
            clientInput = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            // Output to client
            clientOutput = new DataOutputStream(clientSocket.getOutputStream());

            System.out.println("Client connected");

            String command = "";
            clientOutput.writeUTF("Hello! Type 'Joke 1', 'Joke 2', or 'Joke 3' to get a joke; type 'bye' to exit.");

            // Process commands from client
            while (!command.equals("bye")) {
                try {
                    // Read jokes from files
                    BufferedReader jokeReader = new BufferedReader(new FileReader(Joke1Path));
                    String joke1 = jokeReader.readLine();
                    jokeReader = new BufferedReader(new FileReader(Joke2Path));
                    String joke2 = jokeReader.readLine();
                    jokeReader = new BufferedReader(new FileReader(Joke3Path));
                    String joke3 = jokeReader.readLine();
                    jokeReader.close();

                    // Handle client requests
                    command = clientInput.readUTF();

                    switch (command) {
                        case "Joke 1":
                            System.out.println("Sending Joke 1...");
                            clientOutput.writeUTF(joke1);
                            break;
                        case "Joke 2":
                            System.out.println("Sending Joke 2...");
                            clientOutput.writeUTF(joke2);
                            break;
                        case "Joke 3":
                            System.out.println("Sending Joke 3...");
                            clientOutput.writeUTF(joke3);
                            break;
                        case "bye":
                            clientOutput.writeUTF("disconnected");
                            break;
                        default:
                            clientOutput.writeUTF("Invalid command. Please try 'Joke 1', 'Joke 2', 'Joke 3', or 'bye'.");
                            break;
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
            }

            // Close resources
            clientSocket.close();
            clientInput.close();
            clientOutput.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        int port = 4010;
        new Server(port);
    }
}
