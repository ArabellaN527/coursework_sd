import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class CardDeck {
    private final int deckId;
    private final LinkedList<Card> Deck = new LinkedList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public CardDeck(int deckId) {
        this.deckId = deckId;
    }
    public Card draw() {
        lock.lock();
        try {
            return Deck.pollFirst();      // null if empty
        } finally {
            lock.unlock();
        }
    }
    public void discard(Card card) {
        lock.lock();
        try {
            Deck.addLast(card);
        } finally {
            lock.unlock();
        }
    }
    public int getSize() {
        lock.lock();
        try { return Deck.size(); } finally { lock.unlock(); }
    }
    public int getDeckId() { 
        return deckId; 
    }

    public ReentrantLock getLock() { 
        return lock; 
    }







    

}