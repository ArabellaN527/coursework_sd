import static org.junit.Assert.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

import org.junit.Before;
import org.junit.Test;

public class PlayerTest {

    private CardDeck left;
    private CardDeck right;
    private AtomicInteger WinningPlayerID;
    private Player player;
    private AtomicBoolean GameOver;
    private List<Card> hand;

    @Before
    public void setUp() {
        left = new CardDeck(1, new LinkedList<Card>());
        right = new CardDeck(2,new LinkedList<Card>());
        GameOver = new AtomicBoolean(false);
        WinningPlayerID = new AtomicInteger(0); // 0 = no winner ID yet (players are 1-4)
        hand = new ArrayList<Card>();
        player = new Player(1, left, right, GameOver, hand, WinningPlayerID, new PlayerOutput(1));
    }

    private void deal(int a, int b, int c, int d) {
        hand.add(new Card(a));
        hand.add(new Card(b));
        hand.add(new Card(c));
        hand.add(new Card(d)); // sets up hand with 4 cards
    }
    private void runPlayer() throws InterruptedException{
        Thread t = new Thread(player);
        t.start();
        t.join(2000);
    }

    private void runFor(long ms) throws InterruptedException {
        Thread t = new Thread(player);//stops test threads that wont reach win conditions from running forever 
        t.start();
        Thread.sleep(ms);
        GameOver.compareAndSet(false, true);   // end the loop; nobody won
        t.join(2000);
        assertFalse("player thread still running", t.isAlive());
    }

    @Test
    public void getPlayerIDReturnsID() {
        assertEquals(1, player.getPlayerID());
    }


    @Test
    public void winsAfterDrawingFourthMatchingCard() throws InterruptedException {
        deal(1, 1, 1, 2);
        left.discard(new Card(1)); //means the right deck has only a one for the player to draw should then win

        runPlayer();

        assertTrue(GameOver.get());
        assertEquals(1, WinningPlayerID.get());
        assertEquals(2, right.draw().getCardValue()); // got rid of the 2
    }

    @Test
    public void neverDiscardWantedValue() throws InterruptedException {
        deal(1, 2, 3, 4);
        left.discard(new Card(1));//right deck now contains all these cards to draw from
        left.discard(new Card(1));
        left.discard(new Card(6));
        left.discard(new Card(1));

        runPlayer();

        while (right.getSize() > 0) {
            assertNotEquals(1, right.draw().getCardValue()); // everything discarded is in the right deck
        }//check no ones have been discarded
    }  
    
    @Test
    public void stopWhenGameWon() throws InterruptedException {
        deal(1, 2, 3, 4);
        left.discard(new Card(2));
        WinningPlayerID.set(3);
        GameOver.set(true);

        runPlayer();

        assertEquals(3, WinningPlayerID.get());
        assertEquals(0, right.getSize());//no cards should have had chance to be discarded
    }

    @Test
    public void noCardsLostOrCreated() throws InterruptedException {
        deal(1, 1, 1, 5);
        left.discard(new Card(6)); //cards to draw from
        left.discard(new Card(1));

        runPlayer();
        //check only 6 cards remain in circulation
        assertEquals(6, hand.size() + left.getSize() + right.getSize());
        assertEquals(4, hand.size());
    }
    @Test
    public void emptyLeftDeckDoesNothing() throws Exception {
        deal(1, 2, 3, 4);
        runFor(100);
        assertFalse(GameOver.get() && WinningPlayerID.get() != 0);
        assertEquals(4, hand.size());
        assertEquals(0, right.getSize());
    }
    @Test
    public void handStaysAtFourCardsAfterEveryTurn() throws Exception {
        deal(1, 2, 3, 4);
        for (int v = 5; v <= 9; v++) {
            left.discard(new Card(v));          // 5 distinct cards: no win is possible
        }

        runFor(200);
        assertEquals(0, left.getSize()); //check all cards moved from left to right
        assertEquals(5, right.getSize());

        // nobody won, and the hand is back to 4 cards
        assertEquals(0, WinningPlayerID.get());
        assertEquals(4, hand.size());
    }
}
