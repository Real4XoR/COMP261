
import java.util.*;

public class LempelZiv {
    private static final int WINDOW_SIZE = 100;
    private static final int LOOKAHEAD_BUFFER_SIZE = 8;
    /**
     * Take uncompressed input as a text string, compress it, and return it as a
     * text string.
     */
    public static String compress(String input) {
        StringBuilder compressed = new StringBuilder();
        int inputLength = input.length();
        int currentIndex = 0;

        while (currentIndex < inputLength) {
            int matchIndex = -1;
            int matchLength = 0;
    
            // Find the longest match in the sliding search window
            for (int i = Math.max(0, currentIndex - WINDOW_SIZE); i < currentIndex; i++) {
                int length = findMatchLength(input, i, currentIndex);
                if (length > matchLength) {
                    matchLength = length;
                    matchIndex = i;
                }
            }
    
            if (matchLength > 0) {
                // Encode the match as [offset|length|next char]
                char nextChar = (currentIndex + matchLength < inputLength) ? input.charAt(currentIndex + matchLength) : '\0';
                compressed.append("[").append(currentIndex - matchIndex).append("|").append(matchLength).append("|").append(nextChar).append("]");
                currentIndex += matchLength + 1;
            } else {
                // No match found, encode the character as [0|0|char]
                compressed.append("[0|0|").append(input.charAt(currentIndex)).append("]");
                currentIndex++;
            }
        }
    
        // Append the last character if it was not part of a match
        if (currentIndex == inputLength - 1) {
            compressed.append("[0|0|").append(input.charAt(currentIndex)).append("]");
        }
    
        return compressed.toString();
    }
    private static int findMatchLength(String input, int startIndex, int currentIndex) {
        int matchLength = 0;
        while (currentIndex < input.length() && input.charAt(startIndex) == input.charAt(currentIndex)) {
            matchLength++;
            startIndex++;
            currentIndex++;
        }
        return matchLength;
        }
    /**
     * Take compressed input as a text string, decompress it, and return it as a
     * text string.
     */
    public static String decompress(String compressed) {
        StringBuilder decompressed = new StringBuilder();
        int currentIndex = 0;
        int compressedLength = compressed.length();
    
        while (currentIndex < compressedLength) {
            if (compressed.charAt(currentIndex) == '[') {
                // Extract the match information
                int matchIndexEnd = compressed.indexOf("|", currentIndex + 1);
                int matchIndex = Integer.parseInt(compressed.substring(currentIndex + 1, matchIndexEnd));
    
                int matchLengthEnd = compressed.indexOf("|", matchIndexEnd + 1);
                int matchLength = Integer.parseInt(compressed.substring(matchIndexEnd + 1, matchLengthEnd));
    
                int nextCharEnd = compressed.indexOf("]", matchLengthEnd + 1);
                char nextChar = compressed.charAt(matchLengthEnd + 1);
    
                // Decode the match and append to the decompressed string
                if (matchIndex == 0 && matchLength == 0) {
                    decompressed.append(nextChar);
                } else {
                    int matchStartIndex = decompressed.length() - matchIndex;
                    for (int i = 0; i < matchLength; i++) {
                        decompressed.append(decompressed.charAt(matchStartIndex + i));
                    }
                    decompressed.append(nextChar);
                }
    
                currentIndex = nextCharEnd + 1;
            } else {
                // Invalid format, skip to the next character
                currentIndex++;
            }
        }
    
        return decompressed.toString();
    }
    /**
     * The getInformation method is here for your convenience, you don't need to
     * fill it in if you don't want to. It is called on every run and its return
     * value is displayed on-screen. You can use this to print out any relevant
     * information from your compression.
     */
    public String getInformation() {
        return "";
        }
}
