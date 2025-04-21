import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpServerGame {

    private static final Map<String, Integer> secretNumbers = new HashMap<>();
    private static final Map<String, Integer> guesses = new HashMap<>();

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        server.createContext("/", new BasicHandler());
        server.createContext("/start", new StartHandler());
        server.createContext("/input-number", new InputNumberHandler());
        server.createContext("/join", new JoinHandler());
        server.createContext("/guess", new GuessHandler());
        server.createContext("/result", new ResultHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("✅ HTTP server started on port " + port);
    }

    static class BasicHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "🟢 Java Game Server is running!";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class StartHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String gameId = UUID.randomUUID().toString();
            secretNumbers.put(gameId, null);
            String response = "Game created with ID: " + gameId;
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class InputNumberHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String gameId = getQueryParam(exchange, "gameId");
            String body = new String(exchange.getRequestBody().readAllBytes());
            try {
                int number = Integer.parseInt(body.trim());
                secretNumbers.put(gameId, number);
                send(exchange, "Secret number set for game " + gameId);
            } catch (NumberFormatException e) {
                send(exchange, "Invalid number.");
            }
        }
    }

    static class JoinHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String gameId = getQueryParam(exchange, "gameId");
            if (!secretNumbers.containsKey(gameId)) {
                send(exchange, "Game ID not found");
                return;
            }
            send(exchange, "Joined game " + gameId);
        }
    }

    static class GuessHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String gameId = getQueryParam(exchange, "gameId");
            String body = new String(exchange.getRequestBody().readAllBytes());
            try {
                int guess = Integer.parseInt(body.trim());
                guesses.put(gameId, guess);
                send(exchange, "Guess received for game " + gameId);
            } catch (NumberFormatException e) {
                send(exchange, "Invalid guess.");
            }
        }
    }

    static class ResultHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String gameId = getQueryParam(exchange, "gameId");
            if (!secretNumbers.containsKey(gameId)) {
                send(exchange, "Game ID not found");
                return;
            }

            Integer secret = secretNumbers.get(gameId);
            Integer guess = guesses.get(gameId);

            if (secret == null) {
                send(exchange, "Waiting for player 1 to input secret number");
            } else if (guess == null) {
                send(exchange, "Waiting for player 2 to guess");
            } else {
                if (secret.equals(guess)) {
                    send(exchange, "SUCCESS! Player 2 guessed correctly.");
                } else {
                    send(exchange, "FAIL! Secret number was " + secret);
                }
            }
        }
    }

    private static String getQueryParam(HttpExchange exchange, String key) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2 && pair[0].equals(key)) {
                return pair[1];
            }
        }
        return null;
    }

    private static void send(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
