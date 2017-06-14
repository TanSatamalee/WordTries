import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Iterator;

/**
 * Implements autocomplete on prefixes for a given dictionary of terms and weights.
 * @author P. Satamalee
 */
public class Autocomplete {

    private Node root = null;
    /**
     * Initializes required data structures from parallel arrays.
     * @param terms Array of terms.
     * @param weights Array of weights.
     */
    public Autocomplete(String[] terms, double[] weights) {

        /* Builds main data structure
        */
        if (terms.length != weights.length) {
            String error1 = "The length of the terms and weights arrays are different";
            throw new IllegalArgumentException(error1);
        }
        for (int i = 0; i < terms.length; i += 1) {
            String word = terms[i];
            if (word == null || word.equals("")) {
                throw new IllegalArgumentException();
            }
            if (weights[i] < 0) {
                throw new IllegalArgumentException("There are negative weights");
            }
            root = place(root, word, 0, weights[i]);
        }

    }

    private final int N = 128;

    /**
     * Basic Constructor of a Node containts links Node array and exists boolean
     */
    private class Node {
        boolean exists;
        double weight;
        double highest;
        char letter;
        String word;
        Node left;
        Node mid;
        Node right;
        String wordBuilder;

        /**
         * Basic Constructor of a Node containts links Node array and exists boolean
         * @param c letter of the node
         */
        public Node(char c) {
            exists = false;
            letter = c;
            weight = 0;
            highest = 0;
            word = "";
            left = null;
            right = null;
            mid = null;
            wordBuilder = String.valueOf(c);
        }
    }

    /**
     * Inspired from: http://algs4.cs.princeton.edu/52trie/TST.java.html
     * Inserts word into a node
     * @param x node being inserted to
     * @param s word being inserted
     * @param d how far into word we are inserting
     * @param value of weight of word
     * @return returns the node that is placed last
     */
    private Node place(Node x, String s, int d, double value) {
        if (x == null) {
            x = new Node(s.charAt(d));
        }
        double temp = 0;
        char c = s.charAt(d);
        if (c < x.letter) {
            if (x.left == null) {
                x.left = new Node(c);
            }
            x.left.wordBuilder = x.wordBuilder.substring(0, x.wordBuilder.length() - 1)
                + String.valueOf(x.left.letter);
            x.left = place(x.left, s, d, value);
            temp = x.left.highest;
        } else if (c > x.letter) {
            if (x.right == null) {
                x.right = new Node(c);
            }
            x.right.wordBuilder = x.wordBuilder.substring(0, x.wordBuilder.length() - 1)
                + String.valueOf(x.right.letter);
            x.right = place(x.right, s, d, value);
            temp = x.right.highest;
        } else if (d < s.length() - 1) {
            if (x.mid == null) {
                x.mid = new Node(s.charAt(d + 1));
            }
            x.mid.wordBuilder = x.wordBuilder + String.valueOf(x.mid.letter);
            x.mid = place(x.mid, s, d + 1, value);
            temp = x.mid.highest;
        } else {
            if (x.exists) {
                throw new IllegalArgumentException("There are duplicate input terms");
            }
            x.exists = true;
            x.weight = value;
            if (x.highest < value) {
                x.highest = value;
            }
            x.word = s;
        }

        if (x.highest < temp) {
            x.highest = temp;
        }
        return x;
    }

    /**
     * Inspired from: http://algs4.cs.princeton.edu/52trie/TST.java.html
     * returns the Node for a certain word
     * @param x node we are searching in
     * @param s word we are searching for
     * @param ifw boolean condition for searching
     * @param d how far into the word(s) we are looking at 
     * @return node that ends with s
     */
    private Node find(Node x, String s, boolean ifw, int d) {
        if (x == null) {
            return null;
        }

        char c = s.charAt(d);
        if (c < x.letter) {
            return find(x.left, s, ifw, d);
        } else if (c > x.letter) {
            return find(x.right, s, ifw, d);
        } else if (d < s.length() - 1) {
            return find(x.mid, s, ifw, d + 1);
        } else {
            if (!ifw) {
                return x;
            }
            if (!x.exists) {
                return null;
            } else {
                return x;
            }
        }
    }

