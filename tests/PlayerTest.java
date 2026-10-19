import static org.junit.Assert.*;

import java.beans.Transient;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.smartcardio.Card;

import java.util.List;
import java.util.ArrayList;

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
        left = new CardDeck(1);
        right = new CardDeck(2);
        GameOver = new AtomicBoolean(false);
        WinningPlayerID = new AtomicInteger(0); // 0 = no winner ID yet (players are 1-4)
        player = new Player(1, left, right, GameOver, hand, WinningPlayerID);
    }

    private void deal(int a, int b, int c, int d) {
        hand.add(new Card(a));
        hand.add(new Card(b));
        hand.add(new Card(c));
        hand.add(new Card(d)); // sets up hnad with 4 cards
    }

    @Test
    public void getPlayerIDReturnsID() {
        assertEquals(1, player.getPlayerID());
    }


    @Test
    public void winsAfterDrawingFourthMatchingCard() throws InterruptedException {
        deal(1, 1, 1, 2);
        left.discard(new Card(1));

        runplayer();

        assertTrue(GameOver.get());
        assertEquals(1, WinningPlayerID.get());
        assertEquals(2, right.draw().getCardValue()); // got rid of the 2
    }

    @Test
    public void neverDiscardWantedValue() throws InterruptedException {
        deal(1, 2, 3, 4);
        left.discard(new Card(1));
        left.discard(new Card(1));
        left.discard(new Card(5));
        left.discard(new Card(6));
        left.discard(new Card(1));

        runplayer();

        while (right.getSize() > 0) {
            assertNotEquals(1, right.draw().getCardValue()); // everything discarded is in the right deck
        }
    }  
    
    @Test
    public void stopWhenGameWon() throws InterruptedException {
        deal(1, 2, 3, 4);
        left.discard(new Card(2));
        WinningPlayerID.set(3);
        GameOver.set(true);

        runPlayer();

        assertEquals(3, WinningPlayerID.get());
        assertEquals(1, left.getSize());
    }

    @Test
    public void noCardsLostOrCreated() throws InterruptedException {
        deal(1, 1, 1, 5);
        left.discard(new Card(6));
        left.discard(new Card(1));

        runPlayer();

        assertEquals(6, hand.size() + left.getSize() + right.getSize());
        assertEquals(4, hand.size());
    }
}
