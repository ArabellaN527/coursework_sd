import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.locks.*;
import java.util.LinkedList;

public class CardDeckTest{
    private CardDeck deck;
    //test methods under normal conditions
    @Before
    public void setUp() {
        deck = new CardDeck(1, new LinkedList<Card>());
    }
    @Test
    public void TestgetDeckID(){
         assertEquals(1, deck.getDeckId());
    }
    @Test
    public void TestdrawFromEmptyDeck() {
        deck.draw();
        assertEquals(0, deck.getSize()); // deck remains empty if drawn from
    }
    @Test
    public void TestDiscard() {
        deck.discard(new Card(3));
        assertEquals(1, deck.getSize());
    }
     @Test
    public void TestdrawRemovesFromDeck() {
        deck.discard(new Card(3));
        deck.discard(new Card(4));
        deck.draw();
        assertEquals(1, deck.getSize());
}   
    @Test
    public void TestFirstInFirstOut() {
        deck.discard(new Card(4));
        deck.discard(new Card(5));
        assertEquals(4, deck.draw().getCardValue());
        assertEquals(5, deck.draw().getCardValue());
        assertEquals(0, deck.getSize());
    }
    //test the deck locks
    @Test
    public void TestlockIsusable() {
        ReentrantLock lock = deck.getLock();
        assertNotNull(lock);
 
        lock.lock();
        try {
            assertTrue(lock.isHeldByCurrentThread());
            // the lock is reentrant, so the deck's own methods still work while held
            deck.discard(new Card(1));
            assertEquals(1, deck.getSize());
        } finally {
            lock.unlock();
        }
        assertFalse(lock.isLocked());
    }
    @Test
    public void TestlockIsReleased() {
        deck.discard(new Card(1));
        deck.draw();
        deck.draw();   // empty draw must also release the lock
        deck.getSize();
        assertFalse(deck.getLock().isLocked());
    }


}