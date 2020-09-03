package me.wand555.werewolf.conversations.prompts.mayor;

import me.wand555.werewolf.conversations.prompts.SelectVoteTargetPrompt;
import me.wand555.werewolf.cycles.voting.village.VillageVotingMayorCycle;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.plugin.Plugin;

public class SayMayorInformationPrompt extends MessagePrompt {

    private final Plugin plugin;
    private final VillageVotingMayorCycle cycle;

    public SayMayorInformationPrompt(Plugin plugin, VillageVotingMayorCycle cycle) {
        this.plugin = plugin;
        this.cycle = cycle;
    }


    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        return new SelectVoteTargetPrompt(plugin, cycle);
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Select a player you wish to choose as mayor or 'none' if you don't want to vote for anyone." +
                "\nYou need to agree on a single player. If you fail to agree, the player with the most votes wins." +
                "\nIncase two player have the same amount of votes, the mayor is randomly selected.";
    }
}
