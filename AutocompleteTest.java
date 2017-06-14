import org.junit.Test;
import static org.junit.Assert.*;

public class AutocompleteTest {

    @Test
    public void testBasics() {
        String[] terms = {"wug", "hello", "toast", "toastada"};
        double[] weights = {1,2,3,4};
        Autocomplete ac = new Autocomplete(terms, weights);
        assertEquals(4, ac.weightOf("toastada"), 0.1);
        assertEquals(3, ac.weightOf("toast"), 0.1);
        assertEquals(2, ac.weightOf("hello"), 0.1);
        assertEquals(1, ac.weightOf("wug"), 0.1);
        assertEquals(0, ac.weightOf("nothing"), 0.1);
        assertEquals("toastada", ac.topMatch("to"));
        assertEquals("hello", ac.topMatch("h"));
        assertEquals("wug", ac.topMatch("w"));
        for (String term : ac.topMatches("to", 5)) {
            StdOut.printf("%14.1f  %s\n", ac.weightOf(term), term);
        }
        System.out.println("Should Print:");
        System.out.println("4.0 toastada");
        System.out.println("3.0 toast");
        System.out.println();
        assertEquals("toastada", ac.topMatch(""));
        for (String term2 : ac.spellCheck("topst", 1, 5)) {
            StdOut.printf("%14.1f  %s\n", ac.weightOf(term2), term2);
        }
        System.out.println("Should Print:");
        System.out.println("3.0 toast");
        System.out.println();

    }



    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(AutocompleteTest.class);
    }

}