package me.wand555.werewolf.cards;

import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.PlayerCard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

public class SpectatorCard extends Card {

    public SpectatorCard(Player player) {
        super(Type.SPECTATOR, player);
        //TODO invisible to all playing players
    }

    /**
     * Adding all existing cards to this new card teams.
     * No hiding, just displaying the type they really are.
     */
    @Override
    public void initialiseTeams(Player player) {
        Bukkit.broadcastMessage(ChatColor.RED + "PRETEST");
        HashMap<Type, Team> teams = super.setUpEmptyTeams(scoreboard);
        for(PlayerCard pCard : gameMaster.playerCards.values()) {
            //adding all other player cards to this specific player card teams
            teams.get(pCard.getCard().getType()).addEntry(Bukkit.getPlayer(pCard.getUUID()).getName());
            //adding this card to all other card teams
            pCard.getCard().addNewPlayerToTeams(this.getType(), player);
        }

    }

    /**
     * Just adding them to the correct team. No hiding needed because spectators should see everything as it really is.
     * @param theirType
     * @param newPlayer
     */
    @Override
    protected void addNewPlayerToTeams(Type theirType, Player newPlayer) {
        Card.getTeam(theirType, scoreboard).addEntry(newPlayer.getName());
        //Card.getTeam(theirType, scoreboard).getEntries().forEach(Bukkit::broadcastMessage);
        //Bukkit.broadcastMessage(player.getName() + ": seeing " + newPlayer.getName() + " as a " + theirType);
    }
}
