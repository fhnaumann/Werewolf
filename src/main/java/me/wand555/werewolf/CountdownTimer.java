package me.wand555.werewolf;

import me.wand555.werewolf.cycles.CountdownCycle;
import me.wand555.werewolf.cycles.ForceCompletion;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownTimer extends BukkitRunnable implements Pausable {

    private final Werewolf plugin;
    private final SideBoardHandler sideBoardHandler;
    private final CountdownCycle cycle;
    /**
     * depending on the phase, the time will start differently
     */
    private int timeUntilOver;
    private boolean paused;

    public CountdownTimer(Werewolf plugin, CountdownCycle cycle, int startTimeUntilOver, SideBoardHandler sideBoardHandler) {
        this.plugin = plugin;
        this.cycle = cycle;
        this.timeUntilOver = startTimeUntilOver;
        this.sideBoardHandler = sideBoardHandler;
    }

    @Override
    public void run() {
        if(!isPaused()) {
            sideBoardHandler.updateCountdownTime(--timeUntilOver);
            if(timeUntilOver == 0) {
                cycle.broadcastUnspecifiedEventMessage();
                if(cycle instanceof ForceCompletion) {
                    ((ForceCompletion) cycle).forceComplete();
                }
            }
        }

    }

    public void startTimer() {
        this.runTaskTimer(plugin, 0L, 20L);
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
