public class Card {
    
    private int CardValue;

    public Card(int CardValue) {
        if (CardValue < 0) {
        throw new IllegalArgumentException("Card value must be non-negative: " + CardValue);
        } //error handling for card values 
        this.CardValue = CardValue;
    }

    public int getCardValue() {
        return CardValue;
    }

    @Override
    public String toString() {
        return String.valueOf(CardValue);
    }

}

    