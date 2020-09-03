package me.wand555.werewolf.cards;

import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.PlayerCard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class VillagerCard extends Card {

    public VillagerCard(Player player) {
        super(Type.VILLAGER, player);
    }

    /**
     * Scenarios:
     * 1. existingCard = Spectator/Villager
     *      Just adding every spectator/villager to the spectatorTeam/villageTeam from this card. Nothing to hide.
     * 2. existingCard = Werewolf
     *      The existingCard should hide as a villager, because 'this' (villagerCard) should not see werewolfs.
     *      Instead it should see werewolfs as normal villagers.
     * @param player
     */
    @Override
    public void initialiseTeams(Player player) {
        super.initialiseTeams(player);
    }
}
