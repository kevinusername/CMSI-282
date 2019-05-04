package csp;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

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
        throw new UnsupportedOperationException();
    }

    private static Map<DateVar, LocalDate> rBackTracking(Map<DateVar, LocalDate> assignments) {

    }

    private static boolean checkAssignments(Map<DateVar, LocalDate> assignments, Set<DateConstraint> constraints) {
        for (DateConstraint rule : constraints) {
            LocalDate lVal = assignments.get(rule.L_VAL);
            LocalDate rVal = rule.arity() == 1 ? rule.R_VAL : assignments.get(rule.R_VAL);
            if (lVal == null || rVal == null) continue;
            switch (rule.OP) {
                case ">":
                    if (!lVal.isAfter(rVal)) return false;
                case "<":
                    if (!lVal.isBefore(rVal)) return false;
                case ">=":
                    if (lVal.isBefore(rVal)) return false;
                case "<=":
                    if (lVal.isAfter(rVal)) return false;
                case "==":
                    if (!lVal.isEqual(rVal)) return false;
                case "!=":
                    if (lVal.isEqual(rVal)) return false;
            }
        }
        return true;
    }

    private static boolean isComplete(int nMeetings, Map<DateVar, LocalDate> assignments, Set<DateConstraint> constraints) {
        return nMeetings == assignments.size() && checkAssignments(assignments, constraints);
    }



    private static class DateVar {

        int id;
        Set<LocalDate> domain;

        DateVar(int meeting, LocalDate rangeStart, LocalDate rangeEnd) {
            id = meeting;
            // Toal would be proud of this stream ->
            domain = rangeStart.datesUntil(rangeEnd.plusDays(1)).collect(Collectors.toSet());
        }
    }
}

