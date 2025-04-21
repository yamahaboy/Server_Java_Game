
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    private static final String BASE_URL = "https://java-tcp-game-production.up.railway.app";

    public static void main(String[] args) throws IOException {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose:");
        System.out.println("1 - Create game");
        System.out.println("2 - Join game");
        System.out.print("> ");
        int action = Integer.parseInt(scanner.nextLine());

        String[] data;
        if (action == 1) {
            data = post("/create", "").split(",");
        } else {
            data = post("/join", "").split(",");
        }

        if (data.length < 2) {
            System.out.println("Failed to start/join game");
            return;
        }

        String sessionId = data[0].trim();
        String playerId = data[1].trim();

        while (true) {
            String response;
            do {
                response = get("/next?sessionId=" + encode(sessionId) + "&playerId=" + encode(playerId));
                if (!response.trim().isEmpty()) {
                    System.out.print(response);
                }
                safeSleep(100);
            } while (!response.trim().isEmpty());

            if (response.contains("input") || response.contains("choose")
                    || response.contains("Enter your choice") || response.contains("One more time?")
                    || response.contains("Who will start?")) {
                System.out.print("> ");
                String userInput = scanner.nextLine();
                post("/answer?sessionId=" + encode(sessionId) + "&playerId=" + encode(playerId), userInput);
            }
        }
    }

    private static String post(String path, String body) throws IOException {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes());
        }
        return readResponse(conn);
    }

    private static String get(String path) throws IOException {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return readResponse(conn);
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
        }
        return response.toString();
    }

    private static void safeSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
