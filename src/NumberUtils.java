import java.util.Random;

public class NumberUtils {
    private static final Random random = new Random(); 

    public static int[] generateRandomNumbers(int baseNumber) {
        int randomNumber1 = baseNumber + random.nextInt(31) - 15;
        int randomNumber2;
        do {
            randomNumber2 = baseNumber + random.nextInt(31) - 15;
        } while (randomNumber2 == randomNumber1 || randomNumber2 == baseNumber);

        return new int[]{baseNumber, randomNumber1, randomNumber2};
    }

    public static void shuffleArray(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}
