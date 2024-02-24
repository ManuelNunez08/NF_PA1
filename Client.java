import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    // initialize socket and input output streams
    private Socket socket = null;
    private DataInputStream input = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    // constructor to put ip address and port
    public Client(String address, int port) {
        // establish a connection
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            input = new DataInputStream(System.in);

            // sends output to the socket
            out = new DataOutputStream(
                    socket.getOutputStream());

            // takes input from the server socket
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
        } catch (UnknownHostException u) {

            System.out.println(u);
            return;
        } catch (IOException i) {
            System.out.println(i);
            return;
        }

        // string to read message from input
        String line = "";
        String joke = "";

        // keep reading until "Over" is input
        while (!line.equals("STOP")) {
            try {
                line = input.readLine();
                out.writeUTF(line);

                joke = in.readUTF();
                System.out.println(joke);

            } catch (IOException i) {
                System.out.println(i);
            }
        }

        // close the connection
        try {
            input.close();
            out.close();
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String args[]) {
        // int port = Integer.valueOf(args[0]);
        int port = 4010;
        Client client = new Client("127.0.0.1", port);
    }
}
