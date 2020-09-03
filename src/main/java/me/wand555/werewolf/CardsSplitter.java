package me.wand555.werewolf;

import me.wand555.werewolf.cards.Card;

import java.util.HashMap;

public class CardsSplitter {

    private final int playerAmount;
    private int villagerAmount;
    private int werewolfAmount;
    private int seerAmount;

    public CardsSplitter(int playerAmount) {
        //min Spieler Anzahl = 4
        this.playerAmount = playerAmount;
        int counter = 0;
        counter = determineWerewolfAmount();
        if(counter <= playerAmount) {
            counter += determineSeerAmount();
            if(counter <= playerAmount) {

            }
        }
        villagerAmount = playerAmount - counter;
    }

    public int determineWerewolfAmount() {
        double firstVariable = Math.pow((playerAmount/10), 4);
        double secondVariable = playerAmount/25;
        double thirdVariable = 1;
        return (int) (firstVariable + secondVariable + thirdVariable);
    }

    public int determineSeerAmount() {
        return Card.Type.SEER.getMaxAmount();
    }

    public HashMap<Card.Type, Integer> getSuggestedSplits() {
        HashMap<Card.Type, Integer> splittings = new HashMap<>();
        splittings.put(Card.Type.VILLAGER, villagerAmount);
        splittings.put(Card.Type.WEREWOLF, werewolfAmount);
        splittings.put(Card.Type.SEER, seerAmount);
        return splittings;
    }
}
