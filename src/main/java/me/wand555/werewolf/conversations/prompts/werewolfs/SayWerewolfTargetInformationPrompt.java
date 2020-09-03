package me.wand555.werewolf.conversations.prompts.werewolfs;

import me.wand555.werewolf.conversations.prompts.SelectVoteTargetPrompt;
import me.wand555.werewolf.cycles.voting.werewolf.WerewolvesKillCycle;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.plugin.Plugin;

public class SayWerewolfTargetInformationPrompt extends MessagePrompt {

    private final Plugin plugin;
    private final WerewolvesKillCycle werewolvesKillCycle;

    public SayWerewolfTargetInformationPrompt(Plugin plugin, WerewolvesKillCycle werewolvesKillCycle) {
        this.plugin = plugin;
        this.werewolvesKillCycle = werewolvesKillCycle;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Select a target you wish to kill by writing their name in the chat or 'none' if you don't want to kill anyone." +
                "\nYou need to agree on a single target. If you fail to agree, the target with the most votes dies." +
                "\nIncase two targets have the same amount of votes, the final target is randomly selected.";
    }


    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        return new SelectVoteTargetPrompt(plugin, werewolvesKillCycle);
    }


}
