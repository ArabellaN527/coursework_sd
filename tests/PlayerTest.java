import static org.junit.Assert.*;

import java.beans.Transient;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

public class PlayerTest {

    private CardDeck left;
    private CardDeck right;
    private AtomicInteger WinningPlayerID;
    private Player player;
    private AtomicBoolean GameOver;

    @Before
    public void setUp() {
        left = new CardDeck(1, 16);
        right = new CardDeck(2, 16);
        GameOver = new AtomicBoolean(false);
        WinningPlayerID = new AtomicInteger(0); // 0 = no winner ID yet (players are 1-4)
        player = new player(1, left, right, GameOver);
    }

    @Test
    public void getPlayerIDReturnsIID() {
        assertEquals(1, player.getPlayerID());
    }

    @Test(expected = IllegalStateException.class)
    public void fifthCardIsRejected() {
        deal(1, 2, 3, 4);
        player.receiveCard(new Card(5));
    }

    @Test
    public void winsAfterDrawingFourthMatchingCard() throws InterruptedException {
        deal(1, 1, 1, 2);
        left.discard(new Card(1));

        run.player();

        assertTrue(GameOver.get());
        assertEquals(1, WinningPlayerID.get());
        assertEquals(2, right.draw().getValue()); // got rid of the 2
    }

    @Test
    public void neverDiscardWantedValue() throws InterruptedException {
        deal(1, 2, 3, 4);
        left.discard(new Card(1));
        left.discard(new Card(1));
        left.discard(new Card(5));
        left.discard(new Card(6));
        left.discard(new Card(1));

        run.player();

        while (right.getSize() > 0) {
            assertNotEquals(1, right.draw().getValue());
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

        assertEquals(6, 4 + left.getSize() + right.getSize());
    }
}
