package me.wand555.werewolf;

import me.wand555.werewolf.cards.Card;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class' purpose is to keep track of which player has what card.
 * If a player dies/leaves he is <strong>not</strong> removed from here
 * with the exception of villageDayVotingPlayers.
 */
public class CardPointer {

    private final Collection<PlayerCard> cards;

    private final PlayerCard armor;
    private List<PlayerCard> couple;
    private final PlayerCard seer;
    private final PlayerCard witch;
    private final Set<PlayerCard> werewolfs;
    private final Set<PlayerCard> villagers;

    /**
     * Use this to specifically mean all players that are eligible to vote during the day
     * (every non spectator).
     */
    private final Set<PlayerCard> villageDayVotingPlayers;

    public CardPointer(Collection<PlayerCard> cards) {
        this.cards = cards;
        this.armor = mapToTypeSingle(Card.Type.ARMOR);
        this.couple = new ArrayList<>();
        this.seer = mapToTypeSingle(Card.Type.SEER);
        this.witch = mapToTypeSingle(Card.Type.WITCH);
        this.werewolfs = mapToTypeMultiple(Card.Type.WEREWOLF);
        this.villagers = mapToTypeMultiple(Card.Type.VILLAGER);
        this.villageDayVotingPlayers = mapToDayVoteEligible();
    }

    public PlayerCard getPlayerCard(UUID uuid) {
        return cards.stream().filter(pCard -> pCard.getUUID().equals(uuid)).findFirst().orElse(null);
    }

    public PlayerCard getArmor() {
        return armor;
    }

    public boolean hasArmor() {
        return armor != null;
    }

    public List<PlayerCard> getCouple() {
        return couple;
    }

    public boolean hasCouple() {
        return couple.get(0) != null && couple.get(1) != null;
    }

    public void setFirstPlayerCouple(PlayerCard pCard) {
        couple.add(0, pCard);
    }

    public void setSecondPlayerCouple(PlayerCard pCard) {
        couple.add(1, pCard);
    }

    public PlayerCard getSeer() {
        return seer;
    }

    public boolean hasSeer() {
        return seer != null;
    }

    public PlayerCard getWitch() {
        return witch;
    }

    public boolean hasWitch() {
        return witch != null;
    }

    public Set<PlayerCard> getWerewolfs() {
        return werewolfs;
    }

    public Set<PlayerCard> getVillagers() {
        return villagers;
    }

    public Set<PlayerCard> getVillageDayVotingPlayers() {
        return villageDayVotingPlayers;
    }

    public boolean removeFromVillageDayVotingPlayers(PlayerCard playerCard) {
        return  villageDayVotingPlayers.remove(playerCard);
    }

    /**
     * Not every card guarantees that the player is online.
     * If names are needed, they should be accessed with @{@link PlayerCard#getName()}.
     * Any other use that requires the player object should check with @{@link PlayerCard#partOfCycle()} first.
     * @return
     */
    public Collection<PlayerCard> getCards() {
        return cards;
    }

    /**
     * Use this if @{@link Card.Type#getMaxAmount()} == 1 (theoretically if != 0, but as of now
     * there are no cards that have a max amount over 1).
     * @param type
     * @return
     */
    private PlayerCard mapToTypeSingle(Card.Type type) {
        return cards.stream()
                .filter(pCard -> pCard.getCard().getType() == type)
                .findFirst().orElse(null);
    }

    /**
     * Use this if @{@link Card.Type#getMaxAmount()} == 0
     * @param type
     * @return
     */
    private Set<PlayerCard> mapToTypeMultiple(Card.Type type) {
        return cards.stream()
                .filter(pCard -> pCard.getCard().getType() == type)
                .collect(Collectors.toSet());
    }

    private Set<PlayerCard> mapToDayVoteEligible() {
        return cards.stream()
                .filter(pCard -> pCard.getCard().getType() != Card.Type.SPECTATOR)
                .collect(Collectors.toSet());
    }
}
