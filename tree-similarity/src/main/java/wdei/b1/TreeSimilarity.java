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

        int rightChildrenSize = rightRoot.childNodeSize();
        int leftChildrenSize = leftRoot.childNodeSize();
        int[][] M = new int[leftChildrenSize][rightChildrenSize];

        Element[] leftElements = getChildren(leftRoot);
        Element[] rightElements = getChildren(rightRoot);

        for (int i = 1; i < leftChildrenSize; i++) {
            for (int j = 1; j < rightChildrenSize; j++) {
                M[i][j] = max(
                        M[i][j - 1],
                        M[i - 1][j],
                        M[i - 1][j - 1] + stm(leftElements[i], rightElements[j])
                );
            }
        }
        return M[leftChildrenSize-1][rightChildrenSize-1] + 1;
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
        Element[] children = new Element[element.childNodeSize()];
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
