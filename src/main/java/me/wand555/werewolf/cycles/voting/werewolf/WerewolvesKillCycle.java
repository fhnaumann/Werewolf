package me.wand555.werewolf.cycles.voting.werewolf;

import com.sun.istack.internal.Nullable;
import me.wand555.werewolf.PlayerCard;
import me.wand555.werewolf.cards.Card;
import me.wand555.werewolf.cycles.daytimechange.BeginDayCycle;
import me.wand555.werewolf.cycles.ConversationCycle;
import me.wand555.werewolf.cycles.Cycle;
import me.wand555.werewolf.cycles.ForceCompletion;
import me.wand555.werewolf.cycles.MakroCycle;
import me.wand555.werewolf.cycles.SeerLookingCycle;
import me.wand555.werewolf.cycles.voting.Votable;
import me.wand555.werewolf.cycles.voting.Vote;
import me.wand555.werewolf.cycles.WitchChooseCycle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WerewolvesKillCycle extends ConversationCycle implements Votable, ForceCompletion {

    private final Set<PlayerCard> werewolves;
    private final List<WerewolfVote> votes;
    private final Map<PlayerCard, WerewolfVote> werewolfVotes;
    private int noneVoteCount;
    private PlayerCard finalTarget;

    public WerewolvesKillCycle(MakroCycle makroCycle) {
        super(Phase.WEREWOLFS_KILL, makroCycle, makroCycle.getCardPointer().getWerewolfs().stream().map(PlayerCard::getUUID).toArray(UUID[]::new));
        this.werewolfVotes = makroCycle.getCardPointer().getWerewolfs().stream()
                .filter(PlayerCard::partOfCycle)
                .collect(Collectors.toMap(Function.identity(), PlayerCard -> new WerewolfVote()));
        this.werewolves = werewolfVotes.keySet();
        this.votes = new ArrayList<>(werewolfVotes.values());
    }

    @Override
    public void beginCycle() {
        //System.out.println("before size: " + werewolves.size());
        PlayerCard[] arr = werewolves.toArray(new PlayerCard[werewolves.size()]);
        makeUnconscious(arr);
        super.beginCycle();
    }

    @Override
    public void endCycle() {
        makroCycle.getRoundInformation().setPlayerKilledByWerewolfs(finalTarget);
        makeUnconscious();
        super.endCycle();
    }

    @Override
    protected Cycle getNextCycle() {
        if(makroCycle.getCardPointer().hasWitch()) {
            return new WitchChooseCycle(makroCycle.getCardPointer().getWitch(), makroCycle);
        }
        else if(makroCycle.getCardPointer().hasSeer()) {
            return new SeerLookingCycle(makroCycle.getCardPointer().getSeer(), makroCycle);
        }
        else {
            return new BeginDayCycle(makroCycle);
        }
    }

    @Override
    public void broadcastUnspecifiedEventMessage() {
        Bukkit.broadcastMessage("The werewolfs have selected their target!");
    }

    @Override
    public void broadcastEventMessage(Object[] data) {

    }

    public Set<PlayerCard> getWerewolves() {
        return werewolves;
    }

    /**
     *
     * @param voter the werewolf who casted their vote
     * @param target the target selected
     */
    @Override
    public void computeTargetVote(PlayerCard voter, @Nullable PlayerCard target) {
        final List<Player> otherWerewolfs = getWerewolves().stream()
                .filter(pCard -> !pCard.getUUID().equals(voter.getUUID()))
                .filter(pCard -> pCard.partOfCycle())
                .map(pCard -> Bukkit.getPlayer(pCard.getUUID()))
                .collect(Collectors.toList());
        final Player voterPlayer = Bukkit.getPlayer(voter.getUUID());
        final WerewolfVote vote = werewolfVotes.get(voter);
        System.out.println("Vote: " + vote);

        if(target == Vote.NO_TARGET_CARD) {
            //target deselected or 'none' as first vote
            voterPlayer.sendMessage("You voted to select nobody.");
            otherWerewolfs.forEach(player -> player.sendMessage(voterPlayer.getName() + " doesn't want to kill a target."));
            vote.setTarget(WerewolfVote.NO_TARGET_CARD);
            noneVoteCount++;
            System.out.println("list size: " + werewolves + " none-votes: " + noKillVoteAmount());
            if(werewolves.size() == noKillVoteAmount()) {
                werewolves.forEach(playerCard -> {
                    Player player = Bukkit.getPlayer(playerCard.getUUID());
                    if(playerCard.partOfCycle()) {
                        player.sendMessage("You all decided to spare a target this round.");
                    }
                });
                endCycle();
                return;
            }

        }
        else {
            voterPlayer.sendMessage("Selected " + Bukkit.getPlayer(target.getUUID()).getName() + ".");
            vote.setTarget(target);
            otherWerewolfs.forEach(player -> player.sendMessage(voterPlayer.getName() + " wants to kill " + target.getName() + "."));
            if(werewolves.size() == voteAmount(target)) {
                setFinalTargetAndComplete(target);
                return;
            }
        }
        return;
    }

    @Override
    public int voteAmount(PlayerCard target) {
        return (int) votes.stream().filter(vote -> Objects.equals(vote.getTarget(), target)).count();
    }

    @Override
    public int noKillVoteAmount() {
        return noneVoteCount;
    }

    public PlayerCard getFinalTarget() {
        return finalTarget;
    }

    @Override
    public void setFinalTargetAndComplete(PlayerCard finalTarget) {
        this.finalTarget = finalTarget;
        broadcastUnspecifiedEventMessage();
        endCycle();
    }

    /**
     * When the time is up and the werewolfs haven't agreed on a target
     * @return the target with the most votes
     */
    private PlayerCard getTargetWithHighestVotes() {
        Map<PlayerCard, Integer> voteAmounts = new HashMap<>();
        for(Map.Entry<PlayerCard, WerewolfVote> entry : werewolfVotes.entrySet()) {
            final PlayerCard target = entry.getValue().getTarget();
            voteAmounts.compute(target, (k, v) -> (v == null) ? 1 : ++v);
        }
        Optional<Map.Entry<PlayerCard, Integer>> maxEntry = voteAmounts.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        List<PlayerCard> eligible = makroCycle.getCardPointer().getCards().stream()
                .filter(PlayerCard::partOfCycle)
                .filter(playerCard -> playerCard.getCard().getType() != Card.Type.WEREWOLF)
                .collect(Collectors.toList());
        if(maxEntry.isPresent()) {
            return maxEntry.get().getKey();
        }
        else {
            return eligible.get(ThreadLocalRandom.current().nextInt(eligible.size()));
        }
    }

    @Override
    public String getPageReadyHistory() {
        String result = super.getPageReadyHistory();
        result += "The werewolfs have selected their target!" + "\n";
        result += "Target: " + (finalTarget != null ? finalTarget.getName() : "None");
        return result;
    }

    @Override
    public int getStartTimeUntilOver() {
        return 60*5; //5min
    }

    @Override
    public void forceComplete() {
        setFinalTargetAndComplete(getTargetWithHighestVotes());
    }
}
