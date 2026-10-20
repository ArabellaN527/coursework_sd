# Software development coursework
# muilti threaded card game

A java coursework, simulating a multiplayer card game in which players aim to collect a full hand of thier prefferred card value. Note: game requires that there be 8n cards in circulation where n is the number of players. When a player collects a full hand it notifies all other player threads it has won and all moves are recorded in the output files. 

**Thread safety:** each `CardDeck` locks itself on every `draw()`, `discard()` and `getSize()`, and the game-over flag and winner ID are shared through `AtomicBoolean` and `AtomicInteger`, so only one player can be recorded as the winner.

## Requirements
 
- A JDK (any recent version)
- JUnit 4.13.2 and Hamcrest 1.3, only needed for the tests (see below)

## Running the game
 
From the project root:
 
```
mkdir -p out
javac -d out *.java
java -cp out CardGame
```
the 'out' command is not necessarry the commands work without but it sends the .class files to the out file when compiling keeping the file structure clean. 

to run the tests use:
```
javac -cp "lib/*" -d out *.java tests/*.java
```
### Inputs
when running the project asks for two inputs from the command line; a number of players and the location of a valid deck input.
Note: deck must be a line seperate plain text file of positive integers AND must have exactly 8n entries (n = number of players).

### Outputs
after running the project the file that you run from will now contain 2n plain text files; half containing each players log of their turns and who won and half containing the final state of each deck.
Note; some of the deck files may be empty this does not suggest a problem with the running of the project it is just a side effect of the concurrent threads drawing cards at different times. 

A game only finishes if some player can collect four cards of one value. A pack where no value appears four times will run until you stop it with Ctrl+C.

### running the tests
The tests use JUnit 4. Download the jars once, from the project root or check that the lib file already contains them:
```
mkdir -p lib
wget -P lib https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar
wget -P lib https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar
```
Compile everything and run all the test classes:
 
```
mkdir -p out
javac -cp "lib/*" -d out *.java tests/*.java
java -cp "out:lib/*" org.junit.runner.JUnitCore PlayerTest CardTest CardDeckTest PlayerOutputTest CardGameTest
```