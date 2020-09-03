package me.wand555.werewolf.cycles.armor;

import me.wand555.werewolf.PlayerCard;
import me.wand555.werewolf.cycles.ConversationCycle;
import me.wand555.werewolf.cycles.Cycle;
import me.wand555.werewolf.cycles.ForceCompletion;
import me.wand555.werewolf.cycles.MakroCycle;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ArmorChooseCycle extends ConversationCycle implements ForceCompletion {

    private final PlayerCard armor;
    private PlayerCard firstPlayer;
    private PlayerCard secondPlayer;

    public ArmorChooseCycle(MakroCycle makroCycle) {
        this(makroCycle.getCardPointer().getArmor(), makroCycle);
    }

    public ArmorChooseCycle(PlayerCard armor, MakroCycle makroCycle) {
        super(Phase.ARMOR_CHOOSE, makroCycle, new UUID[] {armor.getUUID()});
        this.armor = armor;
    }

    @Override
    public void beginCycle() {
        makeUnconscious(armor);
        super.beginCycle();
    }

    @Override
    public void endCycle() {
        makroCycle.getRoundInformation().setFirstPlayer(firstPlayer);
        makroCycle.getRoundInformation().setSecondPlayer(secondPlayer);
        super.endCycle();
    }

    @Override
    protected Cycle getNextCycle() {
        return new LovedSeeCycle(firstPlayer, secondPlayer, makroCycle);
    }

    /**
     *
     * @return
     */
    @Override
    public void broadcastUnspecifiedEventMessage() {
        Bukkit.broadcastMessage("Armor has chosen the couple.");
    }

    /**
     * data:
     * 0 - firstPlayer
     * 1 - secondPlayer
     * @param data
     * @return
     */
    @Override
    public void broadcastEventMessage(Object[] data) {
        return;
    }

    public PlayerCard getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(PlayerCard firstPlayer) {
        this.firstPlayer = firstPlayer;
        makroCycle.getCardPointer().setFirstPlayerCouple(firstPlayer);
    }

    public PlayerCard getSecondPlayer() {
        return secondPlayer;
    }

    public void setSecondPlayer(PlayerCard secondPlayer) {
        this.secondPlayer = secondPlayer;
        makroCycle.getCardPointer().setSecondPlayerCouple(secondPlayer);
    }

    @Override
    public int getStartTimeUntilOver() {
        return 60;
    }

    @Override
    public String getPageReadyHistory() {
        String result = super.getPageReadyHistory();
        result += armor.getName() + " (Armor) has chosen " + firstPlayer.getName() +
                " and " + secondPlayer.getName() + " as the couple.";
        return result;
    }

    @Override
    public void forceComplete() {
        List<PlayerCard> candidates = makroCycle.getCardPointer().getVillageDayVotingPlayers()
                .stream().collect(Collectors.toList());
        PlayerCard firstRandom = candidates.remove(ThreadLocalRandom.current().nextInt(candidates.size()));
        PlayerCard secondRandom = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        setFirstPlayer(firstRandom);
        setSecondPlayer(secondRandom);
    }

}
