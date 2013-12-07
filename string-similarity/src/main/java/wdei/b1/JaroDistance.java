package wdei.b1;

/**
 * Created with IntelliJ IDEA.
 * User: serprime
 * Date: 12/7/13
 * Time: 11:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class JaroDistance {

    public double calculate(String left, String right) {

        // prepare matrix of matching characters
        //printMatchingMatrix(left, right);

        Vars vars = calculateVars(left, right);
        double m = vars.matches;
        double t = vars.transpositions;

        double distance = 0;
        if (m > 0) {
            double ml = m / (double) left.length();
            double mr = m / (double) right.length();
            double mt = (m - t) / m;
            double mlrt = ml + mr + mt;
            distance = (1f / 3f) * mlrt;
        }
        System.out.println(String.format("Jaro distance (%s, %s): %f", left, right, distance));

        return distance;
    }

    private Vars calculateVars(String left, String right) {
        int totalMatches = 0;
        int totalTranspositions = 0;
        int matchingRadius = max(left.length(), right.length()) / 2 - 1;
        // check for each character in left if it matches a character inside the radius of right
        for (int l = 0; l < left.length(); l++) {
            int start = max(0, l - matchingRadius);
            int end = min(l + matchingRadius, right.length() - 1);
            // matches
            for (int r = start; r <= end; r++) {
                if (left.charAt(l) == right.charAt(r)) {
                    totalMatches++;
                    // we have to break if we find the first match,
                    // else we count one character
                    // multiple times and can get a distance greater 1
                    break;
                }
            }
            // transpositions
            if (left.charAt(l) == right.charAt(l)) {
                continue;
            }
            for (int r = start; r <= end; r++) {
                if (left.charAt(l) == right.charAt(r)) {
                    if (l != r) {
                        totalTranspositions++;
                        // here we have to break to only count 1 transposition per character.
                        break;
                    }
                }
            }
        }
        return new Vars(totalMatches, totalTranspositions);
    }

    public class Vars {
        public Vars(int mathes, int transpositions) {
            this.matches = mathes;
            this.transpositions = transpositions;
        }

        public int matches = 0;
        public int transpositions = 0;

        @Override
        public String toString() {
            return "Vars{" +
                    "matches=" + matches +
                    ", transpositions=" + transpositions +
                    '}';
        }
    }


    private int max(int left, int right) {
        return left > right ? left : right;
    }

    private int min(int left, int right) {
        return left < right ? left : right;
    }

    private void printMatchingMatrix(String left, String right) {
        boolean[][] matches = new boolean[left.length()][right.length()];
        for (int l = 0; l < left.length(); l++) {
            for (int r = 0; r < right.length(); r++) {
                matches[l][r] = left.charAt(l) == right.charAt(r);
            }
        }
        System.out.println("  " + right);
        int row = 0;
        for (boolean[] match : matches) {
            System.out.print(left.charAt(row) + " ");
            for (boolean b : match) {
                System.out.print(b ? 1 : 0);
            }
            row++;
            System.out.println("");
        }
    }
}
