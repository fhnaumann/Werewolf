package me.wand555.werewolf;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * This only purpose is to count up every second from the start of the game.
 */
public class GameTimer extends BukkitRunnable implements Pausable {

    private final SideBoardHandler sideBoardHandler;
    private long timeRunning;
    private boolean paused;

    public GameTimer(Werewolf plugin, SideBoardHandler sideBoardHandler) {
        this(plugin, sideBoardHandler, 0);
    }

    public GameTimer(Werewolf plugin, SideBoardHandler sideBoardHandler, long timeRunning) {
        this.sideBoardHandler = sideBoardHandler;
        this.timeRunning = timeRunning;
        this.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void run() {
        if(!isPaused()) {
            sideBoardHandler.updateGameTime(++timeRunning);
        }
    }

    public long getTimeRunning() {
        return this.timeRunning;
    }

    @Override
    public boolean isPaused() {
        return this.paused;
    }

    @Override
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
