package me.wand555.werewolf;

import me.wand555.werewolf.cycles.Cycle;
import me.wand555.werewolf.util.DateUtil;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;

public class SideBoardHandler {

    private final Collection<PlayerCard> cards;

    private final Cycle.Phase startPhase = Cycle.Phase.NIGHT_BEGINS;

    public SideBoardHandler(Collection<PlayerCard> cards) {
        this.cards = cards;
    }

    /**
     * Mostly used when a player switches from any card to spectator due to being killed.
     * Note: Also used at first creation for everybody.
     * @param pCard
     */
    public void setUpIndividualSideBoard(PlayerCard pCard) {
        Scoreboard board = pCard.getScoreboard();
        System.out.println("BOARD: " + board);
        Objective obj = board.registerNewObjective("Sidebar", "dummy", "Game Information");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.getScore("Your role:").setScore(10);
        Team roleTeam = board.registerNewTeam("roleTeam");
        roleTeam.setPrefix(pCard.getCard().getType().getName());
        roleTeam.addEntry(ChatColor.AQUA.toString());
        obj.getScore(ChatColor.AQUA.toString()).setScore(9);

        obj.getScore(ChatColor.RED.toString()).setScore(8);

        obj.getScore("Current Phase:").setScore(7);
        Team currentPhaseTeam = board.registerNewTeam("currentPhaseTeam");
        currentPhaseTeam.setPrefix(startPhase.getDisplayName());
        currentPhaseTeam.addEntry(ChatColor.BLACK.toString());
        obj.getScore(ChatColor.BLACK.toString()).setScore(6);

        obj.getScore(ChatColor.WHITE.toString()).setScore(5);

        obj.getScore("Time left:").setScore(4);
        Team timeLeft = board.registerNewTeam("timeLeftTeam");
        timeLeft.setPrefix("-"); //change later (I hope :D) -> get start time from ArmorChoose, but the RoundCycle instance doesn't exist here
        timeLeft.addEntry(ChatColor.DARK_AQUA.toString());
        obj.getScore(ChatColor.DARK_AQUA.toString()).setScore(3);

        obj.getScore(ChatColor.YELLOW.toString()).setScore(2);

        obj.getScore("Game Time:").setScore(1);
        Team gameTimeTeam = board.registerNewTeam("gameTimeTeam");
        gameTimeTeam.setPrefix(DateUtil.formatDuration(0));
        gameTimeTeam.addEntry(ChatColor.BLUE.toString());
        obj.getScore(ChatColor.BLUE.toString()).setScore(0);
    }

    /**
     * Sets up the side board at the start of the game.
     */
    public void setUpSideBoard() {
        for(PlayerCard pCard : cards) {
            setUpIndividualSideBoard(pCard);
        }
    }

    /**
     * Called every second when the total game time increases.
     */
    public void updateGameTime(long newTime) {
        String formattedTime = DateUtil.formatDuration(newTime);
        cards.stream().forEach(pCard -> {
            //System.out.println("pCard: " + pCard);
            //System.out.println(pCard.getScoreboard().getTeam("gameTimeTeam"));

            pCard.getScoreboard().getTeam("gameTimeTeam").setPrefix(formattedTime);
        });
    }


    public void updatePhase(Cycle.Phase newPhase, int startTimeUntilOver) {
        String formattedTime = startTimeUntilOver != -1 ? DateUtil.formatNoHourDuration(startTimeUntilOver) : "-";
        cards.stream().forEach(pCard -> {
            pCard.getScoreboard().getTeam("currentPhaseTeam").setPrefix(newPhase.getDisplayName());
            pCard.getScoreboard().getTeam("timeLeftTeam").setPrefix(formattedTime);
        });
    }

    /**
     * If newTime == -1, "-" will be displayed for the duration of this phase (or until this method is called again)
     * @param newTime
     */
    public void updateCountdownTime(long newTime) {
        String formattedTime = newTime != -1 ? DateUtil.formatNoHourDuration(newTime) : "-";
        cards.stream().forEach(pCard -> pCard.getScoreboard().getTeam("timeLeftTeam").setPrefix(formattedTime));
    }
}
