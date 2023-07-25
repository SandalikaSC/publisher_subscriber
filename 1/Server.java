import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java MyServerApp <PORT>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Listening on port " + port);
             System.out.println("Welcome to the Server!!!");

            // Wait for a client to connect
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            try (
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {
                String inputLine;
                while ((inputLine = clientReader.readLine()) != null) {
                    System.out.println("Client " + inputLine);

                    if ("terminate".equalsIgnoreCase(inputLine.trim())) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Client disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
