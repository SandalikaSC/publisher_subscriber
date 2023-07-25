import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static Map<Socket, String> clients = new HashMap<>();

    private static void createThread(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
    
            while (true) {
                System.out.print("client > ");
                String msg = in.readLine();
    
                if (msg == null) {
                    break; // Client disconnected
                }
    
                if (clients.get(clientSocket).equals("PUBLISHER")) {
                    forwardMessage(clientSocket, msg);
                }
    
                if (msg.equals("terminate")) {
                    break;
                }
                System.out.println(msg);
            }
    
            // Client disconnected, remove from clients map
            String mode = clients.remove(clientSocket);
            System.out.println("Client " + clientSocket.getInetAddress() + " (" + mode + ") disconnected.");
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private static void forwardMessage(Socket publisherSocket, String msg) {
        for (Socket subscriberSocket : clients.keySet()) {
            if (!subscriberSocket.equals(publisherSocket) && clients.get(subscriberSocket).equals("SUBSCRIBER")) {
                try {
                    PrintWriter out = new PrintWriter(subscriberSocket.getOutputStream(), true);
                    out.println(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void startServer(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established from address " + clientSocket.getInetAddress());

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("Welcome to the Server!!!!");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String mode = in.readLine();
                clients.put(clientSocket, mode);

                Thread thread = new Thread(() -> createThread(clientSocket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Server <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        startServer(port);
    }
}
