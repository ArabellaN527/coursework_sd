import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
public class PlayerOutput {
    private int PlayerID;
    private String Filename;
    private PrintWriter writer;
    //create individual player output writing streams for each player
    public PlayerOutput(int PlayerID){
        this.PlayerID = PlayerID;
        this.Filename = "Player"+PlayerID+"_Output.txt";
        //write to specifically the file for that player
        try{
            this.writer = new PrintWriter(new FileWriter(this.Filename,false));
        }
        catch(IOException inputError){
            System.out.println("error creating file for "+PlayerID);
            inputError.printStackTrace();
        }
    }
    //start the log for the player at the begginning of each game
    public void StartLog(List<Card> Hand){
        if(writer!= null){
            writer.println("Player "+PlayerID+" initial hand ");
            printHand(Hand);
        }
    }
    //log the cards moved for every turn 
    public void logTurn(int CardDrawn,int CardDiscarded, List<Card> Hand, int leftDeck, int rightDeck){
        if (writer!=null){
            writer.println("Player "+PlayerID+" draws a "+ CardDrawn +" from deck "+ leftDeck);
            writer.println("Player "+PlayerID+" discards a "+ CardDiscarded + " to deck " + rightDeck);
            writer.println("Player "+PlayerID+" current hand ");
            printHand(Hand);

        }
    }
    // log winner in every player log
   public void logWinnerNotification(List<Card> Hand, int WinningPlayerID){
     if (writer!=null){
            if (WinningPlayerID== PlayerID){
                writer.println("Player "+PlayerID+" wins");
                writer.println("Player "+PlayerID+" exits");
                writer.println("Player "+PlayerID+" final hand:");
                printHand(Hand);
            }
            else{
                writer.println("Player "+ WinningPlayerID+" has informed player "+ PlayerID+" that player "+WinningPlayerID+ " has won");
                writer.println("Player "+PlayerID+" exits");
                writer.println("Player "+PlayerID+" final hand:");
                printHand(Hand);
            }
        }
   }
   //to print the string of number from a hand
   public void printHand(List<Card> Hand){
    if(writer!= null && Hand!=null){
        for(Card card: Hand){
            writer.print(card.getCardValue()+" ");
        }
        //create new line after printing hand and save to file
        writer.println();
        writer.flush();
    }
   }
   //close player output after every loop to reduce resource use 
   public void closePlayerOutput(){
    if(writer!= null){
        writer.close();
    }
   }
}


