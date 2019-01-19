package pathfinder.uninformed;

import java.util.ArrayDeque;
import java.util.ArrayList;
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

        ArrayDeque<SearchTreeNode> frontier = new ArrayDeque<SearchTreeNode>();

        frontier.add(new SearchTreeNode(problem.GOAL_STATE, null, null));

        // TODO: Loop: as long as the frontier is not empty...
        while (!frontier.isEmpty()) {
            SearchTreeNode currentNode = frontier.poll();
            Map<String, MazeState> transitions = problem.getTransitions(currentNode.state);
            for (String t : transitions.keySet()) {
                frontier.add(new SearchTreeNode(transitions.get(t), t, currentNode));
            }
        }


        // TODO: If that node's state is the goal (see problem's isGoal method),
        // you're done! Return the solution
        // [Hint] Use a helper method to collect the solution from the current node!

        // TODO: Otherwise, must generate children to keep searching. So, use the
        // problem's getTransitions method from the currently expanded node's state...

        // TODO: ...and *for each* of those transition states...
        // [Hint] Look up how to iterate through <key, value> pairs in a Map -- an
        // example of this is already done in the MazeProblem's getTransitions method

        // TODO: ...add a new SearchTreeNode to the frontier with the appropriate
        // action, state, and parent

        // Should never get here, but just return null to make the compiler happy
        return null;
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