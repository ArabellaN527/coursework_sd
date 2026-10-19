import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class CardGame {
    private static List<Card> InitialDeck = new ArrayList<Card>();
    private int numberPlayers;
    //create lists of players and decks to set up ring topology
    private static List<CardDeck> gameDecks = new ArrayList<>();
    private static List<List<Card>> PlayerHands = new ArrayList<>();
    private static List<Player> Players = new ArrayList<>();
    public static AtomicBoolean GameOver = new AtomicBoolean(false);
    private static AtomicInteger WinningPlayerID = new AtomicInteger();

    public static void initialiseGame(String filePath, int numberPlayers){
        InitialDeck.clear();
        PlayerHands.clear();
        gameDecks.clear();
        Players.clear();
        //attempt to input file path allowing for handling file not found
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
        //handle decks of the wrong size 
        if(InitialDeck.size() == (8* numberPlayers)){
            InitialDeal(InitialDeck, numberPlayers);
        }
        else{
            System.out.println("input file length and number of players do not match");
        }
    }
    //sets up the game by dealing and creating players and decks
    private static void InitialDeal(List<Card> InitialDeck,int numberPlayers){
        for(int i =0;i<numberPlayers;i++){
            gameDecks.add(new CardDeck(i+1,new LinkedList<Card>()));
            PlayerHands.add(new ArrayList<>());
        }
        int cardindex =0;
        //deal to all players round robin 
        for(int round=0;round<4;round++){
            for(int j = 0;j<numberPlayers;j++){
                Card CardToDeal = InitialDeck.get(cardindex++);
                PlayerHands.get(j).add(CardToDeal);
            }}
            //deal to all decks round robin 
        for(int round=0;round<4;round++){
            for(int j = 0;j<numberPlayers;j++){
                Card CardToDeal=InitialDeck.get(cardindex++);
                gameDecks.get(j).discard(CardToDeal);
            }}
            //create all the players and list them so they can be referenced in order
        for(int i =1;i<=numberPlayers;i++){
            if(i<numberPlayers){
            Players.add(new Player(i,gameDecks.get(i-1),gameDecks.get(i),GameOver,
                    PlayerHands.get(i-1),WinningPlayerID,new PlayerOutput(i)));
            }
            else{
                Players.add(new Player(i,gameDecks.get(i-1),gameDecks.get(0),GameOver,
                    PlayerHands.get(i-1),WinningPlayerID,new PlayerOutput(i)));
            }//last player should discard back to deck 1
            
        }  
    }

     public static void main(String[] args){
        Scanner in = new Scanner(System.in);
 
        // keep asking until initialiseGame manages to create the players
        while(Players.isEmpty()){
            System.out.println("Please enter the number of players:");
            int numberPlayers;
            try{
                numberPlayers = Integer.parseInt(in.nextLine().trim());
            }
            //make sure the input deck is formatted correctly 
            catch(NumberFormatException e){
                System.out.println("Number of players must be a whole number.");
                continue;
            }
            if(numberPlayers < 1){
                System.out.println("Number of players must be at least 1.");
                continue;
            }
            System.out.println("Please enter location of pack to load:");
            initialiseGame(in.nextLine().trim(), numberPlayers);
            // if the pack was invalid initialiseGame printed why and Players is still empty
        }
 
        // one thread per player; start() runs Player.run() on the new thread
        List<Thread> threads = new ArrayList<>();
        for(Player p : Players){
            threads.add(new Thread(p));
        }
        for(Thread t : threads){
            t.start();
        }
        try{
            for(Thread t : threads){
                t.join(); //makes the main thread wait until player threads have finished running
            }
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
            GameOver.set(true);
            //end game when threads are interrupted 
        }
    }
    
}