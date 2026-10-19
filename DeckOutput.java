import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.LinkedList;

public class DeckOutput {
    private final int deckId;
    public CardDeck deck;
    private String Filename;
    private PrintWriter writer;

    public DeckOutput(CardDeck deck){
        deckId = deck.getDeckId();
        Filename = "deck"+deckId+"_Output.txt";
        //write to specifically the file for that deck
        try{
            this.writer = new PrintWriter(new FileWriter(this.Filename,false));
        }
        catch(IOException inputError){
            System.out.println("error creating file for "+deckId);
            inputError.printStackTrace();
        }
        writer.println("Deck"+deckId+" final contents: ");
        printDeck(deck);


    }
     public void printDeck(CardDeck deck){
    if(writer!= null && deck!=null){
        for(Card card: deck.getCards()){
            writer.print(card.getCardValue()+" ");
        }
        //create new line after printing deck and save to file
        writer.println();
        writer.flush();
    }
   }
   //close deck output after every loop to reduce resource use 
   public void closeDeckOutput(){
    if(writer!= null){
        writer.close();
    }

}}