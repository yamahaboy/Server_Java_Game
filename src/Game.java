
import java.io.IOException;
import java.net.Socket;

public class Game {

    private final SocketPlayer player1;
    private final SocketPlayer player2;

    public Game(Socket socket1, Socket socket2) throws IOException {
        this.player1 = new SocketPlayer(1, "Player 1", socket1);
        this.player2 = new SocketPlayer(2, "Player 2", socket2);
    }

    public void start() {
        while (true) {
            SocketPlayer startingPlayer = askStartingPlayer();
            SocketPlayer otherPlayer = (startingPlayer == player1) ? player2 : player1;

            Round round = new Round(startingPlayer, otherPlayer);
            round.play();

            if (!askToPlayAgain()) {
                break;
            }
        }

        endGame();
    }

    private SocketPlayer askStartingPlayer() {
        player1.sendMessage("Who will start? (1 or 2):");
        player1.sendMessage("");

        int input = player1.readInt();

        SocketPlayer chosen = (input == 1) ? player1 : player2;

        player1.sendMessage("You chose: Player " + input);
        player1.sendMessage("");

        player2.sendMessage("Player " + input + " will start the round.");
        player2.sendMessage("");

        return chosen;
    }

    private boolean askToPlayAgain() {
        player1.sendMessage("One more time? (yes/no):");
        player1.sendMessage("");

        try {
            String response = player1.readLine();
            return response != null && response.equalsIgnoreCase("yes");
        } catch (IOException e) {
            player1.sendMessage("Error while reading your answer. Ending game.");
            player1.sendMessage("");
            return false;
        }
    }

    private void endGame() {
        player1.sendMessage("End game");
        player1.sendMessage("");

        player2.sendMessage("End game");
        player2.sendMessage("");
    }
}
