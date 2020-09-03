package me.wand555.werewolf;

import me.wand555.werewolf.cycles.daytimechange.BeginNightCycle;
import me.wand555.werewolf.cycles.daytimechange.DayTimeChangeCycle;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Runs every tick and speeds up day/night transition significantly to match the round time.
 * 12:00AM = 6000 ticks
 * 12:00PM = 18000 ticks
 */
public class ChangeSunMoonTimer extends BukkitRunnable {

    public static final long NOON = 6000L;
    public static final long MIDNIGHT = 18000L;
    public static final long TICKS_TILL_DAY_OR_NIGHT = 12000L;

    private final Werewolf plugin;
    private final DayTimeChangeCycle cycle;
    private final World world;
    private final long forwardPerTick;

    private final long amountThisRuns;
    private long counter = 0;

    public ChangeSunMoonTimer(Werewolf plugin, DayTimeChangeCycle cycle) {
        this.plugin = plugin;
        this.cycle = cycle;
        this.world = GameMaster.getGameMaster().gameWorld;
        this.amountThisRuns = cycle.getTotalTimeToNextCycle()*20;
        this.forwardPerTick = TICKS_TILL_DAY_OR_NIGHT/amountThisRuns;
    }

    @Override
    public void run() {
        world.setTime(world.getTime() + forwardPerTick);
        counter++;
        if(counter == amountThisRuns) {
            //adjust incase @forwardPerTick would be a fraction and therefore might have a slight offset
            if(cycle instanceof BeginNightCycle) world.setTime(MIDNIGHT);
            else world.setTime(NOON);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            cycle.endCycle();
        }
    }

    public void startTimer() {
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        this.runTaskTimer(plugin, 0L, 1L);
    }
}
