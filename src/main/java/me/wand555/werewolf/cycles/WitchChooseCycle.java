package me.wand555.werewolf.cycles;

import me.wand555.werewolf.PlayerCard;
import me.wand555.werewolf.cards.WitchCard;
import me.wand555.werewolf.cycles.daytimechange.BeginDayCycle;
import org.bukkit.Bukkit;

import java.util.UUID;

public class WitchChooseCycle extends ConversationCycle {

    private final PlayerCard witch;

    public WitchChooseCycle(MakroCycle makroCycle) {
        this(makroCycle.getCardPointer().getWitch(), makroCycle);
    }

    public WitchChooseCycle(PlayerCard witch, MakroCycle makroCycle) {
        super(Phase.WITCH_CHOOSE, makroCycle, new UUID[] {witch.getUUID()});
        this.witch = witch;
    }

    @Override
    public void beginCycle() {
        makeUnconscious(witch);
        super.beginCycle();
    }

    @Override
    public void endCycle() {
        WitchCard witchCard = (WitchCard) witch.getCard();
        makroCycle.getRoundInformation().setHealPotion(witchCard.getHealPotion());
        makroCycle.getRoundInformation().setKillPotion(witchCard.getKillPotion());
        super.endCycle();
    }

    @Override
    protected Cycle getNextCycle() {
        if(makroCycle.getCardPointer().hasSeer()) {
            return new SeerLookingCycle(makroCycle.getCardPointer().getSeer(), makroCycle);
        }
        else {
            return new BeginDayCycle(makroCycle);
        }

    }

    public PlayerCard getWitch() {
        return witch;
    }

    public void healPotionUse(PlayerCard target) {
        ((WitchCard)witch.getCard()).getHealPotion().setUsedOn(target);
    }

    public void killPotionUse(PlayerCard target) {
        ((WitchCard)witch.getCard()).getKillPotion().setUsedOn(target);
    }

    @Override
    public void broadcastUnspecifiedEventMessage() {
        Bukkit.broadcastMessage("The witch has made their decision.");
    }

    /**
     * 0 = heal/kill
     * @param data
     */
    @Override
    public void broadcastEventMessage(Object[] data) {

    }

    @Override
    public int getStartTimeUntilOver() {
        return 60;
    }

    @Override
    public String getPageReadyHistory() {
        return super.getPageReadyHistory();
    }
}
