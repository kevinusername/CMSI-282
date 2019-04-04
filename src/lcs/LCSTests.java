package lcs;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import java.util.HashSet;
import java.util.Arrays;

public class LCSTests {
    
    // =================================================
    // Test Configuration
    // =================================================
    
    // Global timeout to prevent infinite loops from
    // crashing the test suite + to test that your
    // memoization is working...
    // If they are, 3 seconds should be more than enough
    @Rule
    public Timeout globalTimeout = Timeout.seconds(3);
    
    // Grade record-keeping
    static int possible = 0, passed = 0;
    
    // the @Before method is run before every @Test
    @Before
    public void init () {
        possible++;
    }
    
    // Each time you pass a test, you get a point! Yay!
    // [!] Requires JUnit 4+ to run
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {
            passed++;
        }
    };
    
    // Used for grading, reports the total number of tests
    // passed over the total possible
    @AfterClass
    public static void gradeReport () {
        System.out.println("============================");
        System.out.println("Tests Complete");
        System.out.println(passed + " / " + possible + " passed!");
        if ((1.0 * passed / possible) >= 0.9) {
            System.out.println("[!] Nice job!"); // Automated acclaim!
        }
        System.out.println("============================");
    }
    
    
    // =================================================
    // Unit Tests
    // =================================================
    
    // Bottom-up LCS Tests
    // -----------------------------------------------
    @Test
    public void BULCSTest_t0() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                ""
            )),
            LCS.bottomUpLCS("", "")
        );
        // LCS.memoCheck can either be the 1 element matrix
        // or null -- up to you, I won't check for cases with
        // empty-String arguments
    }
    
    @Test
    public void BULCSTest_t1() {
        // First assertion: correctness test for set with LCS
        assertEquals(
            new HashSet<>(Arrays.asList(
                ""
            )),
            LCS.bottomUpLCS("A", "B")
        );
        // Second assertion: proper format / solution of memo table
        assertArrayEquals(
            new int[][] {
                {0, 0},
                {0, 0}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void BULCSTest_t2() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "A"
            )),
            LCS.bottomUpLCS("A", "A")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0},
                {0, 1}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void BULCSTest_t3() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABC"
            )),
            LCS.bottomUpLCS("ABC", "ABC")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0},
                {0, 1, 1, 1},
                {0, 1, 2, 2},
                {0, 1, 2, 3}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void BULCSTest_t4() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "AA", "BA"
            )),
            LCS.bottomUpLCS("ABA", "BAA")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0},
                {0, 0, 1, 1},
                {0, 1, 1, 1},
                {0, 1, 2, 2}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void BULCSTest_t5() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "A", "B", "C"
            )),
            LCS.bottomUpLCS("ABC", "CBA")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0},
                {0, 0, 0, 1},
                {0, 0, 1, 1},
                {0, 1, 1, 1}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void BULCSTest_t6() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                ""
            )),
            LCS.bottomUpLCS("ABC", "DEF")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void BULCSTest_t7() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "A", "B", "C"
            )),
            LCS.bottomUpLCS("ABCC", "CBA")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0},
                {0, 0, 0, 1},
                {0, 0, 1, 1},
                {0, 1, 1, 1},
                {0, 1, 1, 1}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void BULCSTest_t8() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABC", "XBC", "XBZ", "ABZ"
            )),
            LCS.bottomUpLCS("XABZC", "AXBCZ")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1},
                {0, 1, 1, 1, 1, 1},
                {0, 1, 1, 2, 2, 2},
                {0, 1, 1, 2, 2, 3},
                {0, 1, 1, 2, 3, 3}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void BULCSTest_t9() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABC", "XBC"
            )),
            LCS.bottomUpLCS("XABZC", "AXBC")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1},
                {0, 1, 1, 1, 1},
                {0, 1, 1, 2, 2},
                {0, 1, 1, 2, 2},
                {0, 1, 1, 2, 3}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void BULCSTest_t10() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABCDE"
            )),
            LCS.bottomUpLCS("XABCDEX", "YABCDEY")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1},
                {0, 0, 1, 2, 2, 2, 2, 2},
                {0, 0, 1, 2, 3, 3, 3, 3},
                {0, 0, 1, 2, 3, 4, 4, 4},
                {0, 0, 1, 2, 3, 4, 5, 5},
                {0, 0, 1, 2, 3, 4, 5, 5}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void BULCSTest_t11() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABCXEZ"
            )),
            LCS.bottomUpLCS("AVBWCXDYEZ", "ABYCXWEZDV")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2},
                {0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                {0, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3},
                {0, 1, 2, 2, 3, 3, 3, 3, 3, 3, 3},
                {0, 1, 2, 2, 3, 4, 4, 4, 4, 4, 4},
                {0, 1, 2, 2, 3, 4, 4, 4, 4, 5, 5},
                {0, 1, 2, 3, 3, 4, 4, 4, 4, 5, 5},
                {0, 1, 2, 3, 3, 4, 4, 5, 5, 5, 5},
                {0, 1, 2, 3, 3, 4, 4, 5, 6, 6, 6}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void BULCSTest_t12() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABCXEZ"
            )),
            LCS.bottomUpLCS("AVBWCXDEZ", "ABYCXWEZDV")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2},
                {0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                {0, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3},
                {0, 1, 2, 2, 3, 3, 3, 3, 3, 3, 3},
                {0, 1, 2, 2, 3, 4, 4, 4, 4, 4, 4},
                {0, 1, 2, 2, 3, 4, 4, 4, 4, 5, 5},
                {0, 1, 2, 2, 3, 4, 4, 5, 5, 5, 5},
                {0, 1, 2, 2, 3, 4, 4, 5, 6, 6, 6}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void BULCSTest_t13() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABCYZ", "ABCDE", "VWXYZ", "VWXDE"
            )),
            LCS.bottomUpLCS("ABCVWXDEYZ", "VWXABCYZDE")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2},
                {0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 3},
                {0, 1, 1, 1, 1, 2, 3, 3, 3, 3, 3},
                {0, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3},
                {0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3},
                {0, 1, 2, 3, 3, 3, 3, 3, 3, 4, 4},
                {0, 1, 2, 3, 3, 3, 3, 3, 3, 4, 5},
                {0, 1, 2, 3, 3, 3, 3, 4, 4, 4, 5},
                {0, 1, 2, 3, 3, 3, 3, 4, 5, 5, 5}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void BULCSTest_t14() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABW", "YZX", "RSX", "YZW", "RSW", "ABX"
            )),
            LCS.bottomUpLCS("ABRSYZXW", "YZRSABWX")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 1, 2, 2, 2},
                {0, 0, 0, 1, 1, 1, 2, 2, 2},
                {0, 0, 0, 1, 2, 2, 2, 2, 2},
                {0, 1, 1, 1, 2, 2, 2, 2, 2},
                {0, 1, 2, 2, 2, 2, 2, 2, 2},
                {0, 1, 2, 2, 2, 2, 2, 2, 3},
                {0, 1, 2, 2, 2, 2, 2, 3, 3}
            },
            LCS.memoCheck
        );
    }
    
    
    // Top-Down LCS Tests
    // -----------------------------------------------
    @Test
    public void TDLCSTest_t0() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                ""
            )),
            LCS.topDownLCS("", "")
        );
        // LCS.memoCheck can either be the 1 element matrix
        // or null -- up to you, I won't check for cases with
        // empty-String arguments
    }
    
    @Test
    public void TDLCSTest_t1() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                ""
            )),
            LCS.topDownLCS("A", "B")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0},
                {0, 0}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void TDLCSTest_t2() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "A"
            )),
            LCS.topDownLCS("A", "A")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0},
                {0, 1}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void TDLCSTest_t3() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABC"
            )),
            LCS.topDownLCS("ABC", "ABC")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 3}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void TDLCSTest_t4() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "AA", "BA"
            )),
            LCS.topDownLCS("ABA", "BAA")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0},
                {0, 0, 1, 0},
                {0, 1, 1, 0},
                {0, 0, 0, 2}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void TDLCSTest_t5() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "A", "B", "C"
            )),
            LCS.topDownLCS("ABC", "CBA")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0},
                {0, 0, 0, 1},
                {0, 0, 1, 1},
                {0, 1, 1, 1}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void TDLCSTest_t6() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                ""
            )),
            LCS.topDownLCS("ABC", "DEF")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void TDLCSTest_t7() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "A", "B", "C"
            )),
            LCS.topDownLCS("ABCC", "CBA")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0},
                {0, 0, 0, 1},
                {0, 0, 1, 1},
                {0, 1, 1, 1},
                {0, 1, 1, 1}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void TDLCSTest_t8() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABC", "XBC", "XBZ", "ABZ"
            )),
            LCS.topDownLCS("XABZC", "AXBCZ")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 0},
                {0, 1, 1, 1, 1, 0},
                {0, 1, 1, 2, 2, 0},
                {0, 1, 1, 2, 0, 3},
                {0, 0, 0, 0, 3, 3}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void TDLCSTest_t9() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABC", "XBC"
            )),
            LCS.topDownLCS("XABZC", "AXBC")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0},
                {0, 1, 1, 0, 0},
                {0, 1, 1, 2, 0},
                {0, 1, 1, 2, 0},
                {0, 0, 0, 0, 3}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void TDLCSTest_t10() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABCDE"
            )),
            LCS.topDownLCS("XABCDEX", "YABCDEY")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1},
                {0, 0, 1, 2, 2, 2, 2, 2},
                {0, 0, 1, 2, 3, 3, 3, 3},
                {0, 0, 1, 2, 3, 4, 4, 4},
                {0, 0, 1, 2, 3, 4, 5, 5},
                {0, 0, 1, 2, 3, 4, 5, 5}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void TDLCSTest_t11() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABCXEZ"
            )),
            LCS.topDownLCS("AVBWCXDYEZ", "ABYCXWEZDV")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2},
                {0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                {0, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3},
                {0, 1, 2, 2, 3, 3, 3, 3, 3, 3, 3},
                {0, 1, 2, 2, 3, 4, 4, 4, 4, 4, 4},
                {0, 1, 2, 2, 3, 4, 4, 4, 4, 5, 5},
                {0, 0, 0, 3, 3, 4, 4, 4, 4, 5, 5},
                {0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5},
                {0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 6}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void TDLCSTest_t12() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABCXEZ"
            )),
            LCS.topDownLCS("AVBWCXDEZ", "ABYCXWEZDV")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2},
                {0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                {0, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3},
                {0, 1, 2, 2, 3, 3, 3, 3, 3, 3, 3},
                {0, 1, 2, 2, 3, 4, 4, 4, 4, 4, 4},
                {0, 1, 2, 2, 3, 4, 4, 4, 4, 5, 5},
                {0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5},
                {0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 6}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void TDLCSTest_t13() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABCYZ", "ABCDE", "VWXYZ", "VWXDE"
            )),
            LCS.topDownLCS("ABCVWXDEYZ", "VWXABCYZDE")
        );
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 1, 2, 2, 2, 2, 0, 0},
                {0, 0, 0, 0, 1, 2, 3, 3, 3, 0, 0},
                {0, 1, 1, 1, 1, 2, 3, 3, 3, 0, 0},
                {0, 1, 2, 2, 2, 2, 3, 3, 3, 0, 0},
                {0, 1, 2, 3, 3, 3, 3, 3, 3, 0, 0},
                {0, 1, 2, 3, 3, 3, 3, 3, 3, 4, 0},
                {0, 1, 2, 3, 3, 3, 3, 3, 3, 4, 5},
                {0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 5},
                {0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5}
            },
            LCS.memoCheck
        );
    }
    
    @Test
    public void TDLCSTest_t14() {
        assertEquals(
            new HashSet<>(Arrays.asList(
                "ABW", "YZX", "RSX", "YZW", "RSW", "ABX"
            )),
            LCS.topDownLCS("ABRSYZXW", "YZRSABWX")
        );
        
        assertArrayEquals(
            new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 1, 2, 2, 0},
                {0, 0, 0, 1, 1, 1, 2, 2, 0},
                {0, 0, 0, 1, 2, 2, 2, 2, 0},
                {0, 1, 1, 1, 2, 2, 2, 2, 0},
                {0, 1, 2, 2, 2, 2, 2, 2, 0},
                {0, 1, 2, 2, 2, 2, 2, 0, 3},
                {0, 0, 0, 0, 0, 0, 0, 3, 3}
            },
            LCS.memoCheck
        );
    }
    
}
