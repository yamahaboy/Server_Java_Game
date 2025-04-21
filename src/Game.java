
public class Game {

    private final Player player1;
    private final Player player2;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void start() {
        while (true) {
            Player startingPlayer = askStartingPlayer();
            Player otherPlayer = (startingPlayer == player1) ? player2 : player1;

            Round round = new Round(startingPlayer, otherPlayer);
            round.play();

            if (!askToPlayAgain()) {
                break;
            }
        }

        endGame();
    }

    private Player askStartingPlayer() {
        player1.sendMessage("Who will start? (1 or 2):");
        player1.sendMessage("");

        int input = player1 instanceof HttpPlayer
                ? ((HttpPlayer) player1).readInt()
                : ((SocketPlayer) player1).readInt();

        Player chosen = (input == 1) ? player1 : player2;

        player1.sendMessage("You chose: Player " + input);
        player1.sendMessage("");

        player2.sendMessage("Player " + input + " will start the round.");
        player2.sendMessage("");

        return chosen;
    }

    private boolean askToPlayAgain() {
        player1.sendMessage("One more time? (yes/no):");
        player1.sendMessage("");

        String response = player1 instanceof HttpPlayer
                ? ((HttpPlayer) player1).readLine()
                : safeReadSocketLine();

        return response != null && response.equalsIgnoreCase("yes");
    }

    private String safeReadSocketLine() {
        return player1.readLine();
    }

    private void endGame() {
        player1.sendMessage("End game");
        player1.sendMessage("");

        player2.sendMessage("End game");
        player2.sendMessage("");
    }
}
