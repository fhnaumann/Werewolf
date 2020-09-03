package me.wand555.werewolf.conversations.prompts.couple;

import me.wand555.werewolf.cycles.armor.LovedSeeCycle;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

public class SayNewGameGoalPrompt extends MessagePrompt {

    private final LovedSeeCycle lovedSeeCycle;

    public SayNewGameGoalPrompt(LovedSeeCycle lovedSeeCycle) {
        this.lovedSeeCycle = lovedSeeCycle;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Your new goal is to be the last two alive." +
                "\nKeep in mind that you may not reveal yourself as the couple to others.";
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        lovedSeeCycle.endCycle();
        return END_OF_CONVERSATION;
    }
}
