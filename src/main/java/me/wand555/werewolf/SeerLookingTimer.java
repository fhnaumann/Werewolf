package me.wand555.werewolf;

import me.wand555.werewolf.cycles.SeerLookingCycle;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The time the @{@link me.wand555.werewolf.cards.SeerCard} is looking at a card he choose.
 * Once the time is up, it will force continue.
 * By default he has 10 seconds to see the card.
 */
@Deprecated
public class SeerLookingTimer extends BukkitRunnable implements Pausable {

    private final Werewolf plugin;
    private final SeerLookingCycle seerLookingCycle;
    private final SideBoardHandler sideBoardHandler;
    private boolean paused;

    //in seconds
    private long timeToLookAtCard = 10;

    public SeerLookingTimer(Werewolf plugin, SeerLookingCycle seerLookingCycle, SideBoardHandler sideBoardHandler) {
        this.plugin = plugin;
        this.seerLookingCycle = seerLookingCycle;
        this.sideBoardHandler = sideBoardHandler;
    }

    @Override
    public void run() {
        if(!isPaused()) {
            sideBoardHandler.updateCountdownTime(--timeToLookAtCard);
            if(timeToLookAtCard == 0) {
                seerLookingCycle.broadcastUnspecifiedEventMessage();
                seerLookingCycle.endCycle();
            }
        }
    }

    public void startTimer() {
        this.runTaskTimer(plugin, 0L, timeToLookAtCard*20);
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
