import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;


public class Player implements Runnable {
    private final int PlayerID;
    private List<Card> Hand;
    private CardDeck leftDeck;
    private CardDeck rightDeck;
    private static AtomicBoolean GameOver = new AtomicBoolean(false);
    private final AtomicInteger WinningPlayerID;
    private final ReentrantLock Playerlock = new ReentrantLock();
    private PlayerOutput playerOutput;

    public Player(int PlayerID,CardDeck leftDeck,CardDeck rightDeck,AtomicBoolean GameOver,
                List<Card> Hand, AtomicInteger WinningPlayerID, PlayerOutput playerOutput, String PlayerOutputFile){
        this.PlayerID = PlayerID;
        this.leftDeck = leftDeck;
        this.rightDeck=rightDeck;
        this.GameOver=GameOver;
        this.Hand = Hand;
        this.WinningPlayerID = WinningPlayerID;
        this.playerOutput = new PlayerOutput(PlayerID);
    }

    public int getPlayerID(){
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
                Thread.yield();
            }
            int Winner = WinningPlayerID.get();
            if (Winner != this.PlayerID) {
                playerOutput.logWinnerNotification(Hand,Winner);
            }
        }
        //if there is an error with a single player print which
        catch (RuntimeException PlayerError) {
        System.err.println("Player " + PlayerID + " error: " + PlayerError.getMessage());
        } 
        finally {
        playerOutput.closePlayerOutput();   // always close the log file
        }


    }
/**the player object is locked when draw + dicard so no other thread can interrupt it during 
 * the time it holds 5 cards 
 * */
    private void DrawAndDiscard(){
        if(GameOver.get()){
            return;
        } // see if game over before attempting to draw next card
        Playerlock.lock();
        //draw from left deck

        Card drawnCard = leftDeck.draw();
        if (drawnCard == null){
            return;
        }
        else{ //only draw and discard a card if there is one available
        Hand.add(drawnCard);
        
        // Choose which card to discard
        Card discardedCard = chooseDiscardCard();
        Hand.remove(discardedCard);
        rightDeck.discard(discardedCard);
        
        playerOutput.logTurn(drawnCard.getCardValue(),discardedCard.getCardValue(),Hand,leftDeck.getDeckId(),rightDeck.getDeckId());
        if(HasWinningHand()){
            HandleWin();
        }}
        playerOutput.closePlayerOutput();
        Playerlock.unlock();
 
    }
    private void HandleWin(){
        if(GameOver.compareAndSet(false,true)){
            WinningPlayerID.set(this.PlayerID);//broadcast player id
            System.out.println("Player "+PlayerID+" wins");
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
        //if all cards are the players preffered then the previous win logic has failed
        }throw new IllegalStateException("Player "+PlayerID+" has no card to discard");
    }
}