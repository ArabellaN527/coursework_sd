
import javax.smartcardio.Card;

public class CardTest {

    // NORMAL CASES

    @Test // constructor stores value and getCard() returns it
    public void getCardValueReturnValueGivenToConstructor() {
        Card card = new Card(7);

        assertEquals(7, card.getCardValue());
    }

    @Test
    public void toStringReturnsJustTheValue() {
        Card card = new Card(10);

        assertEquals("10", card.toString());
    }

    @Test
    public void valueDoesntChange() {
        Card card = new Card(6);

        assertEquals(6, card.getCardValue());
        assertEquals(6, card.getCardValue());
        assertEquals(6, card.getCardValue()); // makes sure value remains the same after multiple runs
    }

    @Test // Two cards are independent of each other
    public void cardsKeepTheirOwnValue() {
        Card a = new Card(4);
        Card b = new Card(2);

        assertEquals(4, a.getCardValue());
        assertEquals(2, b.getCardValue());
    }

    // EDGE CASES

    @Test // value is non-negative integer
    public void zeroIsValidValue() {
        Card card = new Card(0);

        assertEquals(0, card.getCardValue());
    }

    // ERROR CASES

    @Test(expected = IllegalArgumentException.class)
    public void negativeOneIsRejected() {
        new Card(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void largeNegativeValuesRejected() {
        new Card(Integer.MIN_VALUE);
    }
}