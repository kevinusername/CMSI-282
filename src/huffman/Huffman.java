package huffman;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Huffman instances provide reusable Huffman Encoding Maps for
 * compressing and decompressing text corpi with comparable
 * distributions of characters.
 */
public class Huffman {

    // -----------------------------------------------
    // Construction
    // -----------------------------------------------

    private HuffNode trieRoot;
    private Map<Character, String> encodingMap = new HashMap<>();

    /**
     * Creates the Huffman Trie and Encoding Map using the character
     * distributions in the given text corpus
     *
     * @param corpus A String representing a message / document corpus
     *               with distributions over characters that are implicitly used
     *               throughout the methods that follow. Note: this corpus ONLY
     *               establishes the Encoding Map; later compressed corpi may
     *               differ.
     */
    Huffman(String corpus) {
        HashMap<Character, Integer> counter = new HashMap<>();
        for (char c : corpus.toCharArray()) {
            // The fanciest java I will ever write
            counter.computeIfPresent(c, (k, v) -> v + 1);
            counter.putIfAbsent(c, 1);
        }
        PriorityQueue<HuffNode> nodeQueue = new PriorityQueue<>(counter.size());
        counter.forEach((k, v) -> nodeQueue.add(new HuffNode(k, v)));
        generateTrie(nodeQueue);
        generateEncoding(trieRoot, "");
    }

    private void generateEncoding(HuffNode n, String encoding) {
        if (null == n) return;
        if (n.isLeaf())
            encodingMap.put(n.character, encoding);
        else {
            generateEncoding(n.left, encoding + '0');
            generateEncoding(n.right, encoding + '1');
        }
    }

    private void generateTrie(PriorityQueue<HuffNode> nodeQueue) {
        while (nodeQueue.size() > 1) {
            HuffNode small = nodeQueue.poll(), big = nodeQueue.poll();
//            if (small.count == big.count && small.character > big.character) {
//                HuffNode swap = big;
//                big = small;
//                small = swap;
//            }
            HuffNode combined = new HuffNode(small.character, small.count + big.count);
            combined.left = small;
            combined.right = big;
            nodeQueue.add(combined);
        }
        trieRoot = nodeQueue.poll();
    }

    // -----------------------------------------------
    // Compression
    // -----------------------------------------------

    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap
     * field generated during construction for this purpose.
     *
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the
     * Huffman coded bytecode. Formatted as 3 components: (1) the
     * first byte contains the number of characters in the message,
     * (2) the bitstring containing the message itself, (3) possible
     * 0-padding on the final byte.
     */
    public byte[] compress(String message) {
        StringBuilder binaryVersion = new StringBuilder();
        for (char c : message.toCharArray())
            binaryVersion.append(encodingMap.get(c));
        String[] strBytes = binaryVersion.toString().split("(?<=\\G.{8})");

        // Pad the last byte in a disgusting manner
        strBytes[strBytes.length - 1] =
                strBytes[strBytes.length - 1].concat("0".repeat(8 - strBytes[strBytes.length - 1].length()));

        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
        bOutput.write((byte) message.length());
        for (String b : strBytes)
            bOutput.write((byte) Integer.parseInt(b, 2));
        return bOutput.toByteArray();
    }


    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------

    /**
     * Decompresses the given compressed array of bytes into their original,
     * String representation. Uses the trieRoot field (the Huffman Trie) that
     * generated the compressed message during decoding.
     *
     * @param compressedMsg {@code byte[]} representing the compressed corpus with the
     *                      Huffman coded bytecode. Formatted as 3 components: (1) the
     *                      first byte contains the number of characters in the message,
     *                      (2) the bitstring containing the message itself, (3) possible
     *                      0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode message.
     */
    public String decompress(byte[] compressedMsg) {
        int msgLength = (int) compressedMsg[0];
        int charFound = 0;

        StringBuilder binaryBuilder = new StringBuilder();
        for (int i = 1; i < compressedMsg.length; i++) {
            byte b = compressedMsg[i];
            binaryBuilder.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        String binaryString = binaryBuilder.toString();
        return decode(msgLength, charFound, binaryString);
    }

    private String decode(int msgLength, int charFound, String binaryString) {
        StringBuilder result = new StringBuilder();
        HuffNode current = trieRoot;

        char[] charArray = binaryString.toCharArray();
        // Traverse the trie to decode all characters in string
        for (int i = 0; i < charArray.length; ) {
            if (current.isLeaf()) {
                result.append(current.character);
                current = trieRoot;
                charFound++;
            } else if (charArray[i] == '0') {
                current = current.left;
                i++;
            } else {
                current = current.right;
                i++;
            }
            if (charFound == msgLength) break;
        }

        return result.toString();
    }


    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------

    /**
     * Huffman Trie Node class used in construction of the Huffman Trie.
     * Each node is a binary (having at most a left and right child), contains
     * a character field that it represents (in the case of a leaf, otherwise
     * the null character \0), and a count field that holds the number of times
     * the node's character (or those in its subtrees) appear in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {

        HuffNode left, right;
        char character;
        int count;

        HuffNode(char character, int count) {
            this.count = count;
            this.character = character;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        public int compareTo(HuffNode other) {
//            return Comparator.comparingInt((HuffNode n) -> n.count)
//                             .thenComparing((HuffNode n) -> n.character)
//                             .compare(this, other);
            return this.count - other.count;
        }

    }

}
