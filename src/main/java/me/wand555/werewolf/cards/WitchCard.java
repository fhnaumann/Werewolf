package me.wand555.werewolf.cards;

import me.wand555.werewolf.PlayerCard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.UUID;

public class WitchCard extends Card {

    private final WitchPotion healPotion;
    private final WitchPotion killPotion;

    public WitchCard(Player player) {
        super(Type.WITCH, player);
        this.healPotion = new WitchPotion(WitchPotion.PotionType.HEAL);
        this.killPotion = new WitchPotion(WitchPotion.PotionType.KILL);
    }

    public WitchCard(Player player, WitchPotion healPotion, WitchPotion killPotion) {
        super(Type.WITCH, player);
        this.healPotion = healPotion;
        this.killPotion = killPotion;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("healpotion", healPotion.hasUsedPotion() ? healPotion.getUsedOn().getUUID().toString() : null);
        map.put("killpotion", killPotion.hasUsedPotion() ? killPotion.getUsedOn().getUUID().toString() : null);
        return map;
    }

    public WitchPotion getHealPotion() {
        return healPotion;
    }

    public WitchPotion getKillPotion() {
        return killPotion;
    }

    public boolean hasUsedAllPotions() {
        return healPotion.hasUsedPotion() && killPotion.hasUsedPotion();
    }
}
