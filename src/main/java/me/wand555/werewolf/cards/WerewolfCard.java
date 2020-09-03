package me.wand555.werewolf.cards;

import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.PlayerCard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

public class WerewolfCard extends Card {

    public WerewolfCard(Player player) {
        super(Type.WEREWOLF, player);
    }

    @Override
    public void initialiseTeams(Player player) {
        HashMap<Type, Team> teams = super.setUpEmptyTeams(scoreboard);
        for(PlayerCard pCard : gameMaster.playerCards.values()) {
            if(pCard.getUUID().equals(player.getUniqueId())) continue;
            //adding all other player cards to this specific player card teams
            Type otherType = pCard.getCard().getType();
            switch (otherType) {
                case SPECTATOR:
                case VILLAGER:
                case WEREWOLF:
                    //'this' is a werewolf card. spectatorCards/villagerCards/werewolfCards should not hide anything from him
                    teams.get(otherType).addEntry(Bukkit.getPlayer(pCard.getUUID()).getName());
                    break;
                case SEER:
                case WITCH:
                case ARMOR:
                    //'this' is a werewolf card. seerCards should hide as a villager from him
                    teams.get(Type.VILLAGER).addEntry(Bukkit.getPlayer(pCard.getUUID()).getName());
            }
            //adding this card to all other card teams
            pCard.getCard().addNewPlayerToTeams(this.getType(), player);
        }
    }

    @Override
    protected void addNewPlayerToTeams(Type theirType, Player newPlayer) {
        switch(theirType) {
            case SPECTATOR:
            case VILLAGER:
            case WEREWOLF:
            {
                System.out.println("For: " + newPlayer.getName());
                //A werewolf card should see other villagers/werewolfs
                Team team = Card.getTeam(theirType, scoreboard);
                team.addEntry(newPlayer.getName());
                break;
            }
            case WITCH:
            case ARMOR:
            case SEER:
            {
                //the player holding 'this' card should see other seers as villagers.
                Team team = Card.getTeam(Type.VILLAGER, scoreboard);
                team.addEntry(newPlayer.getName());
            }
        }
    }
}
