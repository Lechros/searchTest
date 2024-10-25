import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DataGenerator {
    public final int nameLength;
    public final int cardinality;
    private final Random random;

    public DataGenerator(long seed, int nameLength, int cardinality) {
        random = new Random(seed);
        this.nameLength = nameLength;
        this.cardinality = cardinality;
    }

    public Map<Long, String> generate(int count) {
        Map<Long, String> map = new HashMap<>();
        for (int i = 0; i < count; i++) {
            map.put((long) i, generateName(i));
        }
        return map;
    }

    String generateName(int id) {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < nameLength; i++) {
            name.append(getRandomChar());
        }
        return name.toString();
    }

    String generateKeyword(int length) {
        StringBuilder keyword = new StringBuilder();
        for (int i = 0; i < length; i++) {
            keyword.append(getRandomChar());
        }
        return keyword.toString();
    }

    char getRandomChar() {
        return (char) ('가' + random.nextInt(cardinality));
    }
}
