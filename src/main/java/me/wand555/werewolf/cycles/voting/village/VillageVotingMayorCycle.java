package me.wand555.werewolf.cycles.voting.village;

import me.wand555.werewolf.cycles.Cycle;
import me.wand555.werewolf.cycles.MakroCycle;
import org.bukkit.Bukkit;

public class VillageVotingMayorCycle extends AbstractVillageVotingCycle {

    public VillageVotingMayorCycle(MakroCycle makroCycle) {
        super(Phase.VILLAGE_VOTING_MAYOR, makroCycle);
    }

    @Override
    public void endCycle() {
        makroCycle.getRoundInformation().setMayor(finalTarget);
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(finalTarget.getName() + "is the new mayor."));
        super.endCycle();
    }

    @Override
    protected Cycle getNextCycle() {
        return new VillageVotingCycle(makroCycle);
    }

    @Override
    public String getPageReadyHistory() {
        return super.getPageReadyHistory();
    }

    @Override
    public int getStartTimeUntilOver() {
        return 30;
    }

    @Override
    public void forceComplete() {
        super.forceComplete();
    }
}
