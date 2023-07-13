/**
 * A new KMP instance is created for every substring search performed. Both the
 * pattern and the text are passed to the constructor and the search method. You
 * could, for example, use the constructor to create the match table and the
 * search method to perform the search itself.
 */
public class KMP {

    /**
     * Perform KMP substring search on the given text with the given pattern.
     * 
     * This should return the starting index of the first substring match if it
     * exists, or -1 if it doesn't.
     */
    public static int search(String pattern, String text) {
        int[] matchTable = computeMatchTable(pattern);
        return performStringSearch(pattern, text, matchTable);
    }
     private static int[] computeMatchTable(String pattern) {
        int[] matchTable = new int[pattern.length()];
        int i = 0, j = 1;
        matchTable[0] = 0;

        while (j < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(j)) {
                matchTable[j] = i + 1;
                i++;
                j++;
            } else {
                if (i != 0) {
                    i = matchTable[i - 1];
                } else {
                    matchTable[j] = 0;
                    j++;
                }
            }
        }

        return matchTable;
    }
    
     private static int performStringSearch(String pattern, String text, int[] matchTable) {
        int i = 0, j = 0;

        while (i < text.length() && j < pattern.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            } else {
                if (j != 0) {
                    j = matchTable[j - 1];
                } else {
                    i++;
                }
            }
        }

        if (j == pattern.length()) {
            return i - j; // Return the starting index of the match
        }

        return -1; // No match found
    }
}
