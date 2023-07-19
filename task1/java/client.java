import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java Client <serverIP> <serverPort>");
            System.exit(1);
        }

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);

        try {
            // Step 1: Connect to the server using the specified IP and port
            Socket socket = new Socket(serverIP, serverPort);
            System.out.println("Connected to server: " + serverIP + ":" + serverPort);

            // Step 2: Create input/output streams for communication
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Step 3: Read user input from the console
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            String userInput;

            // Step 4: Send user input to the server until "terminate" is entered
            while ((userInput = userInputReader.readLine()) != null) {
                // Step 5: Send user input to the server
                out.println(userInput);

                // Step 6: Receive and display acknowledgment from the server
                String ack = in.readLine();
                System.out.println("Server acknowledgment: " + ack);

                // Step 7: If the user enters "terminate", exit the loop
                if (userInput.equalsIgnoreCase("terminate")) {
                    break;
                }
            }

            // Step 8: Close resources and disconnect from the server
            System.out.println("Disconnected from server.");
            in.close();
            out.close();
            userInputReader.close();
            socket.close();
        } catch (IOException e) {
            // Handle IO exceptions
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // Handle invalid port number
            System.err.println("Invalid port number. Please provide a valid integer port.");
        }
    }
}