    /**
     * Find n words that have that prefix with the most weights and autocorrects
     * @param prefix given
     * @param n number of words wanted
     * @param isCorrect boolean for looking for TopMatches or SpellCheck
     * @param dist how much error for autocorrect functions
     * @return String array of the n number of terms
     */
    private String[] findTheTop(String prefix, int n, boolean isCorrect, int dist) {
        int nTemp = n;
        if (n == 1) {
            n += 1;
        }
        PriorityQueue<Node> answer = 
            new PriorityQueue<Node>(n, (Comparator) new SortByPriority());
        TreeMap<String, Double> results = new TreeMap<String, Double>();
        if (prefix.equals("")) {
            if (!isCorrect) {
                findTopMatches(root, n, answer, results);
            } else {
                autoCorrect(root, n, answer, results, prefix, dist);
            }
        } else {
            if (!isCorrect) {
                Node base = find(root, prefix, false, 0);
                if ((base != null)) {
                    if (base.mid != null) {
                        findTopMatches(base.mid, n, answer, results);
                    } 
                    if (base.exists) {
                        results.put(base.word, base.weight);
                    }
                }
            } else {
                autoCorrect(root, n, answer, results, prefix, dist);
            }
        }
        n = nTemp;
        if (results.equals(new TreeMap<String, Double>())) {
            return null;
        }
        Set<String> temp = results.keySet();
        Iterator<String> temp2 = temp.iterator();
        String[] answer2 = new String[n]; Double[] values = new Double[n];
        for (int i = 0; i < n; i += 1) {
            values[i] = 0.0;
        }
        while (temp2.hasNext()) {
            String word = temp2.next();
            Double weight = results.get(word);
            for (int j = 0; j < n; j += 1) {
                if (weight > values[j]) {
                    for (int k = n - 1; k > j; k -= 1) {
                        answer2[k] = answer2[k - 1];
                        values[k] = values[k - 1];
                    }
                    answer2[j] = word;
                    values[j] = weight;
                    j = n;
                }
            }
        }
        return answer2;
    }

    /**
     * Find n words that have that prefix with the most weights
     * @param base the node that we are currently analyzing
     * @param n the number of words wanted
     * @param answer PriorityQueue contains Nodes ordered having high to low weights
     * @param results TreeMap contains the topMatch words as combing through TST
     */
    private void findTopMatches(Node base, int n, PriorityQueue<Node> answer,
        TreeMap<String, Double> results) {
        if (base != null) {
            if (base.left != null) {
                answer.add(base.left);
            }
            if (base.right != null) {
                answer.add(base.right);
            }
            if (base.mid != null) {
                answer.add(base.mid);
            }
            if ((base.exists) && ((answer.peek() == null) 
                || (base.weight >= answer.peek().highest))) {
                results.put(base.word, base.weight);
            }
            if (results.size() < n) {
                findTopMatches(answer.poll(), n, answer, results);
            } else {
                return;
            }
            if ((base.exists) && ((answer.peek() == null) 
                || (base.weight >= answer.peek().highest)) 
                && !results.containsKey(base.word)) {
                results.put(base.word, base.weight);
            }
        }
    }

