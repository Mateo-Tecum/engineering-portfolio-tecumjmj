public class CardClient {
    public static void main(String[] args) {
        Card firstCard = new Card(0, 0);
        System.out.println("Rank of first card is " + firstCard.getRank() + ".");
        
        Card secondCard = new Card(12, 1);
        System.out.println("Suit of second card is " + secondCard.getSuit() + ".");
        
        System.out.println(firstCard + "\n" + secondCard);
        
        System.out.print("First card and second card are ");
        System.out.println((firstCard.equals(secondCard) ? "equal" : "not equal") + ".");
        
        Card thirdCard = new Card(2, 3);
        Card fourthCard = new Card(2, 3);
        
        System.out.println(thirdCard);
        System.out.println(fourthCard);
        
        System.out.print("Third card and fourth card are ");
        System.out.println((thirdCard.equals(fourthCard) ? "equal" : "not equal") + ".");
    }
}
