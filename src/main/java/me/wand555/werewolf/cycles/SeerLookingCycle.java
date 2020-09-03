package me.wand555.werewolf.cycles;

import me.wand555.werewolf.PlayerCard;
import me.wand555.werewolf.cycles.daytimechange.BeginDayCycle;

import java.util.UUID;

public class SeerLookingCycle extends ConversationCycle {

    private final PlayerCard seer;
    private PlayerCard lookedAt;

    public SeerLookingCycle(MakroCycle makroCycle) {
        this(makroCycle.getCardPointer().getSeer(), makroCycle);
    }

    public SeerLookingCycle(PlayerCard seer, MakroCycle makroCycle) {
        super(Phase.SEER_CHOOSE, makroCycle, new UUID[] {seer.getUUID()});
        this.seer = seer;
    }

    @Override
    public void beginCycle() {
        makeUnconscious(seer);
        super.beginCycle();
    }

    @Override
    public void endCycle() {
        makroCycle.getRoundInformation().setLookedAt(lookedAt);
        super.endCycle();
    }

    @Override
    protected Cycle getNextCycle() {
        return new BeginDayCycle(makroCycle);
    }

    public String lookAtName(PlayerCard toLookAt) {
        this.lookedAt = toLookAt;
        return toLookAt.getCard().getType().getName();
    }

    @Override
    public int getStartTimeUntilOver() {
        return 2*60;
    }

    @Override
    public void broadcastUnspecifiedEventMessage() {

    }

    @Override
    public void broadcastEventMessage(Object[] data) {

    }
}
