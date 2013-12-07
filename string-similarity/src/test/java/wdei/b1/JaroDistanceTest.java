package wdei.b1;

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
    }


}
