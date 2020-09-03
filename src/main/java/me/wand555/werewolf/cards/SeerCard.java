package me.wand555.werewolf.cards;

import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.PlayerCard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

public class SeerCard extends Card {

    public SeerCard(Player player) {
        super(Type.SEER, player);
    }
    
}
