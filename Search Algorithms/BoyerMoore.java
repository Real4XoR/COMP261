import java.util.*;

public class BoyerMoore {
    
    private static final int NO_OF_CHARS = 256;

    public static int search(String pattern, String text) {
        int patternLength = pattern.length();
        int textLength = text.length();

        if (patternLength == 0 || patternLength > textLength)
            return -1;

        int[] badChar = new int[NO_OF_CHARS];
        int[] goodSuffix = new int[patternLength + 1];
        int[] suffixShift = new int[patternLength + 1];

        preProcessBadCharacter(pattern, badChar);
        preProcessGoodSuffix(pattern, goodSuffix, suffixShift);

        int shift = 0;
        while (shift <= textLength - patternLength) {
            int j = patternLength - 1;

            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j))
                j--;

            if (j < 0)
                return shift;

            int badCharShift = j - badChar[text.charAt(shift + j)];
            int goodSuffixShift = suffixShift[j + 1];

            shift += Math.max(badCharShift, goodSuffixShift);
        }

        return -1;
    }

    private static void preProcessBadCharacter(String pattern, int[] badChar) {
        int patternLength = pattern.length();

        Arrays.fill(badChar, -1);

        for (int i = 0; i < patternLength; i++)
            badChar[pattern.charAt(i)] = i;
    }

    private static void preProcessGoodSuffix(String pattern, int[] goodSuffix, int[] suffixShift) {
        int patternLength = pattern.length();
        int lastPrefixPosition = patternLength;

        for (int i = patternLength - 1; i >= 0; i--) {
            if (isPrefix(pattern, i + 1))
                lastPrefixPosition = i + 1;

            suffixShift[i] = lastPrefixPosition - i + patternLength - 1;
        }

        for (int i = 0; i < patternLength - 1; i++) {
            int suffixLength = getSuffixLength(pattern, i);
            suffixShift[suffixLength] = patternLength - 1 - i + suffixLength;
        }

        suffixShift[patternLength] = patternLength;
        Arrays.fill(goodSuffix, patternLength);
    }

    private static boolean isPrefix(String pattern, int p) {
        int patternLength = pattern.length();

        for (int i = p, j = 0; i < patternLength; i++, j++)
            if (pattern.charAt(i) != pattern.charAt(j))
                return false;

        return true;
    }

    private static int getSuffixLength(String pattern, int p) {
        int patternLength = pattern.length();
        int suffixLength = 0;

        for (int i = p, j = patternLength - 1; i >= 0 && pattern.charAt(i) == pattern.charAt(j); i--, j--)
            suffixLength++;

        return suffixLength;
    }
}
