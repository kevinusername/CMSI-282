// Kevin Peters
/* ************************************************
 * IMPORTANT NOTE:
 * ************************************************
 * This program uses never versions of Java. I can confirm it runs on Java 11/12
 *  and does not run on Java 8. Versions in between may or may not work.
 */
package csp;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some unary and binary constraints
 * on the dates of each meeting.
 */
public class CSP {

    /**
     * Public interface for the CSP solver in which the number of meetings,
     * range of allowable dates for each meeting, and constraints on meeting
     * times are specified.
     *
     * @param nMeetings   The number of meetings that must be scheduled, indexed from 0 to n-1
     * @param rangeStart  The start date (inclusive) of the domains of each of the n meeting-variables
     * @param rangeEnd    The end date (inclusive) of the domains of each of the n meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of the n meetings,
     * indexed by the variable they satisfy, or null if no solution exists.
     */
    public static List<LocalDate> solve(int nMeetings, LocalDate rangeStart, LocalDate rangeEnd,
                                        Set<DateConstraint> constraints) {
        Set<LocalDate> dateRange =
                rangeStart.datesUntil(rangeEnd.plusDays(1)).collect(Collectors.toCollection(LinkedHashSet::new));

        /* Map of variable numbers to a DateVar with full domain */
        HashMap<Integer, DateVar> variables = IntStream.range(0, nMeetings).boxed().collect(
                Collectors.toMap(i -> i, i -> new DateVar(i, dateRange), (a, b) -> b, HashMap::new));

        if (!nodeConsistency(constraints, variables) || !constraintPropogation(constraints, variables))
            return null;

        return rBackTracking(new HashMap<>(), new HashSet<>(variables.values()), constraints);
    }



    /*---------------------------------------------------------------
     * Main Methods
     *-------------------------------------------------------------*/


    private static ArrayList<LocalDate> rBackTracking(Map<Integer, LocalDate> assignments,
                                                      HashSet<DateVar> variables,
                                                      Set<DateConstraint> constraints) {
        if (isComplete(variables.size(), assignments, constraints))
            return new ArrayList<>(assignments.values());

        DateVar unassigned = getUnassigned(assignments, variables);
        if (unassigned == null) return null;

        for (LocalDate value : unassigned.domain) {
            assignments.put(unassigned.id, value);
            // TODO: optimize this check if possible
            if (checkAssignments(assignments, constraints)) {
                ArrayList<LocalDate> result = rBackTracking(assignments, variables, constraints);
                if (result != null)
                    return result;
            }
            assignments.remove(unassigned.id);
        }
        return null;
    }

    /**
     * For all unary constraint, remove values from relevant variable that will never be possible
     *
     * @param constraints all constraints in csp
     * @param variables   all variables in csp
     * @return false if any of the variables determined to have an empty domain
     */
    private static boolean nodeConsistency(Set<DateConstraint> constraints, HashMap<Integer, DateVar> variables) {
        for (DateConstraint rule : constraints) {
            if (rule.arity() == 1) {
                UnaryDateConstraint unaryDateConstraint = (UnaryDateConstraint) rule;
                DateVar variable = variables.get(unaryDateConstraint.L_VAL);
                variable.domain = variable.domain.parallelStream()
                                                 .filter(d -> isConsistent(d, unaryDateConstraint.R_VAL,
                                                                           unaryDateConstraint.OP))
                                                 .collect(Collectors.toCollection(LinkedHashSet::new));
                if (variable.domain.isEmpty())
                    return false;
            }
        }
        return true;
    }

    /**
     * For all binary constraints in the CSP, ensure arc consistency for all arcs. i.e. remove impossible values from
     * nodes' domains.
     *
     * @param constraints all constraints in CSP
     * @param variables   all variables in CSP
     * @return false if any of the variables determined to have an empty domain at any point
     */
    private static boolean constraintPropogation(Set<DateConstraint> constraints, HashMap<Integer, DateVar> variables) {
        /* Map all the "neighbor" relationships and make a queue of nodes representing these relationships */
        Queue<arcNode> nodeQueue = new ArrayDeque<>();
        constraints.stream()
                   .filter(d -> d.arity() == 2)
                   .map(d -> (BinaryDateConstraint) d)
                   .forEach(rule -> handleArcs(variables, nodeQueue, rule));

        /* Go through the nodeQueue and remove all inconsistent values */
        while (!nodeQueue.isEmpty()) {
            arcNode pair = nodeQueue.poll();
            DateVar tail = pair.left, head = pair.right;
            /* Collect all tail's domain elements that have no suitable value in the head's domain */
            Set<LocalDate> inconsistent = tail.domain.parallelStream()
                                                     .filter(lDate -> head.domain.stream().noneMatch(
                                                             rDate -> isConsistent(lDate, rDate, pair.op)))
                                                     .collect(Collectors.toCollection(LinkedHashSet::new));

            tail.domain.removeAll(inconsistent);
            // we know the problem to be unsolvable if a variable has no domain
            if (tail.domain.isEmpty())
                return false;

            // If domain changed, re-add arc neighbors->tail to queue
            if (!inconsistent.isEmpty())
                tail.neighbors.forEach(
                        (key, value) -> { if (key != head) nodeQueue.add(new arcNode(key, tail, opInverse(value))); });
        }
        return true;
    }

