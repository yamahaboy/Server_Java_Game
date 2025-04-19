
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        final int PORT = 5000;
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the IP of the server (for example, 127.0.0.1):");
        String host = scanner.nextLine();

        try (Socket socket = new Socket(host, PORT); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to the server.");

            while (true) {
                String serverMessage;
                StringBuilder messageBlock = new StringBuilder();

                while ((serverMessage = in.readLine()) != null && !serverMessage.trim().isEmpty()) {
                    messageBlock.append(serverMessage).append("\n");
                }

                System.out.print(messageBlock);

                String allMsg = messageBlock.toString();
                if (allMsg.contains("input")
                        || allMsg.contains("choose")
                        || allMsg.contains("Enter your choice")
                        || allMsg.contains("One more time?")
                        || allMsg.contains("Who will start?")) {

                    System.out.print("> ");
                    String userInput = scanner.nextLine();
                    out.println(userInput);
                }
            }

        } catch (IOException e) {
            System.out.println("Error connection: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
