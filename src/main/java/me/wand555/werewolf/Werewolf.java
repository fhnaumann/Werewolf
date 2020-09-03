package me.wand555.werewolf;
import me.wand555.werewolf.cards.Card;
import me.wand555.werewolf.configs.Configuration;
import me.wand555.werewolf.cycles.ConversationCycle;
import me.wand555.werewolf.cycles.MakroCycle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

public class Werewolf extends JavaPlugin {

    private CE myCE;

    public static final Random random = new Random();


    public void onEnable() {
        myCE = new CE();
        this.getCommand("villager").setExecutor(myCE);
        this.getCommand("werewolf").setExecutor(myCE);
        this.getCommand("clearscoreboard").setExecutor(myCE);
        this.getCommand("seer").setExecutor(myCE);
        this.getCommand("start").setExecutor(myCE);
        Bukkit.getOnlinePlayers().forEach(p -> GameMaster.getGameMaster().createScoreboard(p));

        new JoinLeaveListener(this);

        ConfigurationSerialization.registerClass(PlayerCard.class);
        ConfigurationSerialization.registerClass(Card.class);

        Configuration configuration = new Configuration(this, "data", true, false);
        GameMaster.getGameMaster().setConfiguration(configuration);
        //GameMaster.getGameMaster().onStartLoad();
        /*
        Player wand555 = Bukkit.getPlayer("wand555");
        Player officialFinex = Bukkit.getPlayer("OfficialFinex");


        Scoreboard wand555Scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        wand555Scoreboard.registerNewObjective("abc", "dummy", "myScoreboardName").setDisplaySlot(DisplaySlot.BELOW_NAME);
        //System.out.println(wand555Scoreboard.getObjective(DisplaySlot.BELOW_NAME).getScore("myScoreboardName").getScore());
        //wand555Scoreboard.getObjective(DisplaySlot.BELOW_NAME).getScore("myScoreboardName").setScore(4);
        //System.out.println(wand555Scoreboard.getObjective(DisplaySlot.BELOW_NAME).getScore("myScoreboardName").getScore());

        //this changes the score under the player "OfficialFinex", visible from wand555's scoreboard (wand555's POV)
        wand555Scoreboard.getObjective(DisplaySlot.BELOW_NAME).getScore(officialFinex.getName()).setScore(7);
        wand555Scoreboard.getObjective(DisplaySlot.BELOW_NAME).getScore(wand555.getName()).setScore(10);
        System.out.println(wand555Scoreboard.getObjective(DisplaySlot.BELOW_NAME).getScore(wand555.getName()).getScore());
        Bukkit.getScheduler().runTaskLater(this, () -> {
            wand555Scoreboard.getObjective(DisplaySlot.BELOW_NAME).setDisplayName("VotesABC");
            wand555Scoreboard.getObjective(DisplaySlot.BELOW_NAME).getScore(officialFinex.getName()).setScore(12);

        }, 40L);
        wand555.setScoreboard(wand555Scoreboard);*/





    }

    public void onDisable() {
        GameMaster gameMaster = GameMaster.getGameMaster();
        MakroCycle makroCycle = gameMaster.getMakroCycle();
        if(makroCycle == null) return;
        if(makroCycle.getCurrentCycle() instanceof ConversationCycle) {
            ConversationCycle convCycle = (ConversationCycle) makroCycle.getCurrentCycle();
            convCycle.getConvs().forEach(Conversation::abandon);
            //no need to go further and store current data because
            //on stop it should reset back to the beginning of the cycle.
        }
        Collection<PlayerCard> playerCards = makroCycle.getCardPointer().getCards();
        Configuration configuration = makroCycle.getConfiguration();
        FileConfiguration cfg = configuration.getConfig();
        cfg.set("test", new ArrayList<>(playerCards));
        configuration.save();
    }
}
