package me.wand555.werewolf.cycles;

import me.wand555.werewolf.conversations.ConversationHandler;
import me.wand555.werewolf.cycles.voting.Votable;
import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Every ConversationCycle also have a @{@link CountdownCycle} because (nearly) every cycle involving a
 * conversation, also rely on user input, which requires a countdown.
 */
public abstract class ConversationCycle extends CountdownCycle {

    protected final ConversationHandler convHandler;
    protected List<Conversation> convs;
    private final UUID[] forWhoms;


    public ConversationCycle(Phase phase, MakroCycle makroCycle, UUID[] forWhoms) {
        super(phase, makroCycle);
        this.convHandler = makroCycle.getConvHandler();
        this.forWhoms = forWhoms;
    }

    @Override
    public void beginCycle() {
        beginConversations();
        super.beginCycle();
    }

    @Override
    public void endCycle() {
        convs.forEach(Conversation::abandon);
        if(this instanceof Votable) ((Votable) this).clearVotes();
        super.endCycle();
    }

    protected void beginConversations() {
        System.out.println("this: " + this);
        this.convs = Stream.of(forWhoms)
                .map(uuid -> convHandler.buildConversation(this, Bukkit.getPlayer(uuid)))
                .collect(Collectors.toList());
        convs.stream().peek(conversation -> System.out.println(conversation.getForWhom())).forEach(Conversation::begin);
    }

    /**
     * Checks if the conversation with the specified player has finished (is no longer in the list).
     * Often the conversations need to know the state of other conversations, but only know the name of the player
     * involved in the other conversation.
     * FYI: Every conversation that terminates (gracefully or not) will have its ConversationState set to abandon.
     * @param otherPlayer
     * @return
     */
    public boolean isFinished(Player otherPlayer) {
        return convs.stream()
                .filter(conv -> ((Player)conv.getForWhom()).getUniqueId().equals(otherPlayer.getUniqueId()))
                .anyMatch(conv -> conv.getState() == Conversation.ConversationState.ABANDONED);
    }

    public List<Conversation> getConvs() {
        return convs;
    }

}
