import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IterateAllSearch implements Search {
    private final Map<Long, String> data;
    private final Matcher matcher = new Matcher();

    public IterateAllSearch(Map<Long, String> data) {
        this.data = data;
    }

    @Override
    public List<Long> search(String keyword) {
        List<Long> result = new ArrayList<>();
        for (Map.Entry<Long, String> entry : data.entrySet()) {
            if (matcher.match(entry.getValue(), keyword)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }
}
