import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private Socket clientSocket = null;
    private ServerSocket jokeServer = null;
    private DataInputStream clientInput = null;
    private DataOutputStream clientOutput = null;

    public Server(int port) {
        String Joke1Path = "Joke1.txt";
        String Joke2Path = "Joke2.txt";
        String Joke3Path = "Joke3.txt";

        try {
            jokeServer = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");
            clientSocket = jokeServer.accept();

            // Capture client's IP and print connection message
            String clientIP = clientSocket.getInetAddress().getHostAddress();
            System.out.println("Client connection requested from " + clientIP);

            clientInput = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            clientOutput = new DataOutputStream(clientSocket.getOutputStream());

            String command = "";
            clientOutput.writeUTF("Hello! Type 'Joke 1', 'Joke 2', or 'Joke 3' to get a joke; type 'bye' to exit.");

            while (!command.equals("bye")) {
                try {
                    BufferedReader jokeReader = new BufferedReader(new FileReader(Joke1Path));
                    String joke1 = jokeReader.readLine();
                    jokeReader = new BufferedReader(new FileReader(Joke2Path));
                    String joke2 = jokeReader.readLine();
                    jokeReader = new BufferedReader(new FileReader(Joke3Path));
                    String joke3 = jokeReader.readLine();
                    jokeReader.close();

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
                            System.out.println("Disconnected");
                            clientOutput.writeUTF("exit");
                            break;
                        default:
                            clientOutput.writeUTF("Invalid command. Please try 'Joke 1', 'Joke 2', 'Joke 3', or 'bye'.");
                            break;
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
            }

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
