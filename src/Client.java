
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Client {

    private static final String BASE_URL = "https://java-tcp-game-production.up.railway.app";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose:");
        System.out.println("1 - Create game");
        System.out.println("2 - Join game");
        System.out.print("> ");
        int action = Integer.parseInt(scanner.nextLine());

        if (action == 1) {
            System.out.println(post("/create", ""));
        } else {
            System.out.println(post("/join", ""));
        }

        while (true) {
            String response = get("/next");
            System.out.print(response);

            if (response.contains("input") || response.contains("choose")
                    || response.contains("Enter your choice")
                    || response.contains("One more time?")
                    || response.contains("Who will start?")) {

                System.out.print("> ");
                String userInput = scanner.nextLine();
                post("/answer", userInput);
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
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
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
            response.append(line).append("\n");
        }
        return response.toString();
    }
}
