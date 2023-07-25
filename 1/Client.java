import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java MyClientApp <Server_IP> <PORT>");
            System.exit(1);
        }

        String serverIP = args[0];
        int port = Integer.parseInt(args[1]);

        try (
                Socket socket = new Socket(serverIP, port);
                PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));) {
            System.out.println("Connected to server at " + serverIP + ":" + port);
            System.out.println("Welcome to the Server!!!");
            System.out.println("Type 'terminate' to disconnect from the server.");

            String userInput;
            System.out.print("Enter your Message : ");
            while ((userInput = userInputReader.readLine()) != null) {
                System.out.print("Enter your Message : ");
                serverWriter.println(serverIP + " : " + userInput);

                if ("terminate".equalsIgnoreCase(userInput.trim())) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Client " + serverIP + " Disconnected from the server.");
    }
}
