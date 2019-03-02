// Kevin Peters
package nim;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Artificial Intelligence responsible for playing the game of Nim!
 * Implements the alpha-beta-pruning mini-max search algorithm
 */
public class NimPlayer {

    private final int MAX_REMOVAL;

    NimPlayer(int MAX_REMOVAL) {
        this.MAX_REMOVAL = MAX_REMOVAL;
    }

    /**
     * @param remaining Integer representing the amount of stones left in the pile
     * @return An int action representing the number of stones to remove in the range
     * of [1, MAX_REMOVAL]
     */
    public int choose(int remaining) {
        GameTreeNode root = new GameTreeNode(remaining, 0, true);
        alphaBetaMinimax(root, Integer.MIN_VALUE, Integer.MAX_VALUE, true, new HashMap<GameTreeNode, Integer>());

        /* Generate the gametree using alpha-beta pruning and return an optimal choice if applicable,
         * else take the minimum amount possible (1). */
        root.children.sort(Comparator.comparing(node -> node.score));
        GameTreeNode bestChoice = root.children.get(root.children.size() - 1);
        return bestChoice.score == -1 ? 1 : bestChoice.action;
    }

    /**
     * Constructs the minimax game tree by the tenets of alpha-beta pruning with
     * memoization for repeated states.
     *
     * @param node    The root of the current game sub-tree
     * @param alpha   Smallest minimax score possible
     * @param beta    Largest minimax score possible
     * @param isMax   Boolean representing whether the given node is a max (true) or min (false) node
     * @param visited Map of GameTreeNodes to their minimax scores to avoid repeating large subtrees
     * @return Minimax score of the given node + [Side effect] constructs the game tree originating
     * from the given node
     */
    private int alphaBetaMinimax(GameTreeNode node, int alpha, int beta, boolean isMax,
                                 Map<GameTreeNode, Integer> visited) {
        // Memoization for already visited states
        if (visited.containsKey(node)) { return visited.get(node); }

        if (node.remaining == 0) { return handleTerminal(node, isMax, visited); }

        for (int i = 1; i <= Math.min(node.remaining, MAX_REMOVAL); i++) {
            node.children.add(new GameTreeNode(node.remaining - i, i, !node.isMax));
        }

        // Alpha-beta pruning algorithm. Actions based on if MIN or MAX node
        if (isMax) {
            int v = Integer.MIN_VALUE;
            for (GameTreeNode child : node.children) {
                v = Math.max(v, alphaBetaMinimax(child, alpha, beta, false, visited));
                alpha = Math.max(v, alpha);
                if (beta <= alpha) {
                    // Remove pruned children so they aren't considered when evaluating optimal choice
                    node.children.clear();
                    return 1;
                }
            }
            node.score = alpha;
            visited.put(node, node.score);
            return v;
        } else { // implied MIN node
            int v = Integer.MAX_VALUE;
            for (GameTreeNode child : node.children) {
                v = Math.min(v, alphaBetaMinimax(child, alpha, beta, true, visited));
                beta = Math.min(beta, v);
                if (beta <= alpha) {
                    // Remove pruned children so they aren't considered when evaluating optimal choice
                    node.children.clear();
                    return -1;
                }
            }
            node.score = beta;
            visited.put(node, node.score);
            return v;
        }
    }

    /*
     * Score the utility of terminal nodes (nodes with 0 remaining) based on if they are
     * min or max nodes.
     */
    private int handleTerminal(GameTreeNode node, boolean isMax, Map<GameTreeNode, Integer> visited) {
        node.score = isMax ? -1 : 1;
        visited.put(node, node.score);
        return node.score;
    }
}

/**
 * GameTreeNode to manage the Nim game tree.
 */
class GameTreeNode {

    int remaining, action, score;
    boolean isMax;
    ArrayList<GameTreeNode> children;

    /**
     * Constructs a new GameTreeNode with the given number of stones
     * remaining in the pile, and the action that led to it. We also
     * initialize an empty ArrayList of children that can be added-to
     * during search, and a placeholder score of -1 to be updated during
     * search.
     *
     * @param remaining The Nim game state represented by this node: the #
     *                  of stones remaining in the pile
     * @param action    The action (# of stones removed) that led to this node
     * @param isMax     Boolean as to whether or not this is a maxnode
     */
    GameTreeNode(int remaining, int action, boolean isMax) {
        this.remaining = remaining;
        this.action = action;
        this.isMax = isMax;
        children = new ArrayList<>();
        score = -1;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof GameTreeNode && (remaining == ((GameTreeNode) other).remaining &&
                                                 isMax == ((GameTreeNode) other).isMax &&
                                                 action == ((GameTreeNode) other).action);
    }

    @Override
    public int hashCode() {
        return remaining + ((isMax) ? 1 : 0);
    }

}
