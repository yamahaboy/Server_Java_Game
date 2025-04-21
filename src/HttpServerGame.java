
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

public class HttpServerGame {

    private static final List<Session> sessions = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/", new SimpleHandler("🟢 Java Game Server is running!"));
        server.createContext("/create", new CreateHandler());
        server.createContext("/join", new JoinHandler());
        server.createContext("/next", new NextHandler());
        server.createContext("/answer", new AnswerHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("✅ HTTP server started on port " + port);
    }

    static class SimpleHandler implements HttpHandler {

        private final String message;

        public SimpleHandler(String message) {
            this.message = message;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            respond(exchange, message);
        }
    }

    static class CreateHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Session session = new Session();
            sessions.add(session);
            session.messages.add("Waiting for second player...\n");
            respond(exchange, "Game created\n");
        }
    }

    static class JoinHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            for (Session session : sessions) {
                if (session.players.size() == 1) {
                    session.players.add(new PlayerConnection());
                    session.messages.add("Both players connected!\nWho will start? (1 or 2):\n");
                    respond(exchange, "Joined game\n");
                    return;
                }
            }
            respond(exchange, "No game available. Please create one first.\n");
        }
    }

    static class NextHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            for (Session session : sessions) {
                if (!session.messages.isEmpty()) {
                    String next = session.messages.remove();
                    respond(exchange, next);
                    return;
                }
            }
            respond(exchange, "\n");
        }
    }

    static class AnswerHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String input = new String(exchange.getRequestBody().readAllBytes()).trim();
            for (Session session : sessions) {
                session.messages.add("You wrote: " + input + "\n");
                respond(exchange, "OK\n");
                return;
            }
            respond(exchange, "No session\n");
        }
    }

    static class Session {

        List<PlayerConnection> players = new ArrayList<>(List.of(new PlayerConnection()));
        Queue<String> messages = new LinkedList<>();
    }

    static class PlayerConnection {
        // For future: id or state if needed
    }

    static void respond(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
