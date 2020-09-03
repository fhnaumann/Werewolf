package me.wand555.werewolf.cards;

import com.sun.istack.internal.Nullable;
import me.wand555.werewolf.PlayerCard;

public class WitchPotion {

    private final PotionType potionType;
    private PlayerCard usedOn;

    public WitchPotion(PotionType type) {
        this.potionType = type;
    }

    public WitchPotion(PotionType type, PlayerCard usedOn) {
        this.potionType = type;
        this.usedOn = usedOn;
    }

    public PotionType getPotionType() {
        return potionType;
    }

    public boolean hasUsedPotion() {
        return usedOn != null;
    }

    @Nullable
    public PlayerCard getUsedOn() {
        return usedOn;
    }

    /**
     * Also marks the PlayerCard.
     * @param usedOn
     */
    public void setUsedOn(PlayerCard usedOn) {
        this.usedOn = usedOn;
        usedOn.setRemoveOnDay(potionType == PotionType.KILL);
    }

    public enum PotionType {
        HEAL, KILL;
    }
}
