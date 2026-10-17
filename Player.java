import java.util.concurrent.atomic.AtomicBoolean;
import java.util.list;
import java.util.concurrent.atomic.AtomicInteger;

public class Player {
    private final int PlayerID;
    private List<Card> Hand;
    private Deck leftDeck;
    private Deck rightDeck;
    private static AtomicBoolean GameOver = new AtomicBoolean(false);
    private final AtomicInteger WinningPlayerID;
    private final ReentrantLock Playerlock = new ReentrantLock();
    private final PlayerOutput;

    public Player(int PlayerID,Deck leftDeck,Deck rightDeck,AtomicBoolean GameOver,
                List<Card> Hand, AtomicInteger WinningPlayerID, PlayerOutput playerOutput, String PlayerOutputFile){
        this.PlayerID = PlayerID;
        this.leftDeck = leftDeck;
        this.rightDeck=rightDeck;
        this.GameOver=GameOver;
        this.Hand = Hand;
        this.GameOver = GameOver;
        this.WinningPlayerID = WinningPlayerID;
        this.playerOutput = playerOutput;
    }

    public getPlayerID(){
        return PlayerID;
    }


    @Override
    public void run(){
        playerOutput.StartLog(Hand);
        try {
            if(HasWinningHand()){
                HandleWin();
                return;
            }
            while(!GameOver.get()){
                DrawAndDiscard();
                thread.yield();
            }
            int Winner = WinningPlayerID.get();
            if (Winner != this.PlayerID) {
                playerOutput.logWinnerNotification(Hand,Winner);
            }
        }
        //if there is an error with a single player stop that thread to keep threadsafe
        //use interrupt exception as the thread is stopping
        catch(InterruptedException PlayerError){
            Thread.currentThread().interrupt();
            system.err.println("Player " + PlayerID +" error");
        }


    }
/**the player object is locked when draw + dicard so no other thread can interrupt it during 
 * the time it holds 5 cards 
 * */
    private void DrawAndDiscard(){
        if(GameOver()){
            return;
        } // see if game over before attempting to draw next card
        Playerlock.lock();
        //draw from right deck
        Card drawnCard = rightDeck.draw();
        Hand.add(drawnCard);

        // Choose which card to discard
        Card discardedCard = chooseDiscardCard();
        Hand.remove(discardedCard);
        leftDeck.discard(discardedCard);

        playerOutput.logTurn(drawnCard.getCardValue(),discardedCard.getCardValue(),Hand,leftDeck.getDeckId(),rightDeck.getDeckId());
        if(HasWinningHand()){
            HandleWin();
        }
        playerOutput.closePlayerOutput();
        Playerlock.unlock();
 
    }
    private void HandleWin(){
        if(GameOver.compareAndSet(false,true)){
            WinningPlayerID.set(this.PlayerID);//broadcast player id
            system.out.println("Player ",PlayerID," wins");
            playerOutput.logWinnerNotification(Hand,PlayerID);
        }
    }
    private boolean HasWinningHand(){
        if(Hand.size()!=4){ 
            return false;
        }
        int First = Hand.get(0).getCardValue();
        for(Card card: Hand){
            if (card.getCardValue() != First){
                return false;
            }}
        return true;
    }
    private Card chooseDiscardCard(){
        //simplest game play logic returns first card that is not the player favoured one
        for(Card card: Hand){
            if(card.getCardValue() != PlayerID){
                return card;
            }
        }
    }

}