    private static void handleArcs(HashMap<Integer, DateVar> variables,
                                   Queue<arcNode> nodeQueue,
                                   BinaryDateConstraint rule) {
        DateVar lVal = variables.get(rule.L_VAL);
        DateVar rVal = variables.get(rule.R_VAL);
        lVal.neighbors.put(rVal, rule.OP);
        rVal.neighbors.put(lVal, opInverse(rule.OP));
        nodeQueue.add(new arcNode(lVal, rVal, rule.OP));
        nodeQueue.add(new arcNode(rVal, lVal, opInverse(rule.OP)));
    }


    /*---------------------------------------------------------------
     * Helper Methods
     *-------------------------------------------------------------*/


    private static boolean checkAssignments(Map<Integer, LocalDate> assignments, Set<DateConstraint> constraints) {
        return constraints.parallelStream().allMatch(rule -> {
            LocalDate lVal = assignments.get(rule.L_VAL);
            LocalDate rVal = rule.arity() == 1
                             ? ((UnaryDateConstraint) rule).R_VAL
                             : assignments.get(((BinaryDateConstraint) rule).R_VAL);
            if (lVal == null || rVal == null) return true;

            return isConsistent(lVal, rVal, rule.OP);
        });
    }

    /**
     * @param lVal left hand date of constraint
     * @param rVal right hand date of constraint
     * @param op   relationship between the two dates
     * @return if the given values satisfy the given constraint
     */
    private static boolean isConsistent(LocalDate lVal, LocalDate rVal, String op) {
        switch (op) {
            case ">": if (!lVal.isAfter(rVal)) return false;
                break;
            case "<": if (!lVal.isBefore(rVal)) return false;
                break;
            case ">=": if (lVal.isBefore(rVal)) return false;
                break;
            case "<=": if (lVal.isAfter(rVal)) return false;
                break;
            case "==": if (!lVal.isEqual(rVal)) return false;
                break;
            case "!=": if (lVal.isEqual(rVal)) return false;
                break;
        }
        return true; // This should never happen
    }

    /**
     * Useful for finding the inverse of an arc between two nodes
     *
     * @param op operator to invert
     * @return string of inverted operator
     */
    private static String opInverse(String op) {
        switch (op) {
            case ">": return "<";
            case "<": return ">";
            case ">=": return "<=";
            case "<=": return ">=";
            case "==": return "==";
            case "!=": return "!=";
        }
        return null;
    }


    /* Return the next unassigned variable */
    private static DateVar getUnassigned(Map<Integer, LocalDate> assignments, HashSet<DateVar> variables) {
        for (DateVar variable : variables)
            if (!assignments.containsKey(variable.id))
                return variable;
        return null;
    }


    /* Check if this is a complete solution */
    private static boolean isComplete(int nMeetings, Map<Integer, LocalDate> assignments,
                                      Set<DateConstraint> constraints) {
        return nMeetings == assignments.size() && checkAssignments(assignments, constraints);
    }


    /*---------------------------------------------------------------
     * Private Helper Classes:
     *
     * just some things to help keep it more sane
     *-------------------------------------------------------------*/

    private static class DateVar {
        int id;
        LinkedHashSet<LocalDate> domain;
        HashMap<DateVar, String> neighbors = new HashMap<>();

        DateVar(int meeting, Set<LocalDate> dateRange) {
            id = meeting;
            domain = new LinkedHashSet<>(dateRange);
        }
    }

    private static class arcNode {
        DateVar left, right;
        String op;

        arcNode(DateVar left, DateVar right, String op) {
            this.left = left;
            this.right = right;
            this.op = op;
        }
    }
}

