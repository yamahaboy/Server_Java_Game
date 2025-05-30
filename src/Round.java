
public class Round {

    private final Player setter;
    private final Player guesser;

    public Round(Player setter, Player guesser) {
        this.setter = setter;
        this.guesser = guesser;
    }

    public void play() {
        int secretNumber = setter.inputSecretNumber();

        int[] options = NumberUtils.generateRandomNumbers(secretNumber);
        NumberUtils.shuffleArray(options);

        int guess = guesser.guessNumber(options);

        String resultMessage;
        if (guess == secretNumber) {
            resultMessage = "SUCCESS! Player " + guesser.getId() + " guessed correctly.";
        } else {
            resultMessage = "FAIL! Correct number was: " + secretNumber;
        }

        setter.sendMessage(resultMessage);
        setter.sendMessage("");

        guesser.sendMessage(resultMessage);
        guesser.sendMessage("");

        System.out.println(resultMessage);
    }
}
