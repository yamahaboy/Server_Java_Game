import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        final int PORT = 5000;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("The server is running on port " + PORT);
            System.out.println("Waiting for two players to connect..");

            Socket socket1 = serverSocket.accept();
            System.out.println("Player 1 connected: " + socket1.getInetAddress());

            Socket socket2 = serverSocket.accept();
            System.out.println("Player 2 connected: " + socket2.getInetAddress());

            Game game = new Game(socket1, socket2);
            game.start();

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
