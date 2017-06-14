import org.junit.Test;
import static org.junit.Assert.*;

public class AlphabetSortTest extends AlphabetSort {

    @Test
    public void testAlphabetNormal() {
    	Trie t = new Trie();
    	String a = "abcdefghijklmnopqrstuvwxyz";
    	checkAlphabet(a);
    	t.insert("banana");
    	t.insert("pear");
    	t.insert("apple");
    	t.insert("wug");
    	System.out.println("Print Normal:");
		alphabetize(a, t, "");
		System.out.println();
    }

    @Test
    public void testAlphabetReverse() {
    	Trie t = new Trie();
    	String a = "zyxwvutsrqponmlkjihgfedcba";
    	checkAlphabet(a);
    	t.insert("banana");
    	t.insert("pear");
    	t.insert("apple");
    	t.insert("wug");
    	System.out.println("Print Reverse:");
		alphabetize(a, t, "");
		System.out.println();
    }

    @Test
    public void testThrows() {
    	String a = "zyxwvutsrqponmlkjihgfedzba";
    	System.out.println("Print alphabet error:");
    	try {
    		checkAlphabet(a);
    	} catch (IllegalArgumentException e) {
    		System.out.println(e);
    	}
    	System.out.println();
    }

    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(AlphabetSortTest.class);
    }

}