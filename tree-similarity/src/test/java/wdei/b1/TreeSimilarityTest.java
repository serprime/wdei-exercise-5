package wdei.b1;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: serprime
 * Date: 05.12.13
 * Time: 19:35
 * To change this template use File | Settings | File Templates.
 */
public class TreeSimilarityTest {

    @Test
    public void testDocs() {
        String left = "" +
                "<html><h1>Some headline</h1><p>and a paragraph</p></html>";
        String right = "" +
                "<html><h1>Some headline</h1><p>and a paragraph</p></html>";

        Document leftDoc = Jsoup.parse(left);
        Document rightDoc = Jsoup.parse(right);

        System.out.println("node matches: " + new TreeSimilarity().calculate(leftDoc, rightDoc));

    }

}
