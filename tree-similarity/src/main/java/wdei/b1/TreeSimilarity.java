package wdei.b1;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created with IntelliJ IDEA.
 * User: serprime
 * Date: 05.12.13
 * Time: 19:34
 * To change this template use File | Settings | File Templates.
 */
public class TreeSimilarity {

    public double calculate(Document left, Document right) {
        Element leftRoot = left.select("html").iterator().next();
        Element rightRoot = right.select("html").iterator().next();

        return stm(leftRoot, rightRoot);
    }

    private int stm(Element leftRoot, Element rightRoot) {
        if (!same(leftRoot, rightRoot)) {
            return 0;
        }


        Element[] leftChildren = getChildren(leftRoot);
        Element[] rightChildren = getChildren(rightRoot);
        int[][] M = new int[leftChildren.length + 1][rightChildren.length + 1];

        for (int i = 1; i <= leftChildren.length; i++) {
            for (int j = 1; j <= rightChildren.length; j++) {
                int a = M[i][j - 1];
                int b = M[i - 1][j];
                int c = M[i - 1][j - 1];
                int z = stm(leftChildren[i-1], rightChildren[j-1]);
                M[i][j] = max(
                        a,
                        b,
                        c + z
                );
            }
        }
        return M[leftChildren.length][rightChildren.length] + 1;
    }

    private int max(int... vals) {
        int max = Integer.MIN_VALUE;
        for (int val : vals) {
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

    // convert Elements to an array
    private Element[] getChildren(Element element) {
        Element[] children = new Element[element.children().size()];
        int i = 0;
        for (Element child : element.children()) {
            children[i] = child;
            i++;
        }
        return children;
    }

    private boolean same(Element left, Element right) {
        System.out.println(left.nodeName() + ":" + right.nodeName() + " = " + left.nodeName().equals(right.nodeName()));
        return left.nodeName().equals(right.nodeName());
    }

}
