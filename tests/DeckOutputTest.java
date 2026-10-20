import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DeckOutputTest {
 
    private final List<DeckOutput> opened = new ArrayList<>();
 
    @After
    public void tearDown() {
        for (DeckOutput out : opened) {
            out.closeDeckOutput();
        }
        for (int id = 100; id <= 104; id++) {
            new File(fileFor(id)).delete();
        }
    }

    private static String fileFor(int id) {
        return "deck" + id + "_Output.txt";
    }

    private static CardDeck makeDeck(int id, int... values) {
        LinkedList<Card> cards = new LinkedList<>();
        for (int v : values) {
            cards.add(new Card(v));
        }
        return new CardDeck(id, cards);
    }

     private DeckOutput open(CardDeck deck) {
        DeckOutput out = new DeckOutput(deck);
        opened.add(out);
        return out;
    }
 
    private static List<String> readFile(int id) throws IOException {
        return Files.readAllLines(Paths.get(fileFor(id)));
    }

    // Normal cases

    @Test
    public void constructorCreatesFileNamedAfterDeck() {
        open(makeDeck(15, 1, 2, 3, 4));
        assertTrue(new File(fileFor(15)).exists());
    }

     @Test
    public void contentsKeepDeckOrder() throws IOException {
        open(makeDeck(15, 9, 3, 7, 1));
        assertEquals("9 3 7 1 ", readFile(15).get(1));
    }

    // Edge case

    @Test
    public void emptyDeckPrintsBlankLine() throws IOException {
        open(makeDeck(15));
        assertEquals(Arrays.asList("Deck15 final contents: ", ""), readFile(15));
    }

    // Error cases

    @Test
    public void error_closingTwiceDoesNotThrow() {
        DeckOutput out = open(makeDeck(15, 1, 2, 3, 4));
        out.closeDeckOutput();
        out.closeDeckOutput();
    }
 
    @Test
    public void error_printDeckAfterCloseWritesNothing() throws IOException {
        CardDeck deck = makeDeck(15, 1, 2, 3, 4);
        DeckOutput out = open(deck);
        out.closeDeckOutput();
 
        out.printDeck(deck);                    // must not throw
 
        assertEquals(2, readFile(15).size());
    }

}
