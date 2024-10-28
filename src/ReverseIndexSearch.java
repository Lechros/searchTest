import java.util.*;

public class ReverseIndexSearch implements Search {
    private final Map<Long, String> data;
    private final Matcher matcher = new Matcher();
    private Map<Character, Set<Long>> index;

    public ReverseIndexSearch(Map<Long, String> data) {
        this.data = data;
        setupIndex();
    }

    private void setupIndex() {
        index = new HashMap<>();
        for (Map.Entry<Long, String> entry : data.entrySet()) {
            String name = entry.getValue();
            for (int i = 0; i < name.length(); i++) {
                char ch = Character.toLowerCase(name.charAt(i));
                index.computeIfAbsent(ch, k -> new HashSet<>()).add(entry.getKey());
            }
        }
    }

    @Override
    public List<Long> search(String keyword) {
        List<Long> result = new ArrayList<>();
        for (long key : getFilteredIds(keyword)) {
            String name = data.get(key);
            if (matcher.match(name, keyword)) {
                result.add(key);
            }
        }
        return result;
    }

    private Set<Long> getFilteredIds(String keyword) {
        Set<Long> set = index.get(keyword.charAt(0));
        if (set == null) {
            return Collections.emptySet();
        }
        set = new HashSet<>(set);
        for (int i = 1; i < keyword.length(); i++) {
            Set<Long> next = index.get(keyword.charAt(i));
            if (next == null) {
                return Collections.emptySet();
            }
            set.retainAll(next);
        }
        return set;
    }
}
