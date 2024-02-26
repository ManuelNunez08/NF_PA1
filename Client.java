import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class client {
    // initialize socket and input output streams
    private Socket socket = null;
    private DataInputStream input = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    // constructor to put ip address and port
    public client(String address, int port) {
        // establish a connection
        try {
            socket = new Socket(address, port);
            System.out.println("Hello!");

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

        // keep reading until "bye" is input
        while (!line.equals("bye")) {
            try {
                line = input.readLine();
                out.writeUTF(line);

                joke = in.readUTF();
                System.out.println(joke);

            } catch (IOException i) {
                //System.out.println(i);
            }
        }
        System.out.println("exit");

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
        client client = new client("127.0.0.1", port);
    }
}
