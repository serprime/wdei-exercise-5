package wdei.b1;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TreeSimilarity {

    private JaroDistance jaroDistance = new JaroDistance();

    public double calculate(Document left, Document right) {
        Element leftRoot = left.select("html").iterator().next();
        StringBuilder l = new StringBuilder();
        getPath(leftRoot, l);

        Element rightRoot = right.select("html").iterator().next();
        StringBuilder r = new StringBuilder();
        getPath(rightRoot, r);

        return jaroDistance.calculate(l.toString(), r.toString());
    }

    private void getPath(Element tree, StringBuilder str) {
        str.append(tree.nodeName());
        for (Element element : tree.children()) {
            getPath(element, str);
        }
    }

}
