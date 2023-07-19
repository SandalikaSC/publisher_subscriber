import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Server <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        try {
            // Step 1: Create a ServerSocket that listens on the specified port
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started. Listening on port " + port);

            // Step 2: Wait for a client to connect and accept the connection
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            // Step 3: Create input/output streams for communication
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Step 4: Read data from the client and send acknowledgment
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client: " + inputLine);

                // Step 5: Send acknowledgment back to the client
                out.println("Message received: " + inputLine);

                // Step 6: If the client sends "terminate", exit the loop
                if (inputLine.equalsIgnoreCase("terminate")) {
                    break;
                }
            }

            // Step 7: Close resources and disconnect from the client
            System.out.println("Client disconnected.");
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            // Handle IO exceptions
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // Handle invalid port number
            System.err.println("Invalid port number. Please provide a valid integer port.");
        }
    }
}
