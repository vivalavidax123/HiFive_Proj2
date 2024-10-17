package hifive;

public enum Suit {
    SPADES("S", 20), HEARTS("H", 15), DIAMONDS("D", 10), CLUBS("C", 5);
    private String suitShortHand = "";
    private int bonusFactor = 1;

    Suit(String shortHand, int bonusFactor) {
        this.suitShortHand = shortHand;
        this.bonusFactor = bonusFactor;
    }

    // Converts a string representation of a card to its corresponding Suit
    public static Suit getSuitFromString(String cardName) {
        String suitString = cardName.substring(cardName.length() - 1);

        for(Suit suit : Suit.values()) {
            if(suit.getSuitShortHand().equals(suitString)) {
                return suit;
            }
        }
        return Suit.CLUBS;
    }

    // Returns the short-hand representation of the suit
    public String getSuitShortHand() {
        return suitShortHand;
    }

    // Returns the bonus factor associated with the suit
    public int getBonusFactor() {
        return bonusFactor;
    }
}
