package me.wand555.werewolf.cycles.daytimechange;

import me.wand555.werewolf.cycles.Cycle;
import me.wand555.werewolf.cycles.MakroCycle;
import me.wand555.werewolf.cycles.voting.werewolf.WerewolvesKillCycle;
import org.bukkit.Bukkit;

public class BeginNightCycle extends DayTimeChangeCycle {

    public BeginNightCycle(MakroCycle makroCycle) {
        super(Cycle.Phase.NIGHT_BEGINS, makroCycle);
        super.toDisplayAfterDelay.add("Night settles in...");
        super.toDisplayAfterDelay.add("The villagers get tired and start going to bed.");
        super.toDisplayAfterDelay.add("By midnight, all villagers are asleep.");
    }

    @Override
    protected Cycle getNextCycle() {
        return new WerewolvesKillCycle(makroCycle);
    }

    @Override
    public int getTotalTimeToNextCycle() {
        return 4;
    }

    @Override
    public void broadcastMessagesWithIntervals() {
        for(int index = 0, delay = 20; index<super.toDisplayAfterDelay.size(); index++, delay+=20) {
            int finalIndex = index;
            Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.broadcastMessage(super.toDisplayAfterDelay.get(finalIndex)), delay);
        }
    }

    /**
     * FORMAT:
     * PhaseName began
     *
     *
     * @return
     */
    @Override
    public String getPageReadyHistory() {
        String result = super.getPageReadyHistory();
        for(String fromList : super.toDisplayAfterDelay) {
            result += fromList.concat("\n");
        }
        return result;
    }
}
