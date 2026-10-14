public class Card {
    
    private int CardValue;

    public Card(int CardValue) {
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

    