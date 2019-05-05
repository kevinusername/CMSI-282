package csp;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        HashMap<Integer, DateVar> variables = new HashMap<>();
        for (int i = 0; i < nMeetings; i++)
            variables.put(i, new DateVar(i, rangeStart, rangeEnd));

        constraints.parallelStream().filter(rule -> rule.arity() == 1)
                   .forEach(rule -> nodeConsistency((UnaryDateConstraint) rule, variables));

        constraintPropogation(constraints, variables);

        var result = rBackTracking(new HashMap<>(), new HashSet<>(variables.values()), constraints);
        return result == null ? null : new ArrayList<>(result.values());
    }

    /**
     * For each unary constraint, remove values that will never be possible
     *
     * @param constraint all Unary constraints
     * @param variables  all variables in CSP
     */
    private static void nodeConsistency(UnaryDateConstraint constraint, HashMap<Integer, DateVar> variables) {
        DateVar variable = variables.get(constraint.L_VAL);
        variable.domain = variable.domain.parallelStream().filter(d -> isConsistent(d, constraint.R_VAL, constraint.OP))
                                         .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * For all binary constraints in the CSP, ensure arc consistency for all arcs. i.e. remove impossible values from
     * nodes' domains.
     *
     * @param constraints all constraints in CSP
     * @param variables   all variables in CSP
     */
    private static void constraintPropogation(Set<DateConstraint> constraints, HashMap<Integer, DateVar> variables) {
        /* Map all the "neighbor" relationships and make a queue of nodes representing these relationships */
        HashMap<DateVar, HashMap<DateVar, String>> neigbors = new HashMap<>();
        Queue<arcNode> nodeQueue = new ArrayDeque<>();
        constraints.stream().filter(d -> d.arity() == 2).map(d -> (BinaryDateConstraint) d).forEach(rule -> {
            DateVar lVal = variables.get(rule.L_VAL);
            DateVar rVal = variables.get(rule.R_VAL);
            neigbors.computeIfPresent(lVal, (k, v) -> { v.put(rVal, rule.OP); return v; });
            neigbors.computeIfPresent(rVal, (k, v) -> { v.put(lVal, rule.OP); return v; });
            neigbors.putIfAbsent(lVal, new HashMap<>(Map.of(rVal, rule.OP)));
            neigbors.putIfAbsent(rVal, new HashMap<>(Map.of(lVal, rule.OP)));
            nodeQueue.add(new arcNode(lVal, rVal, rule.OP));
            nodeQueue.add(new arcNode(rVal, lVal, opInverse(rule.OP)));
        });

        /* Go through the nodeQueue and remove all inconsistent values */
        while (!nodeQueue.isEmpty()) {
            arcNode pair = nodeQueue.poll();
            DateVar tail = pair.left, head = pair.right;
            /* Collect all tail's domain elements that have no suitable value in the head's domain */
            Set<LocalDate> inconsistent =
                    tail.domain.parallelStream().filter(lDate -> head.domain.parallelStream().noneMatch(
                            rDate -> isConsistent(lDate, rDate, pair.op))).collect(Collectors.toSet());
            tail.domain.removeAll(inconsistent);
            if (!inconsistent.isEmpty())
                neigbors.get(tail).forEach(
                        (key, value) -> { if (key != head) nodeQueue.add(new arcNode(key, tail, opInverse(value))); });
        }

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

    /**
     * @param lVal left hand date of constraint
     * @param rVal right hand date of constraint
     * @param op   relationship between the two dates
     * @return if the given values satisfy the given constraint
     */
    private static boolean isConsistent(LocalDate lVal, LocalDate rVal, String op) {
        switch (op) {
            case ">": if (!lVal.isAfter(rVal)) return false; break;
            case "<": if (!lVal.isBefore(rVal)) return false; break;
            case ">=": if (lVal.isBefore(rVal)) return false; break;
            case "<=": if (lVal.isAfter(rVal)) return false; break;
            case "==": if (!lVal.isEqual(rVal)) return false; break;
            case "!=": if (lVal.isEqual(rVal)) return false; break;
        }
        return true; // This should never happen
    }


    private static Map<Integer, LocalDate> rBackTracking(Map<Integer, LocalDate> assignments,
                                                         HashSet<DateVar> variables,
                                                         Set<DateConstraint> constraints) {
        if (isComplete(variables.size(), assignments, constraints))
            return assignments;

        DateVar unassigned = getUnassigned(assignments, variables);
        if (unassigned == null) return null;

        for (LocalDate value : unassigned.domain) {
            assignments.put(unassigned.id, value);
            // TODO: optimize this check
            if (checkAssignments(assignments, constraints)) {
                Map<Integer, LocalDate> result = rBackTracking(assignments, variables, constraints);
                if (result != null)
                    return result;
            }
            assignments.remove(unassigned.id);
        }
        return null;
    }


    private static DateVar getUnassigned(Map<Integer, LocalDate> assignments, HashSet<DateVar> variables) {
        for (DateVar variable : variables)
            if (!assignments.containsKey(variable.id))
                return variable;
        return null;
    }


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


    private static boolean isComplete(int nMeetings, Map<Integer, LocalDate> assignments,
                                      Set<DateConstraint> constraints) {
        return nMeetings == assignments.size() && checkAssignments(assignments, constraints);
    }


    private static class DateVar {

        int id;
        HashSet<LocalDate> domain;

        DateVar(int meeting, LocalDate rangeStart, LocalDate rangeEnd) {
            id = meeting;
            // Toal would be proud of this stream ->
            domain = (HashSet<LocalDate>) rangeStart.datesUntil(rangeEnd.plusDays(1)).collect(Collectors.toSet());
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

