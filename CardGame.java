import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.*;

public class CardGame {
    private static List<Card> InitialDeck = new ArrayList<Card>();
    private int numberPlayers;

    public static void initialiseGame(String filePath, int numberPlayers){
        InitialDeck.clear();
        try (Scanner scanner = new Scanner(new File(filePath))){
            while(scanner.hasNextInt()){
                int cardValue = scanner.nextInt();
                InitialDeck.add(new Card(cardValue));
            }
        }
        catch(FileNotFoundException error){
            System.out.println("error file not found");
            return;
        }
        if(InitialDeck.size() == (8* numberPlayers)){
            distributeCards(InitialDeck, numberPlayers);
        }
        else{
            System.out.println("input file length and number of players do not match");
        }
    }
    private static void distributeCards(List<Card> InitialDeck,int numberPlayers){

    }

    
}