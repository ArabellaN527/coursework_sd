
import javax.smartcardio.Card;

public class CardTest {

    // NORMAL CASES

    @Test
    public void getValueReturnValueGivenToConstructor() {
        Card card = new Card(7);

        assertEquals(7, card.getValue());
    }

    @Test
    public void toStringReturnsJustTheValue() {
        Card card = new Card(10);

        assertEquals("10", card.toString());
    }

    @Test
    public void valueDoesntChange() {
        Card card = new Card(6);

        assertEquals(6, card.getValue());
        assertEquals(6, card.getValue());
        assertEquals(6, card.getValue());
    }

    @Test
    public void cardsKeepTheirOwnValue() {
        Card a = new Card(4);
        Card b = new Card(2);

        assertEquals(4, a.getValue());
        assertEquals(2, b.getValue());
    }

    // EDGE CASES

    @Test
    public void largeValuesStoredCorrectly() {
        Card card = new Card(Integer.MAX_VALUE);

        assertEquals(Integer.MAX_VALUE, card.getValue());
    }

    @Test
    public void zeroIsValidValue() {
        Card card = new Card(0);

        assertEquals(0, card.getValue());
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