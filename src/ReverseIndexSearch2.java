import java.util.*;

public class ReverseIndexSearch2 implements Search {
    private final Map<Long, String> data;
    private final Matcher matcher = new Matcher();
    private Map<Character, Map<Long, List<Integer>>> index;

    public ReverseIndexSearch2(Map<Long, String> data) {
        this.data = data;
        setupIndex();
    }

    private void setupIndex() {
        index = new HashMap<>();
        for (Map.Entry<Long, String> entry : data.entrySet()) {
            String name = entry.getValue();
            for (int i = 0; i < name.length(); i++) {
                char ch = Character.toLowerCase(name.charAt(i));
                Map<Long, List<Integer>> map = index.computeIfAbsent(ch, k -> new HashMap<>());
                map.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(i);
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
        Map<Long, List<Integer>> first = index.get(keyword.charAt(0));
        if (first == null) {
            return Collections.emptySet();
        }
        Map<Long, Integer> map = new HashMap<>();
        for (Map.Entry<Long, List<Integer>> entry : first.entrySet()) {
            Long id = entry.getKey();
            List<Integer> charPos = entry.getValue();
            map.put(id, charPos.get(0));
        }
        if (map.isEmpty()) {
            return Collections.emptySet();
        }
        for (int i = 1; i < keyword.length(); i++) {
            Map<Long, List<Integer>> other = index.get(keyword.charAt(i));
            Map<Long, Integer> next = new HashMap<>();
            for (Map.Entry<Long, Integer> entry : map.entrySet()) {
                Long id = entry.getKey();
                Integer pos = entry.getValue();
                List<Integer> charPos = other.get(id);
                if (charPos == null) {
                    continue;
                }
                int insertPos = Collections.binarySearch(charPos, pos);
                if (insertPos >= 0) {
                    if (charPos.size() > insertPos + 1) {
                        next.put(id, charPos.get(insertPos + 1));
                    }
                } else {
                    insertPos = -insertPos - 1;
                    if (charPos.size() > insertPos) {
                        next.put(id, charPos.get(insertPos));
                    }
                }
            }
            if (next.isEmpty()) {
                return Collections.emptySet();
            }
            map = next;
        }
        return map.keySet();
    }
}
