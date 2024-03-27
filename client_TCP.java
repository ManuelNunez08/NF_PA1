import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class client_TCP {
    // Initialize socket and input/output streams
    private Socket connectionSocket = null;
    private DataInputStream consoleInput = null;
    private DataInputStream serverInput = null;
    private DataOutputStream serverOutput = null;

    // Constructor with server address and port
    public client_TCP(String serverAddress, int serverPort) {

        // Create a set to track received image numbers
        Set<Integer> receivedImages = new HashSet<>();
        Random rand = new Random();

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

        // Continue communication until "disconnected" is received
        while (receivedImages.size() < 10) {
            try {
                int imageNumber = rand.nextInt(10) + 1;
                if (!receivedImages.contains(imageNumber)) {
                    receivedImages.add(imageNumber);
                }
                // Send image request
                serverOutput.writeUTF("joke" + imageNumber + ".png");

                long fileSize = serverInput.readLong();

                if (fileSize > 0) {
                    // Receive and save the image
                    try (FileOutputStream fos = new FileOutputStream("received_jokes/joke" + imageNumber + ".png")) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        long remaining = fileSize;
                        while ((bytesRead = serverInput.read(buffer, 0,
                                (int) Math.min(buffer.length, remaining))) > 0) {
                            fos.write(buffer, 0, bytesRead);
                            remaining -= bytesRead;
                        }
                    }
                    System.out.println("Received joke" + imageNumber + ".png");
                }

                if (receivedImages.size() == 10) {
                    serverOutput.writeUTF("bye");
                }

            } catch (IOException i) {
                System.out.println(i);
                break;
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
        if (args.length != 2) {
            System.out.println("Please provide valid initilization arguments.");
        } else {
            int port = Integer.valueOf(args[0]);
            // For local comms, IP = 127.0.0.1
            String IP = args[1];
            new client_TCP(IP, port);

        }
    }
}