    /**
     * Find n words that have dist differences from the actualWord
     *  This method needs debugging but still returns some words (some top corrections)
     * @param base the node that we are currently analyzing
     * @param n the number of words wanted
     * @param answer PriorityQueue contains Nodes ordered having high to low weights
     * @param results TreeMap contains the topMatch words as combing through TST
     * @param actualWord the error word we are trying to spellCheck
     * @param dist the correction amount
     */
    private void autoCorrect(Node base, int n, PriorityQueue<Node> answer,
        TreeMap<String, Double> results, String actualWord, int dist) {
        if (base != null) {
            if (base.left != null) {
                if (!answer.contains(base.left)) {
                    answer.add(base.left);
                }
            }
            if (base.right != null) {
                if (!answer.contains(base.right)) {
                    answer.add(base.right);
                }
            }
            if (base.mid != null) {
                if (base.mid.right != null) {
                    String temporary = actualWord; String temp = base.mid.right.wordBuilder;
                    if (temp.length() < actualWord.length()) {
                        temporary = actualWord.substring(0, temp.length());
                    } else {
                        temp = temp.substring(0, actualWord.length());
                    }
                    if ((levenDist(temp, temporary) <= dist)
                        || (levenDist(temp, temporary.substring
                            (0, temporary.length() - 1)) <= dist)) {
                        answer.add(base.mid.right);
                    }
                }
                if (base.mid.left != null) {
                    String temporary = actualWord;
                    String temp = base.mid.left.wordBuilder;
                    if (temp.length() < actualWord.length()) {
                        temporary = actualWord.substring(0, temp.length());
                    } else {
                        temp = temp.substring(0, actualWord.length());
                    }
                    if ((levenDist(temp, temporary) <= dist)
                        || (levenDist(temp, temporary.substring(0, temporary.length() - 1))
                        <= dist)) {
                        answer.add(base.mid.left);
                    }
                }
                if (base.mid.mid != null) {
                    String temporary = actualWord;
                    String temp = base.mid.mid.wordBuilder;
                    if (temp.length() < actualWord.length()) {
                        temporary = actualWord.substring(0, temp.length());
                    } else {
                        temp = temp.substring(0, actualWord.length());
                    }
                    if ((levenDist(temp, temporary) <= dist)
                        || (levenDist(temp, temporary.substring
                            (0, temporary.length() - 1)) <= dist)) {
                        answer.add(base.mid.mid);
                    }
                }
                String temporary = actualWord; String temp = base.mid.wordBuilder;
                if (temp.length() < actualWord.length()) {
                    temporary = actualWord.substring(0, temp.length());
                } else {
                    temp = temp.substring(0, actualWord.length());
                }
                if ((levenDist(temp, temporary) <= dist)
                    || (levenDist(temp, temporary.substring(0, temporary.length() - 1)) <= dist)) {
                    if (!answer.contains(base.mid)) {
                        answer.add(base.mid);
                    }
                }
            }
            if ((base.exists) && (levenDist(base.word, actualWord) <= dist)) {
                results.put(base.word, base.weight);
            }
            if (results.size() < n) {
                autoCorrect(answer.poll(), n, answer, results, actualWord, dist);
            } else {
                return;
            }
            if ((base.exists) && (levenDist(base.word, actualWord) <= dist)) {
                results.put(base.word, base.weight);
            }
        }
    }

    /** 
     * Adopted from Wikipedia:
     * http://en.wikipedia.org/wiki/Levenshtein_distance#Iterative_with_two_matrix_rows
     * @param s word number 1
     * @param t word number 2
     * @return int of how much distance words differ
     */
    private int levenDist(String s, String t) {
        // degenerate cases
        if (s == t) {
            return 0;
        }
        if (s.length() == 0) {
            return t.length();
        }
        if (t.length() == 0) {
            return s.length();
        }
     
        // create two work vectors of integer distances
        int[] v0 = new int[t.length() + 1];
        int[] v1 = new int[t.length() + 1];
     
        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        for (int i = 0; i < v0.length; i++) {
            v0[i] = i;
        }
        for (int i = 0; i < s.length(); i++) {
            // calculate v1 (current row distances) from the previous row v0
     
            // first element of v1 is A[i+1][0]
            //   edit distance is delete (i+1) chars from s to match empty t
            v1[0] = i + 1;
     
            // use formula to fill in the rest of the row
            for (int j = 0; j < t.length(); j++) {
                int cost;
                if (s.charAt(i) == t.charAt(j)) {
                    cost = 0;
                } else {
                    cost = 1;
                }
                v1[j + 1] = Math.min(v1[j] + 1, Math.min(v0[j + 1] + 1, v0[j] + cost));
            }
     
            // copy v1 (current row) to v0 (previous row) for next iteration
            for (int j = 0; j < v0.length; j++) {
                v0[j] = v1[j];
            }
        }
     
        return v1[t.length()];
    }

    /**
     * Find n words that have that prefix with the most weights
     */
    private class SortByPriority implements Comparator<Node> {
        @Override
        public int compare(Node n2, Node n1) {
            return Double.compare(n1.highest, n2.highest);
        }
    }

