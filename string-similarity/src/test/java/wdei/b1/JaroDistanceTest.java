package wdei.b1;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: serprime
 * Date: 12/7/13
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class JaroDistanceTest {

    @Test
    public void testSim() {

        new JaroDistance().calculate("abc", "abc");
        new JaroDistance().calculate("abc", "def");

        new JaroDistance().calculate("abbc", "abbc");

        new JaroDistance().calculate("abbc", "abc");
        new JaroDistance().calculate("abc", "abbc");

        Assert.assertTrue("0.944".equals(String.format("%.3f",
                new JaroDistance().calculate("MARTHA", "MARHTA"))));
        Assert.assertTrue("0.822".equals(String.format("%.3f",
                new JaroDistance().calculate("DWAYNE", "DUANE"))));
        Assert.assertTrue("0.767".equals(String.format("%.3f",
                new JaroDistance().calculate("DIXON", "DICKSONX"))));
        Assert.assertTrue("0.767".equals(String.format("%.3f",
                new JaroDistance().calculate("DICKSONX", "DIXON"))));

        new JaroDistance().calculate("Gamsbart", "Gaumsboat");
    }


}
