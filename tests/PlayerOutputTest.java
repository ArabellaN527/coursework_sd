import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;


public class PlayerOutputTest {

    private static final int ID = 14;
    private static final String Filename = fileFor(ID);

    private PlayerOutput output;
    private List<Card> hand;

    @Before
    public void setUp() {
        output = new PlayerOutput(ID);
        hand = makeHand(1, 2, 3, 4);
    }

    @After
    public void tearDown() {
        output.closePlayerOutput();
        new File(Filename).delete();
    }

    private static String fileFor(int ID) {
        return "Player" + ID + "_Output.txt";
    }

    private static List<Card> makeHand(int... values) {
        List<Card> cards = new LinkedList<>();
        for (int v : values) {
            cards.add(new Card(v));
        }
        return cards;
    }

    private static List<String> readFile(String name) throws IOException {
        return Files.readAllLines(Paths.get(name));
    }

    // Normal cases

    @Test
    public void constructorCreatesFile() {
        assertTrue("Output file should be created", new File(Filename).exists());
    }

    @Test 
    public void startLogWritesInitialHand() throws IOException {
        output.logTurn(5, 2, makeHand(1, 2, 5, 6), 2 ,1);

        assertEquals(Arrays.asList(
            "Player 100 Draws a 5 from right deck",
            "Player 100 discards a 2 to left deck",
            "Player 100 current hand", "1, 2, 5, 6 "), readFile(Filename));
    }

    @Test
    public void winnerLogsWinExitsAndFinalHand() throws IOException {
        output.logWinnerNotification(makeHand(14, 14, 14, 14), ID);

        assertEquals(Arrays.asList(
            "Player 14 wins",
            "Player 14 exits",
            "Player 14 final hand: ",
            "14, 14, 14, 14,"), readFile(Filename));
    } 

    @Test
    public void loserLogsWhoWonExitsAndFinalHand() throws IOException {
        output.logWinnerNotification(hand, 2);

        assertEquals(Arrays.asList(
            "Player 2 has informed player 14 that player 2 has won",
            "Player 14 exists",
            "Player 14's final hand: ",
            "1, 2, 3, 4"), readFile(Filename));
    }

    // Edge cases
    @Test 
    public void sameValueCardDrawnAndDiscarded() throws IOException {
        output.logTurn(5, 5, hand, 2, 1);

        List<String> lines = readFile(Filename);
        assertEquals("Player 14 draws a 5 from right deck", lines.get(0));
        assertEquals("Player 14 discards a 5 to left deck", lines.get(1));
    }

    @Test 
    public void playerIDIsZero() throws IOException {
        PlayerOutput zero = new PlayerOutput(0);
        try {
            zero.StartLog(hand);
            assertEquals("Player 0 initial hand", readFile(fileFor(0)).get(0));
        } finally {
            zero.closePlayerOutput();
            new File(fileFor(0)).delete();
        }
    }

    // Error Case
    @Test
    public void winnerNotificationDoesNotThrow() {
        output.logWinnerNotification(null, ID);
    }

    @Test
    public void HandInLogStillLogsDrawAndDiscard() throws IOException {
        output.logTurn(1, 2, null, 2, 1);
        output.closePlayerOutput();
        assertEquals(3, readFile(Filename).size());
    }
}
    