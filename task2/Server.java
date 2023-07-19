import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Server <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started. Listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number. Please provide a valid integer port.");
        }
    }

    public static void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    static class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String clientIdentifier;
        private boolean isPublisher;

        public ClientHandler(Socket socket) {
            clientSocket = socket;
            clientIdentifier = clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();

            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // Step 1: Read client role ("PUBLISHER" or "SUBSCRIBER")
                String clientRole = in.readLine();
                isPublisher = clientRole.equalsIgnoreCase("PUBLISHER");

                // Step 2: Read data from the client and handle messages accordingly
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client " + clientIdentifier + ": " + inputLine);

                    if (isPublisher) {
                        // Step 3: If the client is a Publisher, broadcast the message to Subscribers
                        broadcastMessage("Publisher message from " + clientIdentifier + ": " + inputLine, this);
                    }

                    // Step 4: If the client sends "terminate", exit the loop
                    if (inputLine.equalsIgnoreCase("terminate")) {
                        break;
                    }
                }

                // Step 5: Close resources and disconnect from the client
                System.out.println("Client disconnected: " + clientIdentifier);
                removeClient(this);
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }

    public static void broadcastMessage(String message, ClientHandler publisher) {
        for (ClientHandler client : clients) {
            if (client != publisher && !client.isPublisher) {
                client.sendMessage(message);
            }
        }
    }
}
