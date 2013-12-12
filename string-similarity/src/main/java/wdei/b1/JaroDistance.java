package wdei.b1;

import java.util.Arrays;

public class JaroDistance {
	
    public double calculate(String left, String right) {

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
        //System.out.println(String.format("Jaro distance (%s, %s): %f", left, right, distance));

        return distance;
    }

    private Vars calculateVars(String left, String right) {
        int totalMatches = 0;
        int totalTranspositions = 0;
        int matchingRadius = max(left.length(), right.length()) / 2 - 1;


        // --
        boolean[] leftMatches = new boolean[left.length()];
        Arrays.fill(leftMatches, false);
        boolean[] rightMatches = new boolean[right.length()];
        Arrays.fill(rightMatches, false);
        // --

        // check for each character in left if it matches a character inside the radius of right
        for (int l = 0; l < left.length(); l++) {
            int start = max(0, l - matchingRadius);
            int end = min(l + matchingRadius, right.length() - 1);
            // matches
            for (int r = start; r <= end; r++) {
                // we need to skip already matched chars
                if (rightMatches[r]) {
                    continue;
                }
                if (left.charAt(l) == right.charAt(r)) {
                    totalMatches++;
                    leftMatches[l] = true;
                    rightMatches[r] = true;
                    // we have to break if we find the first match,
                    // else we count one character
                    // multiple times and can get a distance greater 1
                    break;
                }
            }
        }
        // calculate transpositions
        int halfTranspositions = 0;
        int r = 0;
        for (int l = 0; l < leftMatches.length; l++) {
            if (!leftMatches[l]) {
                continue;
            }
            while (!rightMatches[r]) {
                r++;
            }
            if (left.charAt(l) != right.charAt(r)) {
                halfTranspositions++;
            }
            r++;
        }
        return new Vars(totalMatches, halfTranspositions/2);
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
    


}
