package me.wand555.werewolf.cycles.daytimechange;

import me.wand555.werewolf.ChangeSunMoonTimer;
import me.wand555.werewolf.cycles.Cycle;
import me.wand555.werewolf.cycles.MakroCycle;

import java.util.ArrayList;
import java.util.List;

public abstract class DayTimeChangeCycle extends Cycle {

    protected final ChangeSunMoonTimer sunMoonTimer;
    protected final List<String> toDisplayAfterDelay;

    public DayTimeChangeCycle(Phase phase, MakroCycle makroCycle) {
        super(phase, makroCycle);
        sunMoonTimer = new ChangeSunMoonTimer(plugin, this);
        this.toDisplayAfterDelay = new ArrayList<>();
    }

    @Override
    public void beginCycle() {
        makeUnconscious();
        broadcastMessagesWithIntervals();
        sunMoonTimer.startTimer();
    }

    @Override
    public void endCycle() {
        sunMoonTimer.cancel();
        super.endCycle();
    }

    /**
     * Unlike the rest of the plugin, in this methos anonymus inner bukkit runnables will be used.
     * The exact state should not be stored on reload/stop/crash. Incase previous said happens,
     * the method will run from the start again.
     */
    public abstract void broadcastMessagesWithIntervals();

    /**
     *
     * @return in seconds
     */
    public abstract int getTotalTimeToNextCycle();

    /**
     * "Ending" this method here. It doesn't have a use in any class extending this class.
     * The reason behind that is, that this method sets the time that will be displayed in the sidebar
     * and changing the time should not be displayed there (though internally it obviously has a time
     * until 'this' cycle is over)
     * @see DayTimeChangeCycle#getTotalTimeToNextCycle()
     * @return useless data
     */
    @Override
    public int getStartTimeUntilOver() {
        return -1;
    }
}
