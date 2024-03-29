import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class server_UDP {

    public server_UDP(int port, List<Long> MAT_aggregate) {

        // used to store RTTS
        List<Long> MATs = new ArrayList<>();

        // Use try-with-resources to ensure the socket is closed properly
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] receiveData = new byte[1024];
            byte[] sendData;

            while (true) {
                // Receive request
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String fileName = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

                if (fileName.equals("bye")) {
                    break;
                }

                long start = System.nanoTime();
                File file = new File("jokes/" + fileName);
                // end Meme Access Timer
                long MAT = System.nanoTime() - start;
                MATs.add(MAT);

                sendData = new byte[(int) file.length()];

                try (FileInputStream fis = new FileInputStream(file)) {
                    fis.read(sendData); // Read the file content into sendData

                    // Send the image content back to client
                    InetAddress clientAddress = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress,
                            clientPort);
                    socket.send(sendPacket);

                } catch (Exception e) {
                    System.err.println("Error sending file: " + e.getMessage());

                }
            }
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }

        // update MAT_Agrregate list
        Long MAT_Sum = MATs.stream().mapToLong(Long::longValue).sum();
        MAT_aggregate.add(MAT_Sum);
        // print Out iteration Statistics
        double MAT_Roundded = Math.round((MAT_Sum/1000000.0) * 1000) / 1000.0;
        System.out.println("Sum of Meme Access Time (MAT): " + MAT_Roundded);
        System.out.println();
        ArrayStatistics stats = new ArrayStatistics();
        System.out.println("Meme Access Time (MAT) Stats:");
        stats.print_stats(MATs);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please provide valid arguments.");
            return;
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
                new server_UDP(port, MAT_aggregate);
                port = port + 1;
            }

            System.out.println("-------------------------------------------------------------------------------");

            System.out.println();
            System.out.println("Aggregate Meme Access Time (MAT) Stats:");
            stats.print_stats(MAT_aggregate);
        }
    }
}
