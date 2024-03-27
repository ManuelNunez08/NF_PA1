import java.io.BufferedReader;
import java.io.FileReader;
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
            
            boolean running = true;
            while (running) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String command = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                
                String response;
                switch (command) {
                    case "Joke 1":
                        response = getJoke("Joke1.txt");
                        break;
                    case "Joke 2":
                        response = getJoke("Joke2.txt");
                        break;
                    case "Joke 3":
                        response = getJoke("Joke3.txt");
                        break;
                    case "bye":
                        response = "Server disconnecting. Goodbye!";
                        running = false; // This will stop the server after sending the response
                        break;
                    default:
                        response = "Invalid command. Please try 'Joke 1', 'Joke 2', 'Joke 3', or 'bye'.";
                        break;
                }
                
                sendData = response.getBytes();
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                socket.send(sendPacket);
            }
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getJoke(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.readLine();
        } catch (Exception e) {
            return "Error reading joke from file: " + e.getMessage();
        }
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
