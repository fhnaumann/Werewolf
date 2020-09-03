package me.wand555.werewolf;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CE implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("start")) {
            //open gui with precalculated settings
            GameMaster.getGameMaster().suggestCardSplitting(((Player)commandSender).getWorld(), Bukkit.getOnlinePlayers().size());
        }
        if(cmd.getName().equalsIgnoreCase("werewolf")) {
            Player player = (Player) commandSender;
            GameMaster.getGameMaster().addAsWerewolf(player);
        }
        if(cmd.getName().equalsIgnoreCase("villager")) {
            Player player = (Player) commandSender;
            GameMaster.getGameMaster().addAsVillager(player);
        }
        if(cmd.getName().equalsIgnoreCase("seer")) {
            Player player = (Player) commandSender;
            GameMaster.getGameMaster().addAsSeer(player);
        }
        if(cmd.getName().equalsIgnoreCase("clearscoreboard")) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            });
            GameMaster.getGameMaster().playerCards.clear();
        }
        return true;
    }
}
