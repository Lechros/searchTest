import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    List<Class<? extends Search>> searchers = List.of(
            IterateAllSearch.class,
            ReverseIndexSearch.class,
            ReverseIndexSearch2.class
    );
    int[][] testCases = {
            // @formatter:off
            /*     nameLength        cardinality
              size(rows)  |  keywordLength   |    iter
                   |      |      |           |      |  */
            {    100,     5,     1,        100, 10000},
            {    100,     5,     1,      10000, 10000},
            {    100,    20,     2,        100, 10000},
            {    100,    20,     2,      10000, 10000},
            {    100,   100,     2,        100, 10000},
            {    100,   100,     2,      10000, 10000},
            {  10000,     5,     1,        100,  1000},
            {  10000,     5,     1,      10000,  1000},
            {  10000,    20,     2,        100,  1000},
            {  10000,    20,     2,      10000,  1000},
            {  10000,   100,     2,        100,  1000},
            {  10000,   100,     2,      10000,  1000},
            {1000000,     5,     1,        100,    10},
            {1000000,     5,     1,      10000,    10},
            {1000000,    20,     2,        100,    10},
            {1000000,    20,     2,      10000,    10},
            {1000000,   100,     2,        100,    10},
            {1000000,   100,     2,      10000,    10},
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
        System.out.println("| Searcher | Row count | Name length | Keyword length | Cardinality | Match Count | Setup time | Query time |");
        System.out.println("|:---|---:|---:|---:|---:|---:|---:|---:|");
        for (TestCaseResult result : results) {
            long minSetupTime = result.setupTimes.stream().min(Long::compareTo).get();
            long minQueryTime = result.queryTimes.stream().min(Long::compareTo).get();
            long minTime = Math.min(minSetupTime, minQueryTime);
            for (int i = 0; i < searchers.size(); i++) {
                long setupTime = result.setupTimes.get(i);
                long queryTime = result.queryTimes.get(i);
                long queryCount = result.queryCounts.get(i);
                System.out.printf("|%s|%d|%d|%d|%d|%d|%s|%s|%n",
                        searchers.get(i).getName(),
                        result.size,
                        result.nameLength,
                        result.keywordLength,
                        result.cardinality,
                        queryCount,
                        timeToString(setupTime) + String.format(" (%.1f)", setupTime / (double) minTime),
                        timeToString(queryTime) + String.format(" (%.1f)", queryTime / (double) minTime));
            }
            System.out.println("|-|-|-|-|-|-|-|-|");
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
        String keyword = generator.generateKeyword(keywordLength);
        long dataEnd = System.nanoTime();
        System.out.println("Setup data done (" + timeToString((dataEnd - dataStart)) + ")");

        // Warmup
        for (Class<? extends Search> searcher : searchers) {
            Constructor<? extends Search> constructor = searcher.getConstructor(Map.class);
            Search instance = constructor.newInstance(data);
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
            System.out.println("Search start");
            long searchStart = System.nanoTime();
            long count = 0;
            for (int i = 0; i < iter; i++) {
                count = instance.search(keyword).size();
            }
            long searchEnd = System.nanoTime();
            System.out.println("Search done (" + timeToString((searchEnd - searchStart) / iter) + ")");
            result.queryTimes.add((searchEnd - searchStart) / iter);
            result.queryCounts.add(count);
        }

        results.add(result);
    }

    private String timeToString(long nanoTime) {
        if (nanoTime < 1000 * 10) {
            return String.format("%.2f μs", nanoTime / 1000.0);
        } else if (nanoTime < 1000 * 1000) {
            return String.format("%d μs", nanoTime / 1000);
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
        public List<Long> queryCounts = new ArrayList<>();
    }
}