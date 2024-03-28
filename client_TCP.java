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

class ArrayStatistics {
    // Method to find the minimum value in the array
    public long findMin(long[] array) {
        long min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    // Method to find the maximum value in the array
    public long findMax(long[] array) {
        long max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    // Method to find the mean (average) of the array
    public double findMean(long[] array) {
        double sum = 0;
        for (long num : array) {
            sum += num;
        }
        return sum / array.length;
    }

    // Method to find the standard deviation of the array
    public double findStdDev(long[] array, double mean) {
        double sumOfSquaredDifferences = 0;
        for (long num : array) {
            sumOfSquaredDifferences += Math.pow(num - mean, 2);
        }
        return Math.sqrt(sumOfSquaredDifferences / array.length);
    }

};

public class client_TCP {
    long sum = 0;
    // Initialize socket and input/output streams
    private Socket connectionSocket = null;
    private DataInputStream consoleInput = null;
    private DataInputStream serverInput = null;
    private DataOutputStream serverOutput = null;

    // Constructor with server address and port
    public client_TCP(String serverAddress, int serverPort, long arr[], int count) {

        // Create a set to track received image numbers
        Set<Integer> receivedImages = new HashSet<>();
        Random rand = new Random();

        // Attempt to establish a connection
        long Track_TCP_Connection = System.currentTimeMillis();
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
        long TCPSetupTimeResult = (System.currentTimeMillis() - Track_TCP_Connection);
        System.out.println("TCP Setup Time: " + TCPSetupTimeResult);

        // Continue communication until "disconnected" is received
        while (receivedImages.size() < 10) {
            try {
                int imageNumber = rand.nextInt(10) + 1;
                if (!receivedImages.contains(imageNumber)) {
                    receivedImages.add(imageNumber);
                }

                // Start individual timer for one joke image
                long Inv_Start = System.currentTimeMillis();

                // Send image request
                serverOutput.writeUTF("joke" + imageNumber + ".png");

                long fileSize = serverInput.readLong();
                long Inv_StartResult = (System.currentTimeMillis() - Inv_Start);
                sum += Inv_StartResult;
                System.out.println("Received joke" + imageNumber + ".png : " + Inv_StartResult + "ms");
                arr[count] = sum;
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

            ArrayStatistics stats = new ArrayStatistics();

            long[] arr = new long[10];
            int port = Integer.valueOf(args[0]);

            for (int i = 0; i < 10; i++) {

                System.out.println(
                        "-------------------------------------------------------------------------------\nAttempt: "
                                + (i + 1));
                new client_TCP(args[1], port, arr, i);
                System.out.println("Sum of Round Trip Times (ms): " + arr[i]);
                port = port + 1;

            }

            long min = stats.findMin(arr);
            long max = stats.findMax(arr);
            double mean = stats.findMean(arr);
            double stddev = stats.findStdDev(arr, mean);

            System.out.println("The stats: ");
            System.out.println("Max Aggeragte of RTTs for one iteration: " + min);
            System.out.println("Max Aggeragte of RTTs for one iteration: " + max);
            System.out.println("Mean sum of RTTs for one iteration: " + mean);
            System.out.println("StdDev of aggregatte RTTs for one iteration: " + stddev);

            
        }
    }

}