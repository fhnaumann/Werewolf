package me.wand555.werewolf.conversations.prompts;

import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.cycles.voting.Votable;
import me.wand555.werewolf.cycles.voting.Vote;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.PlayerNamePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SelectVoteTargetPrompt extends PlayerNamePrompt {

    protected final Votable votable;

    public SelectVoteTargetPrompt(Plugin plugin, Votable votable) {
        super(plugin);
        this.votable = votable;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "You may change your vote.";
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        if(super.isInputValid(context, input)) {
            return GameMaster.getGameMaster().getPlayerCard(Bukkit.getPlayer(input).getUniqueId()).partOfCycle();
        }
        else {
            return input.equalsIgnoreCase(Vote.NO_TARGET_STRING);
        }
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "Enter a player name or 'none'";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, Player player) {
        votable.computeTargetVote(
                GameMaster.getGameMaster().getPlayerCard(((Player)conversationContext.getForWhom()).getUniqueId()),
                player == null ? Vote.NO_TARGET_CARD : GameMaster.getGameMaster().getPlayerCard(player.getUniqueId()));
        return this;
        //if the computeTargetVote determined that a target/none has been selected, the conversation
        //will be cancelled from outside
    }
}
