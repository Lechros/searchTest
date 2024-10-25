public class Matcher {
    public boolean match(String name, String keyword) {
        int j = 0;
        for (int i = 0; i < name.length() && j < keyword.length(); i++) {
            if (charMatches(name.charAt(i), keyword.charAt(j))) {
                j++;
            }
        }
        return j == keyword.length();
    }

    private boolean charMatches(char nameCh, char keywordCh) {
        return Character.toLowerCase(nameCh) == Character.toLowerCase(keywordCh);
    }
}
