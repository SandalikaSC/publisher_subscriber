import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java Client <server IP> <port> <mode> <topic>");
            System.exit(1);
        }

        String serverIP = args[0];
        int port = Integer.parseInt(args[1]);
        String mode = args[2];
        String topic = args[3];

        startClient(serverIP, port, mode, topic);
    }

    public static void startClient(String serverIP, int port, String mode, String topic) {
        try {
            Socket clientSocket = new Socket(serverIP, port);
            System.out.println("Server IP: " + serverIP);

            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println(inFromServer.readLine());

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.writeBytes(mode + "\n");
            outToServer.writeBytes(topic + "\n");

            if (mode.equals("SUBSCRIBER")) {
                while (true) {
                    String msg = inFromServer.readLine();
                    if (msg.equals("terminate")) {
                        break;
                    }
                    System.out.println(msg);
                }
            } else {
                while (true) {
                    System.out.print("Enter message: ");
                    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                    String msg = inFromUser.readLine();
                    outToServer.writeBytes(msg + "\n");

                    if (msg.equals("terminate")) {
                        clientSocket.close();
                        break;
                    }
                }
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
