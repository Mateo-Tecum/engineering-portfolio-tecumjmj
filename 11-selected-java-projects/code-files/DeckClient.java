public class DeckClient {
    public static void main(String[] args) {
        Deck deck = new Deck();
        
        Card card = deck.draw();
        System.out.print("Was card that was drawn the Ace of Spades? ");
        System.out.println(card.equals(new Card(0, 0)) ? "Yes!" : "No!");
        
        card = deck.draw();
        System.out.print("Was card that was drawn the Two of Spades? ");
        System.out.println(card.equals(new Card(1, 0)) ? "Yes!" : "No!");
        
        card = deck.draw();
        System.out.print("Was card that was drawn the Three of Spades? ");
        System.out.println(card.equals(new Card(2, 0)) ? "Yes!" : "No!");
        
        System.out.println("The deck is " + (deck.isEmpty() ? "empty" : "not empty") + ".");
        
        System.out.println(deck);
        
        for (int i = 0; i < Deck.SIZE_OF_DECK - 3; ++i) {
            deck.draw();
        }
        
        System.out.println("The deck is " + (deck.isEmpty() ? "empty" : "not empty") + ".");
    }
}
