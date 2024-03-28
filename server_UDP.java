import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class server_UDP {

    public server_UDP(int port) {
        // Use try-with-resources to ensure the socket is closed properly
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] receiveData = new byte[1024];
            byte[] sendData;

            System.out.println("Server started on port " + port);

            while (true) {
                // Receive request
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String fileName = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

                if (fileName.equals("bye")) {
                    break;
                }

                File file = new File("jokes/" + fileName); // Assuming the file exists and is small enough
                sendData = new byte[(int) file.length()]; // This example does not handle large files well

                try (FileInputStream fis = new FileInputStream(file)) {
                    fis.read(sendData); // Read the file content into sendData

                    // Send the image content back to client
                    InetAddress clientAddress = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress,
                            clientPort);
                    socket.send(sendPacket);
                    System.out.println(fileName + " sent.");
                } catch (Exception e) {
                    System.err.println("Error sending file: " + e.getMessage());

                }
            }
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }


        System.out.println("Client Disconnected.");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please provide valid arguments.");
            return;
        }
        int port = Integer.parseInt(args[0]);
        new server_UDP(port);
    }
}
