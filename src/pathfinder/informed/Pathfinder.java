// Kevin Peters
package pathfinder.informed;

import java.util.*;

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

        PriorityQueue<SearchTreeNode> frontier = new PriorityQueue<>();
        HashSet<MazeState> graveyard = new HashSet<>();

        frontier.add(new SearchTreeNode(problem.INITIAL_STATE, null, null, 0, 0));

        while (!(frontier.peek() == null)) {
            SearchTreeNode currentNode = frontier.poll();
            graveyard.add(currentNode.state);

            if (problem.isGoal(currentNode.state)) { return generatePath(currentNode); }

            for (Map.Entry<String, MazeState> action : problem.getTransitions(currentNode.state).entrySet()) {
                if (!graveyard.contains(action.getValue())) {
                    frontier.add(new SearchTreeNode(action.getValue(), action.getKey(), currentNode,
                                                    estimateCost(problem, action.getValue()),
                                                    problem.addCost(currentNode.actual_cost, action.getValue())));
                }
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

    private static int estimateCost(MazeProblem problem, MazeState state) {
        return Math.abs(state.col - problem.GOAL_STATE.col) + Math.abs(state.row - problem.GOAL_STATE.row);
    }
}

/**
 * SearchTreeNode that is used in the Search algorithm to construct the Search
 * tree.
 */
class SearchTreeNode implements Comparable<SearchTreeNode>{

    MazeState state;
    String action;
    SearchTreeNode parent;
    int est_cost, actual_cost;

    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     *
     * @param state  The MazeState (col, row) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     */
    SearchTreeNode(MazeState state, String action, SearchTreeNode parent, int est_cost, int actual_cost) {
        this.state = state;
        this.action = action;
        this.parent = parent;
        this.est_cost = est_cost;
        this.actual_cost = actual_cost;
    }

    public int compareTo(SearchTreeNode other) {
        return this.est_cost - other.est_cost;
    }
}