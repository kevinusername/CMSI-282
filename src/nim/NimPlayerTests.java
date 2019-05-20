package nim;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;

public class NimPlayerTests {

    // =================================================
    // Test Configuration
    // =================================================

    // Grade record-keeping
    static int possible = 0, passed = 0;
    // Global timeout to prevent infinite loops from
    // crashing the test suite + to test that your
    // alpha-beta pruning and memoization are working;
    // If they are, 3 seconds should be more than enough
    @Rule
    public Timeout globalTimeout = Timeout.seconds(3);
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
    public static void gradeReport() {
        System.out.println("============================");
        System.out.println("Tests Complete");
        System.out.println(passed + " / " + possible + " passed!");
        if ((1.0 * passed / possible) >= 0.9) {
            System.out.println("[!] Nice job!"); // Automated acclaim!
        }
        System.out.println("============================");
    }

    // the @Before method is run before every @Test
    @Before
    public void init() {
        possible++;
    }

    // =================================================
    // Unit Tests
    // =================================================

    /**
     * Basic test to make sure the nimesis knows the
     * base cases: how to win when presented with the
     * opportunity
     */
    @Test
    public void NimPlayerTest_t0() {
        NimPlayer nimesis = new NimPlayer(2);
        assertEquals(1, nimesis.choose(1));
        assertEquals(2, nimesis.choose(2));
    }

    /**
     * One-ply above winning condition to see it
     * the nimesis can put itself into a sure win
     */
    @Test
    public void NimPlayerTest_t1() {
        NimPlayer nimesis = new NimPlayer(2);
        assertEquals(1, nimesis.choose(4));
        assertEquals(2, nimesis.choose(5));
    }

    /**
     * OK, two-ply now, just to make sure!
     */
    @Test
    public void NimPlayerTest_t2() {
        NimPlayer nimesis = new NimPlayer(2);
        assertEquals(1, nimesis.choose(6));
        assertEquals(1, nimesis.choose(7));
    }

    /**
     * OK, can now take between 1 - 3 stones
     */
    @Test
    public void NimPlayerTest_t3() {
        NimPlayer nimesis = new NimPlayer(3);
        assertEquals(1, nimesis.choose(1));
        assertEquals(2, nimesis.choose(2));
        assertEquals(3, nimesis.choose(3));
    }

    /**
     * Same, but 1-ply from victory
     */
    @Test
    public void NimPlayerTest_t4() {
        NimPlayer nimesis = new NimPlayer(3);
        assertEquals(1, nimesis.choose(5));
        assertEquals(2, nimesis.choose(6));
        assertEquals(3, nimesis.choose(7));
    }

    /**
     * Same, but 2-ply from victory
     */
    @Test
    public void NimPlayerTest_t5() {
        NimPlayer nimesis = new NimPlayer(3);
        assertEquals(2, nimesis.choose(10));
        assertEquals(1, nimesis.choose(9));
        assertEquals(1, nimesis.choose(8));
    }

    /**
     * OK, welcome to flavor country
     */
    @Test
    public void NimPlayerTest_t6() {
        NimPlayer nimesis = new NimPlayer(3);
        assertEquals(1, nimesis.choose(40));
        assertEquals(3, nimesis.choose(39));
        assertEquals(2, nimesis.choose(38));
    }

    /**
     * OK, *REALLY* welcome to flavor country
     */
    @Test
    public void NimPlayerTest_t7() {
        NimPlayer nimesis = new NimPlayer(3);
        assertEquals(1, nimesis.choose(1000));
    }

    @Test
    public void NimPlayerTest_t8() {
        NimPlayer nimesis = new NimPlayer(4);
        assertEquals(1, nimesis.choose(1));
        assertEquals(2, nimesis.choose(2));
        assertEquals(3, nimesis.choose(3));
        assertEquals(4, nimesis.choose(4));
    }

    @Test
    public void NimPlayerTest_t9() {
        NimPlayer nimesis = new NimPlayer(4);
        assertEquals(1, nimesis.choose(6));
        assertEquals(2, nimesis.choose(7));
        assertEquals(3, nimesis.choose(8));
        assertEquals(4, nimesis.choose(9));
    }

    @Test
    public void NimPlayerTest_t10() {
        NimPlayer nimesis = new NimPlayer(4);
        assertEquals(1, nimesis.choose(10));
        assertEquals(1, nimesis.choose(11));
        assertEquals(2, nimesis.choose(12));
        assertEquals(3, nimesis.choose(13));
        assertEquals(4, nimesis.choose(14));
    }

    @Test
    public void NimPlayerTest_t11() {
        NimPlayer nimesis = new NimPlayer(4);
        assertEquals(1, nimesis.choose(11));
        assertEquals(2, nimesis.choose(12));
        assertEquals(1, nimesis.choose(10));
        assertEquals(1, nimesis.choose(10));
        assertEquals(3, nimesis.choose(13));
        assertEquals(4, nimesis.choose(14));
        assertEquals(3, nimesis.choose(13));
    }

    @Test
    public void NimPlayerTest_t12() {
        NimPlayer nimesis = new NimPlayer(5);
        assertEquals(1, nimesis.choose(1));
        assertEquals(2, nimesis.choose(2));
        assertEquals(3, nimesis.choose(3));
        assertEquals(4, nimesis.choose(4));
        assertEquals(5, nimesis.choose(5));
    }

    @Test
    public void NimPlayerTest_t13() {
        NimPlayer nim1 = new NimPlayer(3);
        NimPlayer nim2 = new NimPlayer(3);
        assertEquals(1, nim1.choose(9));
        assertEquals(3, nim2.choose(7));
        assertEquals(1, nim1.choose(4));
        assertEquals(3, nim2.choose(3));
    }

    @Test
    public void NimPlayerTest_t14() {
        NimPlayer nim1 = new NimPlayer(3);
        NimPlayer nim2 = new NimPlayer(3);
        assertEquals(1, nim1.choose(9));
        assertEquals(1, nim1.choose(9));
        assertEquals(3, nim2.choose(7));
        assertEquals(1, nim1.choose(4));
        assertEquals(1, nim1.choose(4));
        assertEquals(3, nim2.choose(3));
        assertEquals(3, nim2.choose(3));
    }

    @Test
    public void NimPlayerTest_t15() {
        NimPlayer nimesis = new NimPlayer(4);
        assertEquals(1, nimesis.choose(1000));
    }

    @Test
    public void NimPlayerTest_t16() {
        NimPlayer nimesis = new NimPlayer(4);
        assertEquals(1, nimesis.choose(1001));
        assertEquals(2, nimesis.choose(1002));
        assertEquals(3, nimesis.choose(1003));
        assertEquals(4, nimesis.choose(1004));
    }

    @Test
    public void NimPlayerTest_t17() {
        NimPlayer nimesis = new NimPlayer(4);
        assertEquals(1, nimesis.choose(1001));
        assertEquals(2, nimesis.choose(1002));
        assertEquals(3, nimesis.choose(1003));
        assertEquals(1, nimesis.choose(1001));
        assertEquals(3, nimesis.choose(1003));
        assertEquals(4, nimesis.choose(1004));
    }

}