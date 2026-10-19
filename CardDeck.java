import java.util.*;
import java.util.List;
import java.io.FileNotFoundException;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.LinkedList;

// the card deck is a linked list of card elements
// the lock is for the DECK being threadsafe not the player hand
public class CardDeck {
    private final int deckId;
    private LinkedList<Card> Deck = new LinkedList<>();
    private final ReentrantLock lock = new ReentrantLock();
//deck only needs to be identified by id
    public CardDeck(int deckId, LinkedList<Card> Deck) {
        this.deckId = deckId;
        this.Deck = Deck;
    }
/**draw action is threadsafe by locking deck before draw,
 * so only one player can use draw at once
 * pollfirst is needed so you cant draw from empty deck
 * */
 
    public Card draw() {
        lock.lock();
        try {
            return Deck.pollFirst();      // null if empty
        } finally {
            lock.unlock();
        } //unlock lock so never cause livelock 
    }
    //same locking method as draw
    //returns card to end of deck
    public void discard(Card card) {
        lock.lock();
        try {
            Deck.addLast(card);
        } finally {
            lock.unlock();
        }
    }
    //returns size of deck
    public int getSize() {
        lock.lock();
        try { return Deck.size(); } finally { lock.unlock(); }
    }
    public int getDeckId() { 
        return deckId; 
    }
//method to allow threads to claim the lock for a deck
    public ReentrantLock getLock() { 
        return lock; 
    }
    public LinkedList<Card> getCards(){
        return this.Deck;
    }//the output method needs to access the private contents of the deck
}