    /**
     * Find the weight of a given term. If it is not in the dictionary, return 0.0
     * @param term word given
     * @return the weight of that term
     */
    public double weightOf(String term) {
        String x = "A null or empty string is added. ";
        x +=  "Null and empty strings by definition are never in the Trie";
        if (term == null || term.equals("")) {
            throw new IllegalArgumentException(x);
        }
        Node answer22 = find(root, term, true, 0);
        if (answer22 != null) {
            return answer22.weight;
        } else {
            return 0.0;
        }
    }

    /**
     * Return the top match for given prefix, or null if there is no matching term.
     * @param prefix Input prefix to match against.
     * @return Best (highest weight) matching string in the dictionary.
     */
    public String topMatch(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        String[] answerTemp = findTheTop(prefix, 2, false, 0);
        if (answerTemp != null) {
            return answerTemp[0];
        } else {
            return "";
        }
    }

    /**
     * Returns the top k matching terms (in descending order of weight) as an iterable.
     * If there are less than k matches, return all the matching terms.
     * @param prefix the word given to find top matches
     * @param k the number of matches wanted
     * @return an iterable of k words of highest weight with prefix
     */
    public Iterable<String> topMatches(String prefix, int k) {
        if (prefix == null) {
            throw new IllegalArgumentException("Must input prefix");
        }
        if (k <= 0) {
            String xx = "Trying to find the k top matches for non-positive k";
            throw new IllegalArgumentException(xx);
        }
        String[] result = findTheTop(prefix, k, false, 0);
        if (result == null) {
            return new LinkedHashSet<String>();
        }
        LinkedHashSet<String> answer11 = new LinkedHashSet<String>();
        for (int i = 0; i < result.length; i += 1) {
            if (result[i] != null && !result[i].equals("")) {
                answer11.add(result[i]);
            }
        }
        return answer11;
    }

    /**
     * Returns the highest weighted matches within k edit distance of the word.
     * If the word is in the dictionary, then return an empty list.
     * @param word The word to spell-check
     * @param dist Maximum edit distance to search
     * @param k    Number of results to return 
     * @return Iterable in descending weight order of the matches
     */
    public Iterable<String> spellCheck(String word, int dist, int k) {
        if (word == null || word.equals("")) {
            throw new IllegalArgumentException("Must input word");
        }
        if (dist <= 0) {
            String xx = "Trying to find the dist top matches for non-positive dist";
            throw new IllegalArgumentException(xx);
        }
        if (k <= 0) {
            String xx1 = "Trying to find the k top matches for non-positive k";
            throw new IllegalArgumentException(xx1);
        }
        String[] result = findTheTop(word, k, true, dist);
        if (result == null) {
            return new LinkedList<String>();
        }
        LinkedList<String> results11 = new LinkedList<String>();  
        for (int i = 0; i < result.length; i += 1) {
            if (result[i] != null && !result[i].equals("")) {
                results11.add(result[i]);
            }
        }
        return results11;
    }
    /**
     * Test client. Reads the data from the file, 
     * then repeatedly reads autocomplete queries from standard input
     * and prints out the top k matching terms.
     * @param args takes the name of an input file and an integer k as
     * command-line arguments
     */
    public static void main(String[] args) {
        // initialize autocomplete data structure
        In in = new In(args[0]);
        int N = in.readInt();
        String[] terms = new String[N];
        double[] weights = new double[N];
        for (int i = 0; i < N; i++) {
            weights[i] = in.readDouble();   // read the next weight
            in.readChar();                  // scan past the tab
            terms[i] = in.readLine();       // read the next term
        }

        Autocomplete autocomplete = new Autocomplete(terms, weights);

        // process queries from standard input
        int k = Integer.parseInt(args[1]);
        if (args.length == 2) {
            while (StdIn.hasNextLine()) {
                String prefix = StdIn.readLine();
                for (String term : autocomplete.topMatches(prefix, k)) {
                    StdOut.printf("%14.1f  %s\n", autocomplete.weightOf(term), term);
                }
            }
        } else {
            int dist = Integer.parseInt(args[2]);
            while (StdIn.hasNextLine()) {
                String prefix = StdIn.readLine();
                for (String term : autocomplete.spellCheck(prefix, dist, k)) {
                    StdOut.printf("%14.1f  %s\n", autocomplete.weightOf(term), term);
                }
            }
        }
    }
}
