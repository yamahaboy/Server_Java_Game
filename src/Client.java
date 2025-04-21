import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Client {
    private static final String BASE_URL = "https://java-tcp-game-production.up.railway.app";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose action:");
        System.out.println("1 - Create game (Player 1)");
        System.out.println("2 - Join game (Player 2)");
        System.out.print("> ");
        int action = Integer.parseInt(scanner.nextLine());

        String gameId = null;

        if (action == 1) {
            gameId = post("/start", "").replace("Game created with ID: ", "");
            System.out.println("Game ID: " + gameId);
            System.out.print("Enter secret number: ");
            String secret = scanner.nextLine();
            post("/input-number?gameId=" + gameId, secret);
            System.out.println("Waiting for player 2...");
        } else if (action == 2) {
            System.out.print("Enter game ID to join: ");
            gameId = scanner.nextLine();
            post("/join?gameId=" + gameId, "");
            System.out.print("Enter your guess: ");
            String guess = scanner.nextLine();
            post("/guess?gameId=" + gameId, guess);
        }

        System.out.println("Checking result...");
        String result = get("/result?gameId=" + gameId);
        System.out.println("\nRESULT: " + result);
    }

    private static String post(String path, String body) throws IOException {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes());
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }

    private static String get(String path) throws IOException {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }
}
