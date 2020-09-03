package me.wand555.werewolf.cycles;

import me.wand555.werewolf.CountdownTimer;
import me.wand555.werewolf.SideBoardHandler;
import me.wand555.werewolf.Werewolf;
import org.bukkit.Bukkit;

import java.util.concurrent.ThreadLocalRandom;

public abstract class CountdownCycle extends Cycle {

    protected final CountdownTimer countdownTimer;

    public CountdownCycle(Phase phase, MakroCycle makroCycle) {
        super(phase, makroCycle);
        this.countdownTimer = new CountdownTimer(plugin, this, getStartTimeUntilOver(), sideBoardHandler);
    }

    @Override
    public void beginCycle() {
        sideBoardHandler.updatePhase(phase, getStartTimeUntilOver());
        countdownTimer.startTimer();
    }

    @Override
    public void endCycle() {
        countdownTimer.cancel();
        super.endCycle();
    }

    public CountdownTimer getCountdownTimer() {
        return countdownTimer;
    }

    /**
     * Something like:
     * - Armor has coupled two players.
     * - Witch has used heal/damage potion
     *
     * These methods are placed here and not in @{@link ConversationCycle}, because every countdown that reached
     * 0 will broadcast a message.
     */
    public abstract void broadcastUnspecifiedEventMessage();

    public abstract void broadcastEventMessage(Object[] data);
}
