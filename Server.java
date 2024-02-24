import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    // initialize socket and input stream
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    // constructor with port
    public Server(int port) {
        // starts server and waits for a connection
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = server.accept();
            System.out.println("Client accepted");

            // takes input from the client socket
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            // takes input in from the client
            out = new DataOutputStream(
                    socket.getOutputStream());

            String line = "";

            // reads message from client until "Over" is sent
            while (!line.equals("STOP")) {
                try {
                    line = in.readUTF();

                    if (line.equals("Joke 1")) {
                        System.out.print("Request for Joke 1 Received: Sending...\n");
                        out.writeUTF("Joke1 Sent");
                    } else if (line.equals("Joke 2")) {
                        System.out.print("Request for Joke 2 Received: Sending...\n");
                        out.writeUTF("Joke2 Sent");
                    } else if (line.equals("Joke 2")) {
                        System.out.print("Request for Joke 3 Received: Sending...\n");
                        out.writeUTF("Joke2 Sent");
                    } else if (line.equals("STOP")) {
                        out.writeUTF("Closing Connection.\n");
                    } else {
                        out.writeUTF("Invalid Request. Please try again");
                    }

                } catch (IOException i) {
                    System.out.println(i);
                }
            }
            System.out.println("Closing connection");

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
        Server server = new Server(port);
    }
}

/*
 * if (line.equals("Joke 1")){
 * out.writeUTF("Joke1 Sent");
 * }
 * else if (line.equals("Joke 2")) {
 * out.writeUTF("Joke2 Sent");
 * }
 * else if (line.equals("Joke 2")) {
 * out.writeUTF("Joke2 Sent");
 * }
 * else{
 * out.writeUTF("Invalid Request. Please try again");
 * }
 */