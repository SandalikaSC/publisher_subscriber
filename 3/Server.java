import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Server {
    private static final Map<Socket, String[]> clients = new HashMap<>();
    private static final Map<String, Set<Socket>> topics = new HashMap<>();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Server <port>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        startServer(port);
    }

    public static void startServer(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started at port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established wellcome a new client");

                PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream(), true);
                outToClient.println("Welcome to the Server!!!!");

                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String mode = inFromClient.readLine();
                String topic = inFromClient.readLine();

                clients.put(clientSocket, new String[]{mode, topic});

                if (mode.equals("SUBSCRIBER")) {
                    topics.putIfAbsent(topic, new HashSet<>());
                    topics.get(topic).add(clientSocket);
                }

                Thread thread = new Thread(() -> createThread(clientSocket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    public static void createThread(Socket clientSocket) {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String[] clientData = clients.get(clientSocket);
            String mode = clientData[0];
            String topic = clientData[1];

            String senderInfo = "[" + mode + "]";
            
            while (true) {
                String msg = inFromClient.readLine();
                if (msg == null || msg.equals("terminate")) {
                    break;
                }

                if (mode.equals("PUBLISHER")) {
                    forwardMessage(topic, senderInfo + ": " + msg);
                }

                System.out.println(senderInfo + " - " + msg);
            }
            System.out.println("Connection closed by client");
            clients.remove(clientSocket);
            if (mode.equals("SUBSCRIBER")) {
                topics.get(topic).remove(clientSocket);
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void forwardMessage(String topic, String msg) {
        Set<Socket> subscribers = topics.getOrDefault(topic, new HashSet<>());
        for (Socket subscriber : subscribers) {
            try {
                PrintWriter outToClient = new PrintWriter(subscriber.getOutputStream(), true);
                outToClient.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
