/**
 * Prefix-Trie. Supports linear time find() and insert(). 
 * Should support determining whether a word is a full word in the 
 * Trie or a prefix.
 * @author P. Satamalee
 */
public class Trie {

    private Node root = new Node();
    private final int N = 255;

    /**
     * Basic Constructor of a Node containts links Node array and exists boolean
     */
    public class Node {
        boolean exists;
        Node[] links;
        /**
         * Basic Constructor of a Node containts links Node array and exists boolean
         */
        public Node() {
            links = new Node[N];
            exists = false;
        }
    }

    /**
     * Allows access to private variable root
     * @return the root node
     */
    public Node getRoot() {
        return root;
    }

    /**
     * returns boolean if string exists or conditions satisfid
     * @param s word given
     * @param isFullWord condition if the user wants full word or not
     * @return returns boolean whether conditions found or met
     */
    public boolean find(String s, boolean isFullWord) {
        String x = "A null or empty string is added.";
        x +=  "Null and empty strings by definition are never in the Trie";
        if (s == null || s.equals("")) {
            throw new IllegalArgumentException(x);
        }
        return get(root, s, isFullWord, 0) != null;
    }

    /**
     * returns the Node for a certain word
     * @param x node we are searching in
     * @param s word we are searching for
     * @param ifw boolean condition for searching
     * @param d how far into the word(s) we are looking at 
     * @return node that s gottn
     */
    private Node get(Node x, String s, boolean ifw, int d) {
        if (x == null) {
            return null;
        }

        if (!ifw && d == s.length()) {
            return new Node();
        }

        if ((d == s.length()) && !x.exists) {
            return null;
        }

        if ((d == s.length()) && x.exists) {
            return new Node();
        }

        char c = s.charAt(d);
        if (x.links[c] != null) {
            return get(x.links[c], s, ifw, d + 1);
        } else {
            return null;
        }
    }

    /**
     * Places a word into the Trie
     * @param s word to be inserted
     */
    public void insert(String s) {
        if (s == null || s.equals("")) {
            throw new IllegalArgumentException();
        }
        put(root, s, 0);
    }

    /**
     * Actually does the insert action
     * @param x node being inserted to
     * @param s word being inserted
     * @param d how far into word we are inserting
     * @return returns the node that is placed last
     */
    private Node put(Node x, String s, int d) {
        if (x == null) {
            x = new Node();
        }

        if (d == s.length()) {
            x.exists = true;
            return x;
        }

        char c = s.charAt(d);
        x.links[c] = put(x.links[c], s, d + 1);
        return x;
    }

    /*
    public static void main(String[] args) {
        Trie t = new Trie();
        t.insert("hello");
        t.insert("hey");
        t.insert("goodbye");
        System.out.println(t.find("hell", false));
        System.out.println(t.find("hello", true));
        System.out.println(t.find("good", false));
        System.out.println(t.find("bye", false));
        System.out.println(t.find("heyy", false));
        System.out.println(t.find("hell", true));   
    }
    */
}
