package me.wand555.werewolf.cycles.voting;

import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.NoTargetCard;
import me.wand555.werewolf.PlayerCard;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.Objects;

public abstract class Vote {

    public static final String NO_TARGET_STRING = "none";
    /**
     * Every regular operation is unsafe.
     */
    public static final PlayerCard NO_TARGET_CARD = new NoTargetCard();

    protected PlayerCard target;


    public PlayerCard getTarget() {
        return target;
    }

    public void setTarget(PlayerCard newTarget) {
        System.out.println("oldTarget: " + (target == null ? "null" : target.getName()));
        System.out.println("newTarget: " + (newTarget == null ? "null" : newTarget.getName()));
        //if the target doesn't change dont do anything
        if(!Objects.equals(target, newTarget)) {
            //if the old target vote target was valid, decrement vote from there
            if(target != null) {
                displayVoteChangeUnderName(target, VoteOperation.DECREMENT_BY_ONE);
            }
            //if the new target vote is valid, increment vote from there
            if(newTarget != null) {
                displayVoteChangeUnderName(newTarget, VoteOperation.INCREMENT_BY_ONE);
            }
        }
        this.target = newTarget;
    }

    /**
     * VillagerVoting may override this method because the mayor has 2 votes.
     * @param targetPlayerCard
     * @param operation
     */
    protected void displayVoteChangeUnderName(PlayerCard targetPlayerCard, VoteOperation operation) {
        for(PlayerCard pCard : GameMaster.getGameMaster().getCardPointer().getCards()) {
            Objective obj = pCard.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
            Score score = obj.getScore(targetPlayerCard.getName());
            score.setScore(operation == VoteOperation.INCREMENT_BY_ONE ?
                    score.getScore()+VoteOperation.INCREMENT_BY_ONE.getAmount() : score.getScore() != 0 ?
                    score.getScore()+VoteOperation.DECREMENT_BY_ONE.getAmount() : 0);
            if(score.getScore() == 1) obj.setDisplayName("Vote");
            else obj.setDisplayName("Votes");
        }
    }

    protected enum VoteOperation {
        INCREMENT_BY_ONE(1), DECREMENT_BY_ONE(-1),
        /**
         * For when the chief casts their vote
         */
        INCREMENT_BY_TWO(2), DECREMENT_BY_TWO(-2);

        final int amount;
        VoteOperation(int amount) {
            this.amount = amount;
        }

        public int getAmount() {
            return amount;
        }
    }
}
