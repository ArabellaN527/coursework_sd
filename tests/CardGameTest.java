import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class CardGameTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private final ByteArrayOutputStream console = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() throws Exception {
        CardGame.GameOver.set(false);
        AtomicInteger winner = getStatic("WinningPlayerID");
        winner.set(0);
        System.setOut(new PrintStream(console));
    }

    @After
    public void tearDown() {
        CardGame.GameOver.set(true); // stops threads still running
        System.setOut(originalOut);
        for (int i=1; i <= 3; i++) {
            new File("Player" + i + "_Output.txt").delete();
        }
    }

    private String pack(int... values) throws IOException {
        File f = tmp.newFile();
        List<String> lines = new LinkedList<>();
        for (int v : values) {
            lines.add(String.valueOf(v));
        }
        Files.write(f.toPath(), lines);
        return f.getPath();
    }

    private static List<Integer> deckValues(CardDeck deck) throws Exception {
        for (Field f : CardDeck.class.getDeclaredFields()) {
            if (Collection.class.isAssignableFrom(f.getType())) {
                f.setAccessible(true);
                synchronized (deck) {
                    return ((Collection<Card>) f.get(deck)).stream()
                            .map(Card::getCardValue).collect(Collectors.toList());
                }
            }
        }
        throw new AssertionError("CardDeck has no collection field");
    }

    private static <T> T getStatic(String name) throws Exception {
        Field f = CardGame.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(null);
    }

    private List<Thread> runGame() throws Exception {
        List<Player> players = getStatic("Players");
        List<Thread> threads = new ArrayList<>();
        for (Player p : players) {
            threads.add(new Thread(p));
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join(5000);
        }
        return threads;
    }

    // Normal cases

    @Test
    public void handsDealtRoundRobin() throws Exception {
        CardGame.initialiseGame(pack(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16), 2);
 
        List<List<Card>> hands = getStatic("PlayerHands");
        assertEquals(Arrays.asList(1, 3, 5, 7),
                hands.get(0).stream().map(Card::getCardValue).collect(Collectors.toList()));
        assertEquals(Arrays.asList(2, 4, 6, 8),
                hands.get(1).stream().map(Card::getCardValue).collect(Collectors.toList()));
    }

    @Test
    public void decksDealtRoundRobinAfterHands() throws Exception {
        CardGame.initialiseGame(pack(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16), 2); // set for 2 players
 
        List<CardDeck> decks = getStatic("gameDecks");
        assertEquals(Arrays.asList(9, 11, 13, 15), deckValues(decks.get(0)));
        assertEquals(Arrays.asList(10, 12, 14, 16), deckValues(decks.get(1)));
    }

    // Edge cases

    @Test
    public void playerIsSinglePlayer() throws Exception {
        CardGame.initialiseGame(pack(1, 1, 1, 1, 2, 2, 2, 2), 1);
 
        List<Player> players = getStatic("Players");
        List<List<Card>> hands = getStatic("PlayerHands");
        List<CardDeck> decks = getStatic("gameDecks");
        assertEquals(1, players.size());
        assertEquals(4, hands.get(0).size());
        assertEquals(4, deckValues(decks.get(0)).size());
    }

    @Test
    public void edge_reinitialisingClearsThePreviousGame() throws Exception {
        CardGame.initialiseGame(pack(new int[24]), 3);
        CardGame.initialiseGame(pack(new int[16]), 2);
 
        List<Player> players = getStatic("Players");
        List<CardDeck> decks = getStatic("gameDecks");
        List<List<Card>> hands = getStatic("PlayerHands");
        assertEquals(2, players.size());
        assertEquals(2, decks.size());
        assertEquals(2, hands.size());
    }

    // Error case

    @Test
    public void zeroPlayersCreatesNoPlayers() throws Exception {
        CardGame.initialiseGame(pack(), 0);
 
        List<Player> players = getStatic("Players");
        assertTrue(players.isEmpty());
    }

    @Test
    public void tooFewCardsCreatesNoPlayers() throws Exception {
        CardGame.initialiseGame(pack(new int[15]), 2);
 
        List<Player> players = getStatic("Players");
        assertTrue(players.isEmpty());
        assertTrue(console.toString().contains("do not match"));
    }
 
    @Test
    public void tooManyCardsCreatesNoPlayers() throws Exception {
        CardGame.initialiseGame(pack(new int[17]), 2);
 
        List<Player> players = getStatic("Players");
        assertTrue(players.isEmpty());
        assertTrue(console.toString().contains("do not match"));
    }

    // Concurrency tests
    @Test
    public void gameEndsWithCorrectWinnerAndAllThreadsStop() throws Exception {
        CardGame.initialiseGame(pack(1, 2, 1, 2, 1, 2, 5, 6,  1, 8, 7, 8, 7, 8, 7, 8), 2);
 
        List<Thread> threads = runGame();
 
        for (Thread t : threads) {
            assertFalse("A player thread is still running", t.isAlive());
        }
        assertTrue(CardGame.GameOver.get());
        AtomicInteger winner = getStatic("WinningPlayerID");
        assertEquals(1, winner.get());
    }

    @Test
    public void cardsAreConservedAndHandsStayAtFour() throws Exception {
        CardGame.initialiseGame(pack(1, 2, 1, 2, 1, 2, 5, 6,  1, 8, 7, 8, 7, 8, 7, 8), 2);
 
        runGame();
 
        List<List<Card>> hands = getStatic("PlayerHands");
        List<CardDeck> decks = getStatic("gameDecks");
        int total = 0;
        for (List<Card> hand : hands) {
            assertEquals("Every hand should hold 4 cards after the game", 4, hand.size());
            total += hand.size();
        }
        for (CardDeck deck : decks) {
            total += deckValues(deck).size();
        }
        assertEquals("No cards created or lost (8n)", 16, total);
    }

}