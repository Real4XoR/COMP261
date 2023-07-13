/**
 * A new instance of HuffmanCoding is created for every run. The constructor is
 * passed the full text to be encoded or decoded, so this is a good place to
 * construct the tree. You should store this tree in a field and then use it in
 * the encode and decode methods.
 */

import java.util.*;
public class HuffmanCoding {
    private Node root;
    /**
     * This would be a good place to compute and store the tree.
     */
    public HuffmanCoding(String text) {
        buildTree(text);
    }
    private void buildTree(String text) {
        Map<Character, Integer> frequencyTable = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencyTable.put(c, frequencyTable.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(Node::getFrequency).thenComparing(Node::getCharacter));
        for (Map.Entry<Character, Integer> entry : frequencyTable.entrySet()) {
            queue.offer(new Node(entry.getKey(), entry.getValue()));
        }

        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            Node parent = new Node('\0', left.getFrequency() + right.getFrequency());
            parent.setLeft(left);
            parent.setRight(right);
            queue.offer(parent);
        }

        root = queue.poll();
    }
    /**
     * Take an input string, text, and encode it with the stored tree. Should
     * return the encoded text as a binary string, that is, a string containing
     * only 1 and 0.
     */
    public String encode(String text) {
        StringBuilder encodedText = new StringBuilder();
        for (char c : text.toCharArray()) {
            String code = traverseTree(root, c, "");
            encodedText.append(code);
        }
        return encodedText.toString();
    }

    /**
     * Take encoded input as a binary string, decode it using the stored tree,
     * and return the decoded text as a text string.
     */
    public String decode(String encoded) {
        StringBuilder decodedText = new StringBuilder();
        Node currentNode = root;
        for (char c : encoded.toCharArray()) {
            if (c == '0') {
                currentNode = currentNode.getLeft();
            } else if (c == '1') {
                currentNode = currentNode.getRight();
            }

            if (currentNode.isLeaf()) {
                decodedText.append(currentNode.getCharacter());
                currentNode = root;
            }
        }
        return decodedText.toString();
    }

    /**
     * The getInformation method is here for your convenience, you don't need to
     * fill it in if you don't wan to. It is called on every run and its return
     * value is displayed on-screen. You could use this, for example, to print
     * out the encoding tree.
     */
    public String getInformation() {
        return "";
    }
    private String traverseTree(Node node, char target, String code) {
        if (node.isLeaf()) {
            if (node.getCharacter() == target) {
                return code;
            } else {
                return null;
            }
        }

        String leftCode = traverseTree(node.getLeft(), target, code + "0");
        if (leftCode != null) {
            return leftCode;
        }

        String rightCode = traverseTree(node.getRight(), target, code + "1");
        if (rightCode != null) {
            return rightCode;
        }

        return null;
    }
    private static class Node {
        private final char character;
        private final int frequency;
        private Node left;
        private Node right;

        public Node(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        public char getCharacter() {
            return character;
        }

        public int getFrequency() {
            return frequency;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }
    }
}