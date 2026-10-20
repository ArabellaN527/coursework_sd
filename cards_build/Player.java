import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

//runnable allows to be run on threads 
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
                List<Card> Hand, AtomicInteger WinningPlayerID, PlayerOutput playerOutput){
        this.PlayerID = PlayerID;
        this.leftDeck = leftDeck;
        this.rightDeck=rightDeck;
        this.GameOver=GameOver;
        this.Hand = Hand;
        this.WinningPlayerID = WinningPlayerID;
        this.playerOutput = new PlayerOutput(PlayerID);
        //each player starts its own output file
    }

    public int getPlayerID(){
        return PlayerID;
    }


    @Override
    //the player logic for when the game is running 
    public void run(){
        playerOutput.StartLog(Hand);
        try {//first check if any player was dealt the winning hand 
            if(HasWinningHand()){
                HandleWin();
                return;
            }//each player plays untill a thread notifies them through game over that they have won
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
        playerOutput.closePlayerOutput();   // always close the log file at the end of run
        }


    }
/**the player object is locked when draw + discard so no other thread can interrupt it during 
 * the time it holds 5 cards 
 * */
    private void DrawAndDiscard(){
        if(GameOver.get()){
            return;
        } // see if game over before attempting to draw next card
        Playerlock.lock();//cannot be interrupted during draw and discard
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
        Playerlock.unlock();
 
    }
    private void HandleWin(){//only allow handle win if the gameover hasnt already been changed by another thread 
        if(GameOver.compareAndSet(false,true)){
        //using atomic compareand set prevents other players attempting to declare a win at the same time
            WinningPlayerID.set(this.PlayerID);//broadcast player id
            System.out.println("Player "+PlayerID+" wins");
            playerOutput.logWinnerNotification(Hand,PlayerID);
        }
    }
    private boolean HasWinningHand(){
        if(Hand.size()!=4){ 
            return false;
        }//player should never have >4 cards 
        int First = Hand.get(0).getCardValue();
        for(Card card: Hand){
            if (card.getCardValue() != First){
                return false;
            }}//false claim if anycards are different
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