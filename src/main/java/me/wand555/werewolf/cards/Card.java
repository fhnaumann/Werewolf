package me.wand555.werewolf.cards;

import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.PlayerCard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("Card")
public abstract class Card implements ConfigurationSerializable {

    private final Type type;
    protected final GameMaster gameMaster = GameMaster.getGameMaster();
    protected final Scoreboard scoreboard;

    public Card(Type type, Player player) {
        this.type = type;
        this.scoreboard = setUpScoreboard(player);
        initialiseTeams(player);
        player.setScoreboard(scoreboard);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type.toString());
        return map;
    }

    /**
     * The map contains the following paths: uuid; type; healpotion; killpotion;
     * @param map
     * @return
     */
    public static Card deserialize(Map<String, Object> map) {
        Player player = Bukkit.getPlayer(UUID.fromString(map.get("uuid").toString().trim()));
        Type type = Type.valueOf(map.get("type").toString());
        switch(type) {
            case ARMOR: return new ArmorCard(player);
            case SEER: return new SeerCard(player);
            case VILLAGER: return new VillagerCard(player);
            case WEREWOLF: return new WerewolfCard(player);
            case SPECTATOR: return new SpectatorCard(player);
            case WITCH: {
                WitchPotion healPotion;
                WitchPotion killPotion;
                if(map.containsKey("healpotion")) {
                    PlayerCard target = GameMaster.getGameMaster().getPlayerCard(UUID.fromString(map.get("healpotion").toString().trim()));
                    healPotion = new WitchPotion(WitchPotion.PotionType.HEAL, target);
                }
                else {
                    healPotion = new WitchPotion(WitchPotion.PotionType.HEAL);
                }
                if(map.containsKey("killpotion")) {
                    PlayerCard target = GameMaster.getGameMaster().getPlayerCard(UUID.fromString(map.get("killpotion").toString().trim()));
                    killPotion = new WitchPotion(WitchPotion.PotionType.KILL, target);
                }
                else {
                    killPotion = new WitchPotion(WitchPotion.PotionType.KILL);
                }
                return new WitchCard(player, healPotion, killPotion);
            }
        }
        return new SpectatorCard(player);
    }

    private Scoreboard setUpScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        scoreboard.registerNewObjective("ABC", "dummy", "Vote(s)")
                .setDisplaySlot(DisplaySlot.BELOW_NAME);
        return scoreboard;
    }

    public Type getType() {
        return this.type;
    }

    /**
     * Default implementation: If this method is not overriden, it will assume the following:
     * 'this' card should see spectators and villagers normal, every other card
     * will hide as a villager to him.
     * @param player
     */
    public void initialiseTeams(Player player) {
        HashMap<Type, Team> teams = setUpEmptyTeams(scoreboard);
        for(PlayerCard pCard : gameMaster.playerCards.values()) {
            if (pCard.getUUID().equals(player.getUniqueId())) continue;
            //adding all other player cards to this specific player card teams
            Type otherType = pCard.getCard().getType();
            switch (otherType) {
                case SPECTATOR:
                case VILLAGER:
                    //'this' is a seer card. spectatorCards, villagerCards should not hide anything from him
                    teams.get(otherType).addEntry(pCard.getName());
                    break;
                case ARMOR:
                case WITCH:
                case WEREWOLF:
                case SEER:
                    //'this' is a seer card. werewolfCards should hide as a villager from him
                    teams.get(Type.VILLAGER).addEntry(pCard.getName());
                    break;
            }

            //adding this card to all other card teams
            pCard.getCard().addNewPlayerToTeams(this.getType(), player);
        }
    }

    /**
     * Adds the given player to the right team "in this card".
     * Different card types handle it differently. The default implementation (if this method
     * is not overriden) is as following:
     * 'this' card should see other spectators and villagers normal, every other card
     * will hide as a villager to him.
     *
     *
     * 1.Example:
     * theirType = WEREWOLF
     * player = the player that is meant with 'their' in theirType (the new player)
     * class this method is in = VillagerCard
     * The player will be put in the villagerTeam here, because despite the player being
     * a werewolf, he should appear as a villager to them.
     *
     * 2.Example:
     * theirType = WEREWOLF
     * player = the player that is meant with 'their' in theirType (the new player)
     * class this method is in = WerewolfCard
     * The player will be put in the werewolfTeam here, because other werewolfs should see him
     * as a werewolf.
     * @param theirType
     * @param newPlayer
     */
    protected void addNewPlayerToTeams(Type theirType, Player newPlayer) {
        switch(theirType) {
            case SPECTATOR:
            case VILLAGER:
            {
                //the player holding 'this' card should see other spectators/villagers normal
                Team team = Card.getTeam(theirType, scoreboard);
                team.addEntry(newPlayer.getName());
                //Bukkit.broadcastMessage(player.getName() + ": seeing " + newPlayer.getName() + " as a " + theirType);
                break;
            }
            case WITCH:
            case ARMOR:
            case WEREWOLF:
            case SEER:
            {
                //the player holding 'this' card should see other werewolfs as villagers. That's why
                //we're adding newPlayer as a villager in 'this' card
                Team team = Card.getTeam(Type.VILLAGER, scoreboard);
                team.addEntry(newPlayer.getName());
                break;
            }
        }
    }

    protected HashMap<Type, Team> setUpEmptyTeams(Scoreboard board) {
        HashMap<Type, Team> teams = new HashMap<>();
        for(Type type : Type.values()) {
            Team team = board.registerNewTeam(type.getTeamName());
            team.setPrefix(type.getName());
            teams.put(type, team);
        }
        return teams;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public static Team getTeam(Type type, Scoreboard board) {
        return board.getTeam(type.getTeamName());
    }

    public static Card instantiateFromType(Type type, Player player) {
        switch(type) {
            case SPECTATOR: return new SpectatorCard(player);
            case VILLAGER: return new VillagerCard(player);
            case WEREWOLF: return new WerewolfCard(player);
            case SEER: return new SeerCard(player);
            case ARMOR: return new ArmorCard(player);
        }
        return new SpectatorCard(player);
    }

    public enum Type {
        SPECTATOR, VILLAGER, WEREWOLF, SEER, ARMOR, WITCH;

        /**
         * ALL OF THESE CAN BE REPLACED WITH ABSTRACT CLASS IN THE CARD CLASS!!!
         * @return
         */
        public String getTeamName() {
            switch(this) {
                case SPECTATOR: return "SpectatorTeam";
                case VILLAGER: return "VillagerTeam";
                case WEREWOLF: return "WerewolfTeam";
                case SEER: return "SeerTeam";
                case ARMOR: return "ArmorTeam";
                case WITCH: return "WitchTeam";
            }
            return "";
        }

        public String getName() {
            switch(this) {
                case SPECTATOR: return "Spectator ";
                case VILLAGER: return "Villager ";
                case WEREWOLF: return "Werewolf ";
                case SEER: return "Seer ";
                case ARMOR: return "Armor ";
                case WITCH: return "Witch ";
            }
            return "";
        }

        public int getMaxAmount() {
            switch(this) {
                case SPECTATOR:
                case VILLAGER:
                case WEREWOLF: return 0;
                case SEER:
                case WITCH:
                case ARMOR: return 1;
            }
            return Integer.MIN_VALUE;
        }
    }
}
