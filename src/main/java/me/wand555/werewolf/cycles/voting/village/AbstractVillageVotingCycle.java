package me.wand555.werewolf.cycles.voting.village;

import me.wand555.werewolf.PlayerCard;
import me.wand555.werewolf.cycles.ConversationCycle;
import me.wand555.werewolf.cycles.ForceCompletion;
import me.wand555.werewolf.cycles.MakroCycle;
import me.wand555.werewolf.cycles.voting.Votable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractVillageVotingCycle extends ConversationCycle implements Votable, ForceCompletion {

    protected final Set<PlayerCard> villagers;
    protected final List<VillagerVote> votes;
    protected final Map<PlayerCard, VillagerVote> villagerVotes;
    protected int noneVoteCount;
    protected final boolean hasMayor;
    protected PlayerCard finalTarget;

    public AbstractVillageVotingCycle(Phase phase, MakroCycle makroCycle) {
        super(phase, makroCycle,
                makroCycle.getCardPointer().getVillageDayVotingPlayers()
                        .stream()
                        .map(PlayerCard::getUUID)
                        .toArray(UUID[]::new));
        this.villagerVotes = makroCycle.getCardPointer().getVillageDayVotingPlayers().stream()
                .collect(Collectors.toMap(Function.identity(), pCard -> new VillagerVote(pCard.isMayor())));
        this.villagers = villagerVotes.keySet();
        this.votes = new ArrayList<>(villagerVotes.values());
        this.hasMayor = makroCycle.getRoundInformation().getMayor() != null;

    }

    @Override
    public void beginCycle() {
        System.out.println("began abstract day voting");
        System.out.println(Arrays.toString(villagers.toArray(new PlayerCard[villagers.size()])));
        makeUnconscious(villagers.toArray(new PlayerCard[villagers.size()]));
        super.beginCycle();
    }

    @Override
    public void broadcastUnspecifiedEventMessage() {

    }

    @Override
    public void broadcastEventMessage(Object[] data) {

    }

    @Override
    public void computeTargetVote(PlayerCard voter, PlayerCard target) {
        final List<Player> otherVillagers = getVillagers().stream()
                .filter(pCard -> !pCard.getUUID().equals(voter.getUUID()))
                .filter(pCard -> pCard.partOfCycle())
                .map(pCard -> Bukkit.getPlayer(pCard.getUUID()))
                .collect(Collectors.toList());
        final Player voterPlayer = Bukkit.getPlayer(voter.getUUID());
        final VillagerVote vote = villagerVotes.get(voter);

        if(target == null) {
            voterPlayer.sendMessage("You voted to select nobody.");
            otherVillagers.forEach(player -> player.sendMessage(voterPlayer.getName() + " doesn't want to kill a target."));
            vote.setTarget(VillagerVote.NO_TARGET_CARD);
            if(voter.isMayor()) {
                noneVoteCount += 2;
            }
            else {
                noneVoteCount++;
            }

            if(villagers.size() + (hasMayor ? 1 : 0) == noKillVoteAmount()) {
                villagers.forEach(playerCard -> {
                    Player player = Bukkit.getPlayer(playerCard.getUUID());
                    if(player != null) {
                        player.sendMessage("The village decided to spare somebody.");
                    }
                });
                endCycle();
                return;
            }
        }
        else {
            voterPlayer.sendMessage("Selected " + Bukkit.getPlayer(target.getUUID()).getName() + ".");
            vote.setTarget(target);
            otherVillagers.forEach(player -> player.sendMessage(voterPlayer.getName() + " votes for " + target.getName() + "."));
            //+1 because mayor needs to be taken into account
            if(villagers.size() + (hasMayor ? 1 : 0) == voteAmount(target)) {
                setFinalTargetAndComplete(target);
                return;
            }
        }
    }

    @Override
    public int noKillVoteAmount() {
        return noneVoteCount;
    }

    @Override
    public int voteAmount(PlayerCard target) {
        int voteCounter = 0;
        for(VillagerVote vote : votes) {
            if(Objects.equals(vote.getTarget(), target)) {
                voteCounter = vote.isMayorVote() ? voteCounter+2 : voteCounter+1;
            }
        }
        return voteCounter;
    }

    @Override
    public void setFinalTargetAndComplete(PlayerCard finalTarget) {
        this.finalTarget = finalTarget;
        System.out.println("final Target: " + finalTarget);
        broadcastUnspecifiedEventMessage();
        endCycle();
    }


    private PlayerCard getTargetWithHighestVotes() {
        HashMap<PlayerCard, Integer> voteAmounts = new HashMap<>();
        for(Map.Entry<PlayerCard, VillagerVote> entry : villagerVotes.entrySet()) {
            final PlayerCard target = entry.getValue().getTarget();

            voteAmounts.compute(target, (k, v) -> (v == null) ? entry.getKey().isMayor() ? 2 : 1 : entry.getKey().isMayor() ? v+2 : v+1);
        }
        Optional<Map.Entry<PlayerCard, Integer>> maxEntry = voteAmounts.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                ;
        List<PlayerCard> eligible = makroCycle.getCardPointer().getCards().stream()
                .filter(PlayerCard::partOfCycle)
                .collect(Collectors.toList());
        if(maxEntry.isPresent()) {
            return maxEntry.get().getKey();
        }
        else {
            return eligible.get(ThreadLocalRandom.current().nextInt(eligible.size()));
        }
    }

    @Override
    public void forceComplete() {
        setFinalTargetAndComplete(getTargetWithHighestVotes());
    }

    public Set<PlayerCard> getVillagers() {
        return villagers;
    }
}
