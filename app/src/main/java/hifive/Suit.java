package hifive;

public enum Suit {
    SPADES("S", 20), HEARTS("H", 15), DIAMONDS("D", 10), CLUBS("C", 5);
    private String suitShortHand = "";
    private int bonusFactor = 1;

    Suit(String shortHand, int bonusFactor) {
        this.suitShortHand = shortHand;
        this.bonusFactor = bonusFactor;
    }

    public static Suit getSuitFromString(String cardName) {
        String suitString = cardName.substring(cardName.length() - 1);

        for(Suit suit : Suit.values()) {
            if(suit.getSuitShortHand().equals(suitString)) {
                return suit;
            }
        }
        return Suit.CLUBS;
    }

    public String getSuitShortHand() {
        return suitShortHand;
    }

    public int getBonusFactor() {
        return bonusFactor;
    }
}