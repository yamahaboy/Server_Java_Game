
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;

public class HttpServerGame {

    private static final Map<String, Session> sessions = new HashMap<>();
    private static int sessionCounter = 1;

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/create", new CreateHandler());
        server.createContext("/join", new JoinHandler());
        server.createContext("/next", new NextHandler());
        server.createContext("/answer", new AnswerHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println("✅ HTTP server started on port " + port);
    }

    static class Session {

        String sessionId;
        boolean full = false;
        Map<String, Queue<String>> playerMessages = new HashMap<>();
        Map<String, Queue<String>> playerAnswers = new HashMap<>();
        List<String> players = new ArrayList<>();

        Session(String id) {
            this.sessionId = id;
        }

        void addPlayer(String playerId) {
            players.add(playerId);
            playerMessages.put(playerId, new LinkedList<>());
            playerAnswers.put(playerId, new LinkedList<>());
        }

        void sendTo(String playerId, String message) {
            playerMessages.get(playerId).add(message);
        }

        void sendToAll(String message) {
            for (String playerId : players) {
                sendTo(playerId, message);
            }
        }

        void addAnswer(String playerId, String answer) {
            playerAnswers.get(playerId).add(answer);
        }

        String pollMessage(String playerId) {
            return playerMessages.get(playerId).poll();
        }
    }

    static class CreateHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String sessionId = "S" + (sessionCounter++);
            String playerId = UUID.randomUUID().toString();

            Session session = new Session(sessionId);
            session.addPlayer(playerId);
            session.sendTo(playerId, "Game created\nWaiting for second player...\n\n");

            sessions.put(sessionId, session);

            respond(exchange, sessionId + "," + playerId);
        }
    }

    static class JoinHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            for (Session session : sessions.values()) {
                if (!session.full && session.players.size() == 1) {
                    String playerId = UUID.randomUUID().toString();
                    session.addPlayer(playerId);
                    session.full = true;
                    session.sendToAll("Both players connected!\nWho will start? (1 or 2):\n\n");
                    respond(exchange, session.sessionId + "," + playerId);
                    return;
                }
            }
            respond(exchange, "No available games");
        }
    }

    static class NextHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange);
            String sessionId = query.get("sessionId");
            String playerId = query.get("playerId");

            Session session = sessions.get(sessionId);
            if (session != null) {
                String msg = session.pollMessage(playerId);
                respond(exchange, msg == null ? "" : msg);
            } else {
                respond(exchange, "");
            }
        }
    }

    static class AnswerHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange);
            String sessionId = query.get("sessionId");
            String playerId = query.get("playerId");

            String input;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
                input = reader.readLine();
            }

            Session session = sessions.get(sessionId);
            if (session != null) {
                session.addAnswer(playerId, input);
                session.sendToAll("You wrote: " + input + "\n\n");
            }

            respond(exchange, "OK");
        }
    }

    private static void respond(HttpExchange exchange, String response) throws IOException {
        byte[] bytes = response.getBytes();
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static Map<String, String> parseQuery(HttpExchange exchange) {
        String[] parts = exchange.getRequestURI().getQuery().split("&");
        Map<String, String> map = new HashMap<>();
        for (String part : parts) {
            String[] kv = part.split("=");
            map.put(kv[0], kv[1]);
        }
        return map;
    }
}
