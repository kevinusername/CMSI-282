// Kevin Peters
package pathfinder.informed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;

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

        if (problem.KEY_STATE == null) { return null; }

        SearchTreeNode keyNode = optimalPath(problem, new SearchTreeNode(problem.INITIAL_STATE, null, null, 0, 0),
                                             problem.KEY_STATE);

        if (keyNode == null) { return null; }

        ArrayList<SearchTreeNode> solutions = new ArrayList<>();
        for (MazeState goal : problem.GOAL_STATES) {
            SearchTreeNode solution = optimalPath(problem, keyNode, goal);
            if (solution != null) { solutions.add(solution); }
        }

        solutions.sort(Comparator.comparingInt(sol -> sol.actual_cost));

        return solutions.isEmpty() ? null : generatePath(solutions.get(0));
    }

    /**
     * Uses A* along with Manhattan Distance Heuristic to find the optimal path between two nodes
     *
     * @param problem    MazeProblem that contains start and end nodes
     * @param startNode  node from the which the path starts
     * @param finalState node which path ends at
     * @return a node with parent references for the optimal path from startNode to endNode
     */
    private static SearchTreeNode optimalPath(MazeProblem problem, SearchTreeNode startNode, MazeState finalState) {
        PriorityQueue<SearchTreeNode> frontier = new PriorityQueue<>(16, Comparator.comparingInt(
                (SearchTreeNode node) -> node.est_cost));
        HashSet<MazeState> graveyard = new HashSet<>();

        frontier.add(startNode);

        while (!(frontier.peek() == null)) {
            SearchTreeNode currentNode = frontier.poll();
            graveyard.add(currentNode.state);

            if (currentNode.state.equals(finalState)) { return currentNode; }

            problem.getTransitions(currentNode.state).entrySet().stream()
                   .filter(action -> !graveyard.contains(action.getValue()))
                   .map(action -> new SearchTreeNode(action.getValue(),
                                                     action.getKey(),
                                                     currentNode,
                                                     hCost(action.getValue(), finalState),
                                                     problem.newCost(currentNode.actual_cost, action.getValue())))
                   .forEachOrdered(frontier::add);
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

    /**
     * Uses Manhattan Heuristic to estimate cost from startState to finalState
     *
     * @param startState starting node for path
     * @param finalState end node for path
     * @return the absolute value of the difference between the nodes coordinates
     */
    private static int hCost(MazeState startState, MazeState finalState) {
        return Math.abs(startState.col - finalState.col) + Math.abs(startState.row - finalState.row);
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
    int est_cost, actual_cost;

    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     *
     * @param state  The MazeState (col, row) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     */
    SearchTreeNode(MazeState state, String action, SearchTreeNode parent, int heuristicCost, int actualCost) {
        this.state = state;
        this.action = action;
        this.parent = parent;
        this.est_cost = heuristicCost + actualCost;
        this.actual_cost = actualCost;
    }
}