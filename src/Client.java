
// import java.io.*;
// import java.net.Socket;
// import java.util.Scanner;
// public class Client {
//     public static void main(String[] args) {
//         final int PORT = 5000;
//         Scanner scanner = new Scanner(System.in);
//         System.out.print("Enter the IP of the server (for example, 127.0.0.1):");
//         String host = scanner.nextLine();
//         try (Socket socket = new Socket(host, PORT); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
//             System.out.println("Connected to the server.");
//             while (true) {
//                 String serverMessage;
//                 StringBuilder messageBlock = new StringBuilder();
//                 while ((serverMessage = in.readLine()) != null && !serverMessage.trim().isEmpty()) {
//                     messageBlock.append(serverMessage).append("\n");
//                 }
//                 System.out.print(messageBlock);
//                 String allMsg = messageBlock.toString();
//                 if (allMsg.contains("input")
//                         || allMsg.contains("choose")
//                         || allMsg.contains("Enter your choice")
//                         || allMsg.contains("One more time?")
//                         || allMsg.contains("Who will start?")) {
//                     System.out.print("> ");
//                     String userInput = scanner.nextLine();
//                     out.println(userInput);
//                 }
//             }
//         } catch (IOException e) {
//             System.out.println("Error connection: " + e.getMessage());
//         } finally {
//             scanner.close();
//         }
//     }
// }
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // URL сервера на Railway (можно поменять на свой)
        String serverUrl = "https://java-tcp-game-production.up.railway.app";

        while (true) {
            System.out.println("\nChoose action:");
            System.out.println("1 - Check server status (/)");
            System.out.println("2 - Start game (/start)");
            System.out.println("0 - Exit");
            System.out.print("> ");

            String input = scanner.nextLine();
            if (input.equals("0")) {
                break;
            }

            try {
                switch (input) {
                    case "1" ->
                        sendGet(serverUrl + "/");
                    case "2" ->
                        sendPost(serverUrl + "/start");
                    default ->
                        System.out.println("Unknown command.");
                }
            } catch (IOException e) {
                System.out.println("Connection error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void sendGet(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int code = con.getResponseCode();
        String response = readResponse(con);

        System.out.println("GET " + urlStr);
        System.out.println("Status: " + code);
        System.out.println("Response: " + response);
    }

    private static void sendPost(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        int code = con.getResponseCode();
        String response = readResponse(con);

        System.out.println("POST " + urlStr);
        System.out.println("Status: " + code);
        System.out.println("Response: " + response);
    }

    private static String readResponse(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
        }
        in.close();
        return response.toString().trim();
    }
}
