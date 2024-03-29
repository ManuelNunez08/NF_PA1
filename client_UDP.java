
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class client_UDP {

    public client_UDP(String serverAddress, int serverPort, List<Long> RTT_Aggregates, List<Long> DNS_lookup) {

        // used to store RTTS
        List<Long> RTTs = new ArrayList<>();
        long DNS_TimeResult = 0;

        try {

            // declare socket and find server
            DatagramSocket socket = new DatagramSocket();

            long Track_DNS_Lookup = System.nanoTime();
            InetAddress address = InetAddress.getByName(serverAddress);
            DNS_TimeResult = (System.nanoTime() - Track_DNS_Lookup);

            Set<Integer> receivedImages = new HashSet<>();
            Random rand = new Random();

            // Initial message from server not applicable in UDP; removed.
            byte[] sendData;
            byte[] receiveData = new byte[1024];

            while (receivedImages.size() < 10) {
                int imageNumber = 1 + rand.nextInt(10);

                // Start individual timer for one joke image request
                long Inv_Start = System.nanoTime();

                String request = "joke" + imageNumber + ".png";
                sendData = request.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, serverPort);
                socket.send(sendPacket);
                receiveData = new byte[65507]; // Max UDP packet size
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                // calculate time taken to request and receive image
                long Inv_StartResult = (System.nanoTime() - Inv_Start);

                // add to sum
                RTTs.add(Inv_StartResult);

                // Assuming the server sends the image data directly
                try (FileOutputStream fos = new FileOutputStream("received_jokes/" + request)) {
                    fos.write(receivePacket.getData(), 0, receivePacket.getLength());
                    receivedImages.add(imageNumber);
                } catch (Exception e) {
                    System.err.println("Failed to save " + request + ": " + e.getMessage());
                }

                if (receivedImages.size() == 10) {
                    String bye = "bye";
                    sendData = bye.getBytes();
                    DatagramPacket bye_Packet = new DatagramPacket(sendData, sendData.length, address, serverPort);
                    socket.send(bye_Packet);
                }
            }

            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        // update RTTs list
        Long RTT_Sum = RTTs.stream().mapToLong(Long::longValue).sum();
        RTT_Aggregates.add(RTT_Sum);
        DNS_lookup.add(DNS_TimeResult);

        // Print Out iteration Statistics
        double RTT_Roundded = Math.round((RTT_Sum / 1000000.0) * 1000) / 1000.0;
        double DNS_Roundded = Math.round((DNS_TimeResult / 1000000.0) * 1000) / 1000.0;
        System.out.println("Sum of Round Trip Times: " + RTT_Roundded);
        System.out.println("DNS LookupTime: " + DNS_Roundded);
        System.out.println();

        ArrayStatistics stats = new ArrayStatistics();
        System.out.println("RTT Statistics:");
        stats.print_stats(RTTs);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Please provide valid initilization arguments.");
        } else {

            ArrayStatistics stats = new ArrayStatistics();

            // used to store RTTS
            List<Long> aggregate_RTTs = new ArrayList<>();
            List<Long> DNS_lookup = new ArrayList<>();
            int port = Integer.valueOf(args[0]);

            System.out.println();
            System.out.println("*** ALL MEASURMENTS ARE GIVEN IN MILLISECONDS ***");

            for (int i = 0; i < 10; i++) {

                System.out.println(
                        "-------------------------------------------------------------------------------\nAttempt: "
                                + (i + 1));
                new client_UDP(args[1], port, aggregate_RTTs, DNS_lookup);
                port += 1;

            }
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println("Aggregate RTT Statistics:");
            stats.print_stats(aggregate_RTTs);
            System.out.println();
            System.out.println("DNS Lookup Statistics:");
            stats.print_stats(DNS_lookup);

        }
    }
}
