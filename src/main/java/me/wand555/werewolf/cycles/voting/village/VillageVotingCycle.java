package me.wand555.werewolf.cycles.voting.village;

import me.wand555.werewolf.cycles.daytimechange.BeginNightCycle;
import me.wand555.werewolf.cycles.Cycle;
import me.wand555.werewolf.cycles.MakroCycle;
import org.bukkit.Bukkit;

public class VillageVotingCycle extends AbstractVillageVotingCycle {

    public VillageVotingCycle(MakroCycle makroCycle) {
        super(Phase.VILLAGE_VOTING, makroCycle);
    }

    @Override
    public void endCycle() {
        makroCycle.getRoundInformation().setPlayerKilledByVillage(finalTarget);
        if(finalTarget != null) {
            finalTarget.cardDeath(makroCycle, sideBoardHandler, plugin);
            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> !player.getUniqueId().equals(finalTarget.getUUID()))
                    .forEach(player -> player.sendMessage(finalTarget.getName() + "was voted to be killed by the village."));
        }
        else {
            Bukkit.broadcastMessage("No one was voted to be killed by the village");
        }

        super.endCycle();
    }

    @Override
    protected Cycle getNextCycle() {
        return new BeginNightCycle(makroCycle);
    }

    @Override
    public String getPageReadyHistory() {
        return super.getPageReadyHistory();
    }

    @Override
    public int getStartTimeUntilOver() {
        return 5*60;
    }
}
