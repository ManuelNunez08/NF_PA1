import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class client_TCP {

    // Initialize socket and input/output streams
    private Socket connectionSocket = null;
    private DataInputStream consoleInput = null;
    private DataInputStream serverInput = null;
    private DataOutputStream serverOutput = null;

    // Constructor with server address and port
    public client_TCP(String serverAddress, int serverPort, List<Long> RTT_Aggregates, List<Long> TCP_Setup) {

        // Create a set to track received image numbers
        Set<Integer> receivedImages = new HashSet<>();
        Random rand = new Random();

        // Attempt to establish a connection and measure the time taken
        long Track_TCP_Connection = System.nanoTime();
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
        long TCPSetupTimeResult = (System.nanoTime() - Track_TCP_Connection);

        // used to store RTTS
        List<Long> RTTs = new ArrayList<>();

        // Continue communication until all images are received
        while (receivedImages.size() < 10) {
            try {

                // randomly generate image number and add to set
                int imageNumber = rand.nextInt(10) + 1;
                if (!receivedImages.contains(imageNumber)) {
                    receivedImages.add(imageNumber);
                }

                // Start individual timer for one joke image request
                long Inv_Start = System.nanoTime();

                // Send image request
                serverOutput.writeUTF("joke" + imageNumber + ".png");

                // receive image
                long fileSize = serverInput.readLong();

                // calculate time taken to request and receive image
                long Inv_StartResult = (System.nanoTime() - Inv_Start);

                // add to sum
                RTTs.add(Inv_StartResult);

                // read file and situate in folder
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
                }

                // break condition
                if (receivedImages.size() == 10) {
                    serverOutput.writeUTF("bye");
                }

            } catch (IOException i) {
                System.out.println(i);
                break;
            }
        }

        // update RTTs list
        Long RTT_Sum = RTTs.stream().mapToLong(Long::longValue).sum();
        RTT_Aggregates.add(RTT_Sum);
        TCP_Setup.add(TCPSetupTimeResult);

        // Print Out iteration Statistics
        double RTT_Roundded = Math.round((RTT_Sum / 1000000.0) *   1000) / 1000.0;
        double TCP_Roundded = Math.round((TCPSetupTimeResult/ 1000000.0) * 1000) / 1000.0;
        System.out.println("Sum of Round Trip Times: " + RTT_Roundded);
        System.out.println("TCP Setup Time: " + TCP_Roundded);
        System.out.println();

        ArrayStatistics stats = new ArrayStatistics();
        System.out.println("RTT Statistics:");
        stats.print_stats(RTTs);

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

            ArrayStatistics stats = new ArrayStatistics();

            // used to store RTTS
            List<Long> aggregate_RTTs = new ArrayList<>();
            List<Long> TCP_Setup = new ArrayList<>();
            int port = Integer.valueOf(args[0]);

            System.out.println();
            System.out.println("*** ALL MEASURMENTS ARE GIVEN IN MILLISECONDS ***");

            for (int i = 0; i < 10; i++) {

                System.out.println(
                        "-------------------------------------------------------------------------------\nAttempt: "
                                + (i + 1));
                new client_TCP(args[1], port, aggregate_RTTs, TCP_Setup);
                port += 1;

            }
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println("Aggregate RTT Statistics:");
            stats.print_stats(aggregate_RTTs);
            System.out.println();
            System.out.println("TCP Setup Statistics:");
            stats.print_stats(TCP_Setup);
        }
    }

}