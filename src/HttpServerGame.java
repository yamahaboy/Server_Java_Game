
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;

public class HttpServerGame {

    private static final List<Session> sessions = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv("PORT"));

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

        Queue<String> messages = new LinkedList<>();
        final Map<Integer, Queue<String>> playerAnswers = new HashMap<>();
        boolean full = false;

        void sendToAll(String msg) {
            messages.add(msg);
        }

        void addAnswer(int playerIndex, String msg) {
            playerAnswers.computeIfAbsent(playerIndex, k -> new LinkedList<>()).add(msg);
        }

        String awaitAnswer(int playerIndex) {
            while (playerAnswers.getOrDefault(playerIndex, new LinkedList<>()).isEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
            return playerAnswers.get(playerIndex).poll();
        }
    }

    static class CreateHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Session session = new Session();
            sessions.add(session);
            session.messages.add("Game created\nWaiting for second player...\n");
            respond(exchange, "Game created");
        }
    }

    static class JoinHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            for (Session session : sessions) {
                if (!session.full) {
                    session.full = true;
                    session.sendToAll("Both players connected!\n");
                    session.sendToAll("Who will start? (1 or 2):\n");
                    respond(exchange, "Joined game");
                    return;
                }
            }
            respond(exchange, "No available games");
        }
    }

    static class NextHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            for (Session session : sessions) {
                if (!session.messages.isEmpty()) {
                    respond(exchange, session.messages.poll());
                    return;
                }
            }
            respond(exchange, "");
        }
    }

    static class AnswerHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Scanner scanner = new Scanner(exchange.getRequestBody()).useDelimiter("\\A");
            String input = scanner.hasNext() ? scanner.next() : "";

            for (Session session : sessions) {
                if (session.full) {
                    // Временное предположение — всегда отвечает игрок 1
                    session.addAnswer(1, input);
                    session.sendToAll("You wrote: " + input + "\n");
                    break;
                }
            }
            respond(exchange, "OK");
        }
    }

    private static void respond(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
