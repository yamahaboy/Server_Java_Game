
public class HttpPlayer extends Player {

    private final String playerId;
    private final HttpServerGame.Session session;

    public HttpPlayer(int id, String name, String playerId, HttpServerGame.Session session) {
        super(id, name);
        this.playerId = playerId;
        this.session = session;
    }

    @Override
    public int inputSecretNumber() {
        session.sendTo(playerId, getName() + ", input number:\n");
        return readInt();
    }

    @Override
    public int guessNumber(int[] options) {
        session.sendTo(playerId, getName() + ", it's your turn to guess!\n");
        session.sendTo(playerId, "Here are the numbers to choose from:\n");
        System.out.println("[" + getName() + "] options = " + java.util.Arrays.toString(options));

        System.out.println("[SERVER] options = " + options);

        for (int i = 0; i < options.length; i++) {
            session.sendTo(playerId, (i + 1) + ". " + options[i] + "\n");
        }

        session.sendTo(playerId, "Enter your choice (1, 2 or 3):\n");
        return options[readInt() - 1];
    }

    public int readInt() {
        while (true) {
            String input = session.awaitAnswer(playerId);
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                session.sendTo(playerId, "Invalid input. Try again:\n");
            }
        }
    }

    public void sendMessage(String message) {
        session.sendTo(playerId, message);
    }

    public String readLine() {
        return session.awaitAnswer(playerId);
    }
}
