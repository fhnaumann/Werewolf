package me.wand555.werewolf.cycles.armor;

import me.wand555.werewolf.PlayerCard;
import me.wand555.werewolf.cycles.ConversationCycle;
import me.wand555.werewolf.cycles.Cycle;
import me.wand555.werewolf.cycles.MakroCycle;
import me.wand555.werewolf.cycles.daytimechange.BeginNightCycle;

import java.util.UUID;

public class LovedSeeCycle extends ConversationCycle {

    private final PlayerCard firstPlayer;
    private final PlayerCard secondPlayer;

    public LovedSeeCycle(MakroCycle makroCycle) {
        this(makroCycle.getCardPointer().getCouple().get(0), makroCycle.getCardPointer().getCouple().get(1), makroCycle);
    }

    public LovedSeeCycle(PlayerCard firstPlayer, PlayerCard secondPlayer, MakroCycle makroCycle) {
        super(Phase.LOVED_SEE, makroCycle, new UUID[] {firstPlayer.getUUID(), secondPlayer.getUUID()});
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    @Override
    public void beginCycle() {
        makeUnconscious(firstPlayer, secondPlayer);
        super.beginCycle();
    }

    @Override
    public void endCycle() {
        super.endCycle();
    }

    @Override
    protected Cycle getNextCycle() {
        return new BeginNightCycle(makroCycle);
    }

    @Override
    public void broadcastUnspecifiedEventMessage() {

    }

    @Override
    public void broadcastEventMessage(Object[] data) {

    }

    public PlayerCard getFirstPlayer() {
        return firstPlayer;
    }

    public PlayerCard getSecondPlayer() {
        return secondPlayer;
    }

    @Override
    public int getStartTimeUntilOver() {
        return 20;
    }

    @Override
    public String getPageReadyHistory() {
        return null;
    }
}
