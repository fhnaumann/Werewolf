package me.wand555.werewolf.conversations.prompts.couple;

import me.wand555.werewolf.cycles.armor.LovedSeeCycle;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class AcknowledgeEachOtherCouplePrompt extends FixedSetPrompt {

    private final LovedSeeCycle lovedSeeCycle;

    public AcknowledgeEachOtherCouplePrompt(LovedSeeCycle lovedSeeCycle) {
        super(Bukkit.getPlayer(lovedSeeCycle.getFirstPlayer().getUUID()).getName(),
                Bukkit.getPlayer(lovedSeeCycle.getSecondPlayer().getUUID()).getName());
        this.lovedSeeCycle = lovedSeeCycle;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Write your partners name to acknowledge armors decision.";
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        return super.isInputValid(context, input) ? !((Player)context.getForWhom()).getName().equals(input) : false;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String answer) {
        Player convPlayer = (Player) conversationContext.getForWhom();
        convPlayer.sendMessage("Acknowledged your partner.");
        if(lovedSeeCycle.isFinished(Bukkit.getPlayer(answer))) {
            return new SayNewGameGoalPrompt(lovedSeeCycle);
        }
        return END_OF_CONVERSATION;
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "Type your partners name.";
    }
}
