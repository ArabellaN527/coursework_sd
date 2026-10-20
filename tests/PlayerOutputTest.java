import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
        List<Card> cards = new ArrayList<>();
        for (int v : values) {
            cards.add(new Card(v));
        }
        return cards;
    }
    private static List<String> finishAndRead(PlayerOutput out, String fileName) throws IOException {
        out.closePlayerOutput();                       // flush everything to disk first
        List<String> lines = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get(fileName))) {
            lines.add(line.trim());
    }
        return lines;
}

    private List<String> readLines() throws IOException {
        return finishAndRead(output, Filename);
}
    

    // Normal cases

    @Test
    public void TestconstructorCreatesFile() {
        assertTrue("Output file should be created", new File(Filename).exists());
    }

    @Test 
    public void startLogWritesInitialHand() throws IOException {
        output.StartLog(makeHand(1, 2, 5, 6));

        assertEquals(Arrays.asList(
            "Player 14 initial hand",
            "1 2 5 6"),readLines());
    }

    @Test
    public void winnerLogsWinExitsAndFinalHand() throws IOException {
        output.logWinnerNotification(makeHand(14, 14, 14, 14), ID);

        assertEquals(Arrays.asList(
            "Player 14 wins",
            "Player 14 exits",
            "Player 14 final hand:",
            "14 14 14 14"), readLines());
    } 

    @Test
    public void loserLogsWhoWonExitsAndFinalHand() throws IOException {
        output.logWinnerNotification(hand, 2);

        assertEquals(Arrays.asList(
            "Player 2 has informed player 14 that player 2 has won",
            "Player 14 exits",
            "Player 14 final hand:",
            "1 2 3 4"), readLines());
    }

    // Edge cases
    @Test 
    public void sameValueCardDrawnAndDiscarded() throws IOException {
        output.logTurn(5, 5, hand, 2, 1);

        List<String> lines = readLines();
        assertEquals("Player 14 draws a 5 from deck 2", lines.get(0));
        assertEquals("Player 14 discards a 5 to deck 1", lines.get(1));
    }

    // Error Case
    @Test
    public void winnerNotificationDoesNotThrow() throws IOException {
        output.logWinnerNotification(null, ID);
         assertEquals(Arrays.asList(
                "Player 14 wins",
                "Player 14 exits",
                "Player 14 final hand:"), readLines());
    }

    @Test
    public void HandInLogStillLogsDrawAndDiscard() throws IOException {
        output.logTurn(1, 2, null, 2, 1);
        output.closePlayerOutput();
        assertEquals(3, readLines().size());
    }
}
    