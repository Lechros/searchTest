import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    List<Class<? extends Search>> searchers = List.of(
            IterateAllSearch.class,
            ReverseIndexSearch.class
    );
    int[][] testCases = {
            // @formatter:off
            /*     nameLength        cardinality
              size(rows)  |  keywordLength   |    iter
                   |      |      |           |      |  */
            {    100,    10,     3, '힣' - '가', 10000},
            {  10000,    20,     3, '힣' - '가',  1000},
            {  10000,    20,    10, '힣' - '가',  1000},
            {  10000,    20,     3,        100,  1000},
            { 100000,   100,     5, '힣' - '가',    10},
            { 100000,   100,    50, '힣' - '가',    10},
            { 100000,   100,     5,        100,    10},
            { 100000,   100,    50,        100,    10},
            {1000000,    20,     3,        500,    10},
            // @formatter:on
    };
    List<TestCaseResult> results = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new Main().run();
    }

    public void run() throws Exception {
        for (int[] tc : testCases) {
            test(tc[0], tc[1], tc[2], tc[3], tc[4]);
        }
        System.out.println();
        System.out.println();
        printResult();
    }

    private void printResult() {
        System.out.println("| Searcher | Row count | Name length | Keyword length | Cardinality | Setup time | Query time |");
        System.out.println("|:---|---:|---:|---:|---:|---:|---:|");
        for (TestCaseResult result : results) {
            for (int i = 0; i < searchers.size(); i++) {
                long setupTime = result.setupTimes.get(i);
                long queryTime = result.queryTimes.get(i);
                System.out.printf("|%s|%d|%d|%d|%d|%s|%s|%n",
                        searchers.get(i).getName(),
                        result.size,
                        result.nameLength,
                        result.keywordLength,
                        result.cardinality,
                        timeToString(setupTime),
                        timeToString(queryTime));
            }
            System.out.println("|-|-|-|-|-|-|-|");
        }
    }

    private void test(int size, int nameLength, int keywordLength, int cardinality, int iter) throws Exception {
        TestCaseResult result = new TestCaseResult();
        result.size = size;
        result.nameLength = nameLength;
        result.keywordLength = keywordLength;
        result.cardinality = cardinality;

        System.out.println("--------------------");
        System.out.println("Setup data (size=" + size + ", nameLength=" + nameLength + ", keywordLength=" + keywordLength + ")");
        long dataStart = System.nanoTime();
        DataGenerator generator = new DataGenerator(nameLength, keywordLength, cardinality);
        Map<Long, String> data = generator.generate(size);
        long dataEnd = System.nanoTime();
        System.out.println("Setup data done (" + timeToString((dataEnd - dataStart)) + ")");

        // Warmup
        for (Class<? extends Search> searcher : searchers) {
            Constructor<? extends Search> constructor = searcher.getConstructor(Map.class);
            Search instance = constructor.newInstance(data);
            String keyword = generator.generateKeyword(keywordLength);
            for (int i = 0; i < iter / 5; i++) {
                instance.search(keyword);
            }
        }

        for (Class<? extends Search> searcher : searchers) {
            System.out.println("-----");
            Constructor<? extends Search> constructor = searcher.getConstructor(Map.class);
            System.out.println("Init " + searcher.getName());
            long setupStart = System.nanoTime();
            Search instance = constructor.newInstance(data);
            long setupEnd = System.nanoTime();
            System.out.println("Init done (" + timeToString((setupEnd - setupStart)) + ")");
            result.setupTimes.add(setupEnd - setupStart);

            String keyword = generator.generateKeyword(keywordLength);
            System.out.println("Search start");
            long searchStart = System.nanoTime();
            for (int i = 0; i < iter; i++) {
                instance.search(keyword);
            }
            long searchEnd = System.nanoTime();
            System.out.println("Search done (" + timeToString((searchEnd - searchStart) / iter) + ")");
            result.queryTimes.add((searchEnd - searchStart) / iter);
        }

        results.add(result);
    }

    private String timeToString(long nanoTime) {
        if (nanoTime < 1000 * 1000) {
            return String.format("%.4f ms", nanoTime / 1000000.0);
        } else if (nanoTime < 1000 * 1000 * 10) {
            return String.format("%.1f ms", nanoTime / 1000000.0);
        } else {
            return String.format("%d ms", nanoTime / 1000000);
        }
    }

    static class TestCaseResult {
        public int size;
        public int nameLength;
        public int keywordLength;
        public int cardinality;
        public List<Long> setupTimes = new ArrayList<>();
        public List<Long> queryTimes = new ArrayList<>();
    }
}