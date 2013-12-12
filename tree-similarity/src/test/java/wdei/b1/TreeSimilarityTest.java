package wdei.b1;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

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

        String left = "<html><h1></h1><p></p></html>";
        String right = "<html><h1></h1><p></p></html>";

        test(left, right);

        left = "<html>" +
                "<h1></h1>" +
                "  <div>" +
                "    <div>" +
                "      <p></p><span></span>" +
                "    </div>" +
                "    <div>" +
                "      <p></p><span></span>" +
                "    </div>" +
                "    <div>" +
                "      <p></p><span></span>" +
                "    </div>" +
                "    <div>" +
                "      <p></p><span></span>" +
                "    </div>" +
                "  </div>" +
                "</html>";
        right = "<html><h1></h1><p></p></html>";

        test(left, right);

    }

    private void test(String left, String right) {
        Document leftDoc = Jsoup.parse(left);
        Document rightDoc = Jsoup.parse(right);
        System.out.println("\n\n TEST");
        System.out.println("node matches: " + new TreeSimilarity().calculate(leftDoc, rightDoc));
    }

    @Test
    public void testWikiPages() throws IOException {
        String l = FileUtils.readFileToString(new File("src/test/resources/wikiFoo.html"));
        String r = FileUtils.readFileToString(new File("src/test/resources/wikiJaroWinkler.html"));
        test(l, r);
    }

}
