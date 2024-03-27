import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class client_UDP {

    public client_UDP(String serverAddress, int serverPort) {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(serverAddress);
            
            // Initial message from server not applicable in UDP; removed.
            
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String line;
            byte[] sendData;
            byte[] receiveData = new byte[1024];

            while (true) {
                line = userInput.readLine();
                sendData = line.getBytes();
                
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, serverPort);
                socket.send(sendPacket);
                
                if ("bye".equals(line)) {
                    break;
                }
                
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String serverResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(serverResponse);
            }
            
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Please provide valid arguments.");
            return;
        }
        int port = Integer.parseInt(args[1]);
        String IP = args[0];
        new client_UDP(IP, port);
    }
}

