package wdei.b1;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Stack;

public class TreeSimilarity {

    private JaroDistance jaroDistance = new JaroDistance();


    HashMap<String, String> tagCodes = new HashMap<String, String>();
    private Stack<String> codes = new Stack<String>();

    public TreeSimilarity() {
        // !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}

        for (int i = 122; i>=97; i--) {
            codes.push("" + (char) i);
        }
        for (int i = 90; i>=65; i--) {
            codes.push("" + (char) i);
        }
        for (int i = 57; i>=48; i--) {
            codes.push("" + (char) i);
        }
    }

    public double calculate(Document left, Document right) {
        Element leftRoot = left.select("html").iterator().next();
        StringBuilder l = new StringBuilder();
        getPath(leftRoot, l);

        Element rightRoot = right.select("html").iterator().next();
        StringBuilder r = new StringBuilder();
        getPath(rightRoot, r);
        System.out.println("compare:");
        System.out.println(" > " + l.toString());
        System.out.println(" > " + r.toString());
        double res = jaroDistance.calculate(l.toString(), r.toString());
        System.out.println(" = " + res);
        return res;
    }

    private void getPath(Element tree, StringBuilder str) {
        str.append(getCode(tree.nodeName()));
        for (Element element : tree.children()) {
            getPath(element, str);
        }
    }

    private String getCode(String tagName) {
        String code = tagCodes.get(tagName);
        if (code == null) {
            code = codes.pop();
            tagCodes.put(tagName, code);
        }
        return code;
    }

}
