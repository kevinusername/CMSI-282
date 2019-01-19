package pathfinder.uninformed;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first tree search.
 */
public class Pathfinder {

    /**
     * Given a MazeProblem, which specifies the actions and transitions available in the
     * search, returns a solution to the problem as a sequence of actions that leads from
     * the initial to a goal state.
     *
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @return An ArrayList of Strings representing actions that lead from the initial to
     * the goal state, of the format: ["R", "R", "L", ...]
     */
    public static ArrayList<String> solve(MazeProblem problem) {

        ArrayDeque<SearchTreeNode> frontier = new ArrayDeque<>();

        frontier.add(new SearchTreeNode(problem.INITIAL_STATE, null, null));

        while (!frontier.isEmpty()) {
            SearchTreeNode currentNode = frontier.poll();

            if (problem.isGoal(currentNode.state)) { return generatePath(currentNode); }

            Map<String, MazeState> transitions = problem.getTransitions(currentNode.state);
            for (String t : transitions.keySet()) {
                frontier.add(new SearchTreeNode(transitions.get(t), t, currentNode));
            }
        }

        return null;
    }

    /**
     * @param currentNode the final node in a path
     * @return A ArrayList<String> of in-order steps to traverse from the problem's START_STATE to its GOAL_STATE
     * Ex: ["R","D","D","L","U"]
     */
    private static ArrayList<String> generatePath(SearchTreeNode currentNode) {
        ArrayList<String> path = new ArrayList<>();

        while (currentNode.parent != null) {
            path.add(currentNode.action);
            currentNode = currentNode.parent;
        }
        Collections.reverse(path);
        return path;
    }
}

/**
 * SearchTreeNode that is used in the Search algorithm to construct the Search
 * tree.
 */
class SearchTreeNode {

    MazeState state;
    String action;
    SearchTreeNode parent;

    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     *
     * @param state  The MazeState (col, row) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     */
    SearchTreeNode(MazeState state, String action, SearchTreeNode parent) {
        this.state = state;
        this.action = action;
        this.parent = parent;
    }
}