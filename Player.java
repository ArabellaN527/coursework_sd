import java.util.concurrent.atomic.AtomicBoolean;

public class Player {
    private final int PlayerID;
    private LinkedList<Card> Hand = new LinkedList<>();
    private Deck leftDeck;
    private Deck rightDeck;
    private static AtomicBoolean GameOver = new AtomicBoolean(false);

    public Player(int PlayerID,Deck leftDeck,Deck rightDeck,AtomicBoolean GameOver){
        this.PlayerID = PlayerID;
        this.leftDeck = leftDeck;
        this.rightDeck=rightDeck;
        this.GameOver=GameOver;
    }
    public static void run(){

    }

    private void DrawAndDiscard(int PlayerID){
        synchronized (handLock){
        //draw from right deck
        Card drawnCard = rightDeck.draw();
        Hand.add(drawnCard);

        // Choose which card to discard
        Card discardedCard = chooseDiscardCard();
        Hand.remove(discardedCard);
        leftDeck.discard(discardedCard);
 
    }
}