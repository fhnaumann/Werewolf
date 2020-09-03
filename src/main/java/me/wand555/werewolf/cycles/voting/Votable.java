package me.wand555.werewolf.cycles.voting;

import com.sun.istack.internal.Nullable;
import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.PlayerCard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Set;

/**
 * Implemented when voting across multiple players is needed and to evaluate the results.
 */
public interface Votable {

    /**
     * Called when a final target has been decided. Will mostly display resulting messages
     * and end the cycle.
     * @param finalTarget
     */
    void setFinalTargetAndComplete(PlayerCard finalTarget);

    /**
     * Handles the incoming vote and acts accordingly
     * @param voter The player that voted
     * @param target The target that the voter voted for
     */
    void computeTargetVote(PlayerCard voter, @Nullable PlayerCard target);

    /**
     * Checks how many people have decided to *not* kill a target
     * @return
     */
    int noKillVoteAmount();

    /**
     * Checks how many players have voted to kill the target
     * @param target how many votes received
     * @return amount of votes
     */
    int voteAmount(PlayerCard target);


    default void clearVotes() {
        for(PlayerCard pCard : GameMaster.getGameMaster().getCardPointer().getCards()) {
            Objective obj = pCard.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
            for(PlayerCard targetPlayerCard : GameMaster.getGameMaster().getCardPointer().getCards()) {
                Score score = obj.getScore(targetPlayerCard.getName());
                score.setScore(0);
                obj.setDisplayName("Votes");
            }
        }
    }
}
