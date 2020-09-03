package me.wand555.werewolf.cycles.voting.village;

import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.PlayerCard;
import me.wand555.werewolf.cycles.voting.Vote;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.Objects;

public class VillagerVote extends Vote {

    private boolean isMayorVote;

    public VillagerVote(boolean isMayorVote) {
        this.isMayorVote = isMayorVote;
    }

    @Override
    public void setTarget(PlayerCard newTarget) {
        if(!Objects.equals(target, newTarget)) {
            //if the old target vote target was valid, decrement vote from there
            if(target != null) {
                if(isMayorVote) {
                    displayVoteChangeUnderName(target, VoteOperation.DECREMENT_BY_TWO);
                }
                else {
                    displayVoteChangeUnderName(target, VoteOperation.DECREMENT_BY_ONE);
                }
            }
            //if the new target vote is valid, increment vote from there
            if(newTarget != null) {
                if(isMayorVote) {
                    displayVoteChangeUnderName(newTarget, VoteOperation.INCREMENT_BY_TWO);
                }
                else {
                    displayVoteChangeUnderName(newTarget, VoteOperation.INCREMENT_BY_ONE);
                }
            }
        }
        super.target = newTarget;
    }

    /**
     * Adjusted so mayor votes are displayed correctly.
     * @param targetPlayerCard
     * @param operation
     */
    @Override
    protected void displayVoteChangeUnderName(PlayerCard targetPlayerCard, Vote.VoteOperation operation) {
        for(PlayerCard pCard : GameMaster.getGameMaster().getCardPointer().getCards()) {
            Objective obj = pCard.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
            Score score = obj.getScore(targetPlayerCard.getName());
            int currentVoteAmount = score.getScore();
            switch(operation) {
                case INCREMENT_BY_ONE:
                case INCREMENT_BY_TWO:
                    score.setScore(currentVoteAmount+operation.getAmount());
                    break;
                case DECREMENT_BY_ONE:
                case DECREMENT_BY_TWO:
                    int futureAmount = currentVoteAmount+operation.getAmount();
                    score.setScore(futureAmount >= 0 ? futureAmount : 0);
                    break;
            }
            if(score.getScore() == 1) obj.setDisplayName("Vote");
            else obj.setDisplayName("Votes");
        }
    }

    public boolean isMayorVote() {
        return isMayorVote;
    }
}
