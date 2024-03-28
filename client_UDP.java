import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class client_UDP {

    public client_UDP(String serverAddress, int serverPort) {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(serverAddress);

            Set<Integer> receivedImages = new HashSet<>();
            Random rand = new Random();

            // Initial message from server not applicable in UDP; removed.
            byte[] sendData;
            byte[] receiveData = new byte[1024];

            while (receivedImages.size() < 10) {
                int imageNumber = 1 + rand.nextInt(10);

                String request = "joke" + imageNumber + ".png";
                sendData = request.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, serverPort);
                socket.send(sendPacket);

                receiveData = new byte[65507]; // Max UDP packet size
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                // Assuming the server sends the image data directly
                try (FileOutputStream fos = new FileOutputStream("received_jokes/" + request)) {
                    fos.write(receivePacket.getData(), 0, receivePacket.getLength());
                    receivedImages.add(imageNumber);
                    System.out.println("Received " + request);
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

        System.out.println("exit.");
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Please provide valid arguments.");
            return;
        }
        int port = Integer.parseInt(args[0]);
        String IP = args[1];
        new client_UDP(IP, port);
    }
}
