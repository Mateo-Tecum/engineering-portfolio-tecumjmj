public class PlayerClient {
    public static void main(String[] args) {
        Player joker = new Player();
        
        joker.addCard(new Card(0, 0));
        joker.addCard(new Card(0, 1));
        joker.addCard(new Card(0, 2));
        joker.addCard(new Card(0, 3));
        
        System.out.printf("Player %d has %d cards.\n", joker.getID(), joker.getNumOfCards());
        
        System.out.print("Does Player " + joker.getID() + " have an Ace or a Spade? ");
        System.out.println(joker.hasCard(new Card(0, 0)) > -1 ? "Yes!" : "No!");
        
        joker.getCard(1);
        System.out.printf("Player %d has %d cards.\n", joker.getID(), joker.getNumOfCards());
        
        System.out.println(joker);
    }
}
