package me.wand555.werewolf.cycles.daytimechange;

import me.wand555.werewolf.PlayerCard;
import me.wand555.werewolf.cycles.Cycle;
import me.wand555.werewolf.cycles.HandleNightEventsCycle;
import me.wand555.werewolf.cycles.MakroCycle;
import org.bukkit.Bukkit;

public class BeginDayCycle extends DayTimeChangeCycle {

    public BeginDayCycle(MakroCycle makroCycle) {
        super(Cycle.Phase.DAY_BEGINS, makroCycle);
    }

    @Override
    public void endCycle() {

        /*
        RoundInformation roundInformation = makroCycle.getRoundInformation();
        PlayerCard killedByWerewolves = makroCycle.getRoundInformation().getPlayerKilledByWerewolfs();
        //if werewolves killed a target
        //might be saved by witch
        if(killedByWerewolves != null && killedByWerewolves.isRemoveOnDay()) {
            //kill target from werewolves
            makroCycle.getRoundInformation().getPlayerKilledByWerewolfs().cardDeath(makroCycle, sideBoardHandler, plugin);
            Bukkit.broadcastMessage("The werewolves killed ...TODO");
        }
        else {

        }
         */
        super.endCycle();
    }

    @Override
    protected Cycle getNextCycle() {
        return new HandleNightEventsCycle(makroCycle);
    }

    @Override
    public void broadcastMessagesWithIntervals() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.broadcastMessage("Day comes by...");
        }, 20L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.broadcastMessage("The villagers start waking up and realise the werewolfs were here.");
        }, 60L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.broadcastMessage("By noon they all gather in the townhall and decide what to do.");
        }, 160L);
    }

    /**
     * This method also updates the actual deaths by setting the cards to spectator cards
     */
    @Deprecated
    public void handleNightEvents() {
        PlayerCard killedByWerewolfs = makroCycle.getRoundInformation().getPlayerKilledByWerewolfs();
        PlayerCard healPotionUsedOn = null;
        PlayerCard killPotionUsedOn = null;
        if(makroCycle.getCardPointer().hasWitch()) {
            healPotionUsedOn = makroCycle.getRoundInformation().getHealPotion().getUsedOn();
            killPotionUsedOn = makroCycle.getRoundInformation().getKillPotion().getUsedOn();
        }

        if(killedByWerewolfs == null) {
            //werewolfs havent killed anyone
            Bukkit.broadcastMessage("No one died this night.");
        }
        else {
            if(healPotionUsedOn != null) {
                //player was healed, effectively nothing changed
                Bukkit.broadcastMessage("No one died this night.");
            }
            else {
                //player died
                Bukkit.broadcastMessage(killedByWerewolfs.getName() + " died.");
                killedByWerewolfs.cardDeath(makroCycle, sideBoardHandler, plugin);
            }
        }
        if(killPotionUsedOn != null) {
            //witch has used kill potion to kill someone
            Bukkit.broadcastMessage(killPotionUsedOn.getName() + " died.");
            killPotionUsedOn.cardDeath(makroCycle, sideBoardHandler, plugin);
        }
    }

    @Override
    public int getTotalTimeToNextCycle() {
        return 8;
    }

}
