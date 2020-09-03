package me.wand555.werewolf.conversations.villagers;

import me.wand555.werewolf.conversations.prompts.SelectVoteTargetPrompt;
import me.wand555.werewolf.cycles.voting.village.VillageVotingCycle;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.plugin.Plugin;

public class SayVillagerInformationPrompt extends MessagePrompt {

    private final Plugin plugin;
    private final VillageVotingCycle cycle;

    public SayVillagerInformationPrompt(Plugin plugin, VillageVotingCycle cycle) {
        this.plugin = plugin;
        this.cycle = cycle;
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        return new SelectVoteTargetPrompt(plugin, cycle);
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Select a player you wish to choose to kill or 'none' if you don't want to vote for anyone." +
                "\nYou need to agree on a single player. If you fail to agree, the player with the most votes is killed." +
                "\nIncase two player have the same amount of votes, the player is randomly selected.";
    }
}
