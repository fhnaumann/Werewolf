package me.wand555.werewolf.cards;

import me.wand555.werewolf.PlayerCard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.UUID;

public class ArmorCard extends Card {

    public ArmorCard(Player player) {
        super(Type.ARMOR, player);
    }

}
