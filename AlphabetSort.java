import java.util.Scanner;
/**
 * Alphabitizes words based on alphabet given
 * @author P. Satamalee
 */

public class AlphabetSort extends Trie {
    
    /**
     * Main method of AlphabetSort that calls checks to see if arguments are corrects
     * then alphabetizes them
     * @param args unused.
     */
    public static void main(String[] args) {
        Trie t = new Trie();
        Scanner scanner = new Scanner(System.in); String alphabet = null;
        if (scanner.hasNext()) {
            alphabet = scanner.next();
            checkAlphabet(alphabet);
        } else {
            throw new IllegalArgumentException("No words or alphabet are given");
        }
        if (!scanner.hasNext()) {
            throw new IllegalArgumentException("No words or alphabet are given");
        }
        while (scanner.hasNext()) {
            t.insert(scanner.next());
        }
        scanner.close();
        alphabetize(alphabet, t, "");
    }

    /**
     * Calls other alphabetize function
     * @param alpha or the alphabet given
     * @param t Trie of all words given
     * @param word building a word from iterating through all ofthe alphabet
     */
    public static void alphabetize(String alpha, Trie t, String word) {
        alphabetize(alpha, t.getRoot(), word);
    }

    /**
     * Calls other alphabetize function
     * @param alpha or the alphabet given
     * @param n root Node of the Trie with all words
     * @param word building a word from iterating through all ofthe alphabet
     */
    private static void alphabetize(String alpha, Node n, String word) {
        String ogWord = word;
        for (int i = 0; i < alpha.length(); i += 1) {
            word = ogWord;
            char c = alpha.charAt(i);
            if (n.links[c] != null) {
                word += String.valueOf(c);
                if (n.links[c].exists) {
                    System.out.println(word);
                }
                alphabetize(alpha, n.links[c], word);
            }
        }
    }

    /**
     * Checks to see if alphabet contains each letter once
     * @param alpha or the alphabet given
     */
    public static void checkAlphabet(String alpha) {
        if ((alpha == null) || alpha.equals("")) {
            throw new IllegalArgumentException("No words or alphabet are given");
        }
        for (int i = 0; i < alpha.length(); i += 1) {
            char c = alpha.charAt(i);
            for (int j = i + 1; j < alpha.length(); j += 1) {
                char comp = alpha.charAt(j);
                if (c == comp) {
                    String x = "A letter appears  multiple times in the alphabet";
                    throw new IllegalArgumentException(x);
                }
            }
        }
    }
}
