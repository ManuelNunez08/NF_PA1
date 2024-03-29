import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class server_TCP {
    // Initialize server socket and client connection
    private Socket clientSocket = null;
    private ServerSocket jokeServer = null;
    private DataInputStream clientInput = null;
    private DataOutputStream clientOutput = null;

    // Constructor with server port
    public server_TCP(int port, List<Long> MAT_Aggregate) {
        // used to store RTTS
        List<Long> MATs = new ArrayList<>();

        // Start server and wait for a connection
        try {
            jokeServer = new ServerSocket(port);
            clientSocket = jokeServer.accept();

            // Input from client
            clientInput = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            // Output to client
            clientOutput = new DataOutputStream(clientSocket.getOutputStream());

            // anounce connection
            String fileName = "";

            // Process commands from client
            while (true) {
                try {

                    // Handle client requests
                    // read in client command and break if necessary
                    fileName = clientInput.readUTF();
                    if (fileName.equals("bye")) {
                        break;
                    }

                    // Start Meme access Timer
                    long start = System.nanoTime();
                    File file = new File("jokes/" + fileName);
                    // end Meme Access Timer
                    long MAT = System.nanoTime() - start;
                    MATs.add(MAT);

                    clientOutput.writeLong(file.length());

                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            clientOutput.write(buffer, 0, bytesRead);
                        }
                    }

                } catch (IOException e) {
                    System.out.println(e);
                }
            }

            // update MAT_Agrregate list
            Long MAT_Sum = MATs.stream().mapToLong(Long::longValue).sum();
            MAT_Aggregate.add(MAT_Sum);

            // print Out iteration Statistics
            double MAT_Rounded = Math.round((MAT_Sum / 1000000.0) * 1000) / 1000.0;
            System.out.println("Sum of Meme Access Time (MAT): " + MAT_Rounded);
            System.out.println();
            ArrayStatistics stats = new ArrayStatistics();
            System.out.println("Meme Access Time (MAT) Stats");
            stats.print_stats(MATs);

            clientSocket.close();
            clientInput.close();
            clientOutput.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please provide valid initilization arguments.");
        } else {

            System.out.println();
            System.out.println("*** ALL MEASURMENTS ARE GIVEN IN MILLISECONDS ***");
            ArrayStatistics stats = new ArrayStatistics();
            List<Long> MAT_aggregate = new ArrayList<>();
            int port = Integer.valueOf(args[0]);
            for (int i = 0; i < 10; i++) {
                System.out.println(
                        "-------------------------------------------------------------------------------\nAttempt: "
                                + (i + 1));
                new server_TCP(port, MAT_aggregate);
                port = port + 1;
            }

            System.out.println("-------------------------------------------------------------------------------");

            System.out.println();
            System.out.println("Aggregate Meme Access Time (MAT) Stats:");
            stats.print_stats(MAT_aggregate);
        }
    }
}