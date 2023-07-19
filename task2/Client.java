import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java Client <serverIP> <serverPort> <mode>");
            System.exit(1);
        }

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String mode = args[2].toUpperCase();

        try {
            // Step 1: Connect to the server using the specified IP and port
            Socket socket = new Socket(serverIP, serverPort);
            System.out.println("Connected to server: " + serverIP + ":" + serverPort);

            // Step 2: Create input/output streams for communication
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Step 3: Send client mode ("Publisher" or "Subscriber") to the server
            out.println(mode);

            // Step 4: Read user input from the console
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            String userInput;

            // Step 5: Send user input to the server until "terminate" is entered
            while ((userInput = userInputReader.readLine()) != null) {
                out.println(userInput);

                // Step 6: If the user enters "terminate", exit the loop
                if (userInput.equalsIgnoreCase("terminate")) {
                    break;
                }
            }

            // Step 7: Close resources and disconnect from the server
            System.out.println("Disconnected from server.");
            out.close();
            in.close();
            userInputReader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number. Please provide a valid integer port.");
        }
    }
}
