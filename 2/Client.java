import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java Client <server_IP> <port> <mode>");
            System.exit(1);
        }

        String serverIP = args[0];
        int port = Integer.parseInt(args[1]);
        String mode = args[2];

        try {
            Socket socket = new Socket(serverIP, port);
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message = userInputReader.readLine();
            System.out.println(message);

            serverWriter.println(mode);

            try {
                if (mode.equals("SUBSCRIBER")) {
                    while (true) {
                        System.out.print("publisher > ");
                        String msg = userInputReader.readLine();
                        if (msg.equals("terminate")) {
                            break;
                        }
                        System.out.println(msg);
                    }
                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    while (true) {
                        System.out.print("Enter a message: ");
                        String msg = reader.readLine();
                        serverWriter.println(msg);

                        if (msg.equals("terminate")) {
                            // socket.close();
                            break;
                        }
                    }
                }
            } finally {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
