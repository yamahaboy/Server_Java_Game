
import java.io.*;
import java.net.Socket;

public class SocketPlayer extends Player {

    private final BufferedReader in;
    private final PrintWriter out;

    public SocketPlayer(int id, String name, Socket socket) throws IOException {
        super(id, name);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public int inputSecretNumber() {
        out.println(getName() + ", input number:");
        out.println();
        return readInt();
    }

    @Override
    public int guessNumber(int[] options) {
        out.println(); // отделение от прошлого раунда
        out.println(getName() + ", it's your turn to guess!");
        out.println("Here are the numbers to choose from:");

        System.out.println("[" + getName() + "] options = " + java.util.Arrays.toString(options));

        for (int i = 0; i < options.length; i++) {
            out.println((i + 1) + ". " + options[i]);
        }

        out.println("Enter your choice (1, 2 or 3):");
        out.println();
        out.flush();

        return options[readInt() - 1];
    }

    int readInt() {
        try {
            return Integer.parseInt(in.readLine());
        } catch (NumberFormatException | IOException e) {
            out.println("Invalid input. Try again.");
            out.println();
            return readInt();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String readLine() throws IOException {
        return in.readLine();
    }
}
