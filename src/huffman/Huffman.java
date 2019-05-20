// Kevin Peters
package huffman;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

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
        PriorityQueue<HuffNode> nodeQueue = new PriorityQueue<>();
        corpus.chars()
              .mapToObj(c -> (char) c)
              .collect(groupingBy(Character::charValue, summingInt(c -> 1)))
              .forEach((k, v) -> nodeQueue.add(new HuffNode(k, v)));
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

        String binaryStr = message.chars().mapToObj(c -> encodingMap.get((char) c)).collect(Collectors.joining());
        String[] strBytes = binaryStr.split("(?<=\\G.{8})");

        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
        bOutput.write((byte) message.length());
        Arrays.stream(strBytes)
              .mapToInt(b -> (byte) Integer.parseInt(b, 2) << 8 - b.length())
              .forEachOrdered(bOutput::write);

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
        String binaryString =
                IntStream.range(1, compressedMsg.length).mapToObj(i -> compressedMsg[i])
                         // Classic java right here -> (P.S. thanks stackoverflow)
                         .map(b -> String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'))
                         .collect(Collectors.joining());

        return decode(msgLength, binaryString);
    }

    private String decode(int msgLength, String binaryString) {
        StringBuilder result = new StringBuilder();
        HuffNode current = trieRoot;
        int charFound = 0;
        char[] charArray = binaryString.toCharArray();

        // Traverse the trie to decode all characters in string
        int i = 0;
        while (charFound != msgLength) {
            if (current.isLeaf()) {
                result.append(current.character);
                current = trieRoot;
                charFound++;
                continue;
            }
            else if (charArray[i] == '0')
                current = current.left;
            else
                current = current.right;
            i += 1;
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

        public boolean isLeaf() { return left == null && right == null; }

        public int compareTo(HuffNode other) { return this.count - other.count; }
    }

}
