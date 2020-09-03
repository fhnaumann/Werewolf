package me.wand555.werewolf.cycles;

import me.wand555.werewolf.PlayerCard;
import me.wand555.werewolf.RoundInformation;
import me.wand555.werewolf.cycles.voting.Vote;
import me.wand555.werewolf.cycles.voting.village.VillageVotingCycle;
import me.wand555.werewolf.cycles.voting.village.VillageVotingMayorCycle;
import org.bukkit.Bukkit;

public class HandleNightEventsCycle extends Cycle {


    public HandleNightEventsCycle(MakroCycle makroCycle) {
        super(Phase.HANDLE_NIGHT_EVENTS, makroCycle);
    }

    @Override
    public void beginCycle() {
        //"make" all night events happen
        boolean noneRemoved = true;
        for(PlayerCard playerCard : makroCycle.getCardPointer().getCards()) {
            if(playerCard.isRemoveOnDay()) {
                noneRemoved = false;
                playerCard.cardDeath(makroCycle, sideBoardHandler, plugin);
                Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.getUniqueId().equals(playerCard.getUUID()))
                .forEach(player -> player.sendMessage(playerCard.getName() + "died during the night."));
            }
        }
        if(noneRemoved) {
            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage("No one died during the night."));
        }
        super.beginCycle();
        endCycle();
    }

    @Override
    public void endCycle() {
        makroCycle.getBookInformation().parseHistory(getPageReadyHistory());
        makroCycle.setToNextCycle(getNextCycle()).beginCycle();
    }

    @Override
    protected Cycle getNextCycle() {
        System.out.println("began evaluating");
        RoundInformation roundInformation = super.makroCycle.getRoundInformation();
        if(roundInformation.getMayor() == null || roundInformation.getMayor().isRemoveOnDay()) {
            //no mayor has been selected or was killed and not saved during the night
            return new VillageVotingMayorCycle(makroCycle);
        }
        if(roundInformation.getPlayerKilledByVillage() != Vote.NO_TARGET_CARD) {
            //someone died
            if(roundInformation.getPlayerKilledByVillage().isMayor()) {
                //mayor died, need to vote a new one
                roundInformation.getPlayerKilledByVillage().setMayor(false);
                return new VillageVotingMayorCycle(makroCycle);
            }

        }
        return new VillageVotingCycle(makroCycle);
    }

    @Override
    public int getStartTimeUntilOver() {
        return 0;
    }
}
