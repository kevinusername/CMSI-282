package lcs;

import java.util.HashSet;
import java.util.Set;

public class LCS {

    /**
     * memoCheck is used to verify the state of your tabulation after
     * performing bottom-up and top-down DP. Make sure to set it after
     * calling either one of topDownLCS or bottomUpLCS to pass the tests!
     */
    public static int[][] memoCheck;

    // -----------------------------------------------
    // Shared Helper Methods
    // -----------------------------------------------

    private static Set<String> recoverSolution(int[][] solTable, String rStr, String cStr, int row, int col,
                                               StringBuilder curSol) {
        HashSet<String> allSolutions = new HashSet<>();
        int currentVal = solTable[row][col];


        while (currentVal > 0) {
            if (rStr.charAt(row - 1) == cStr.charAt(col - 1)) {
                curSol.append(rStr.charAt(row - 1));
                row--;
                col--;
            } else {
                if (solTable[row - 1][col] > solTable[row][col - 1]) {
                    if (solTable[row - 1][col] > currentVal) {
                        curSol.append(rStr.charAt(row - 1));
                    }
                    row--;
                } else if (solTable[row - 1][col] < solTable[row][col - 1]) {
                    if (solTable[row][col - 1] > currentVal) {
                        curSol.append(cStr.charAt(col - 1));
                    }
                    col--;
                } else {
                    allSolutions.addAll(recoverSolution(solTable, rStr, cStr, row - 1, col, new StringBuilder(curSol)));
                    allSolutions.addAll(recoverSolution(solTable, rStr, cStr, row, col - 1, new StringBuilder(curSol)));
                    break;
                }
            }
            currentVal = solTable[row][col];
        }
        if (currentVal == 0) {
            allSolutions.add(curSol.reverse().toString());
        }

        return allSolutions;
    }


    // -----------------------------------------------
    // Bottom-Up LCS
    // -----------------------------------------------

    /**
     * Bottom-up dynamic programming approach to the LCS problem, which
     * solves larger and larger subproblems iterative using a tabular
     * memoization structure.
     *
     * @param rStr The String found along the table's rows
     * @param cStr The String found along the table's cols
     * @return The longest common subsequence between rStr and cStr +
     * [Side Effect] sets memoCheck to refer to table
     */
    public static Set<String> bottomUpLCS(String rStr, String cStr) {
        int[][] table = BUFillTable(rStr, cStr);

        Set<String> solution = recoverSolution(table, rStr, cStr, rStr.length(), cStr.length(), new StringBuilder());

        memoCheck = table;
        return solution;
    }

    /**
     * @param rStr row String
     * @param cStr col String
     * @return complete table of LCS solution for all subproblems
     */
    private static int[][] BUFillTable(String rStr, String cStr) {
        int[][] table = new int[rStr.length() + 1][cStr.length() + 1];

        for (int i = 1; i <= cStr.length(); i++) {
            for (int j = 1; j <= rStr.length(); j++) {
                fillCell(rStr, cStr, table, i, j);
            }
        }

        return table;
    }

    /**
     * Fills in a given cell based on values of already-filled adjacent cells
     */
    private static void fillCell(String rStr, String cStr, int[][] table, int curRow, int curCol) {
        if (rStr.charAt(curRow - 1) == cStr.charAt(curCol - 1)) {
            table[curRow][curCol] = table[curRow - 1][curCol - 1] + 1;
        } else {
            table[curRow][curCol] = Math.max(table[curRow - 1][curCol], table[curRow][curCol - 1]);
        }
    }


    // -----------------------------------------------
    // Top-Down LCS
    // -----------------------------------------------

    /**
     * Top-down dynamic programming approach to the LCS problem, which
     * solves smaller and smaller subproblems recursively using a tabular
     * memoization structure.
     *
     * @param rStr The String found along the table's rows
     * @param cStr The String found along the table's cols
     * @return The longest common subsequence between rStr and cStr +
     * [Side Effect] sets memoCheck to refer to table
     */
    public static Set<String> topDownLCS(String rStr, String cStr) {
        int row = rStr.length(), col = cStr.length();
        int[][] table = new int[row + 1][col + 1];
        HashSet<int[]> explored = new HashSet<>();
        recursiveFill(table, row, col, rStr, cStr, explored);
        memoCheck = table;

        return recoverSolution(table, rStr, cStr, row, col, new StringBuilder());
    }

    private static void recursiveFill(int[][] table, int row, int col, String rStr, String cStr,
                                      HashSet<int[]> explored) {
        int[] coords = {row, col};
        if (explored.contains(coords)) return;
        else explored.add(coords);

        if (row == 0 || col == 0) return;

        if (rStr.charAt(row - 1) == cStr.charAt(col - 1)) {
            recursiveFill(table, row - 1, col - 1, rStr, cStr, explored);
            fillCell(rStr, cStr, table, row, col);
        } else {
            recursiveFill(table, row - 1, col, rStr, cStr, explored);
            recursiveFill(table, row, col - 1, rStr, cStr, explored);
            fillCell(rStr, cStr, table, row, col);
        }
    }

}
