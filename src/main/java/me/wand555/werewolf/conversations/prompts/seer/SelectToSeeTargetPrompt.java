package me.wand555.werewolf.conversations.prompts.seer;

import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.cycles.SeerLookingCycle;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.PlayerNamePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SelectToSeeTargetPrompt extends PlayerNamePrompt {

    private final SeerLookingCycle seerLookingCycle;

    public SelectToSeeTargetPrompt(Plugin plugin, SeerLookingCycle seerLookingCycle) {
        super(plugin);
        this.seerLookingCycle = seerLookingCycle;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Select a player who's card you wish to see.";
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        if(super.isInputValid(context, input)) {
            return GameMaster.getGameMaster().getPlayerCard(Bukkit.getPlayer(input).getUniqueId()).partOfCycle();
        }
        else {
            return false;
        }
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "'" + invalidInput + "' is not a valid player";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, Player player) {
        ((Player)conversationContext.getForWhom()).sendMessage(player.getName() + " is a"
                + seerLookingCycle.lookAtName(GameMaster.getGameMaster().getPlayerCard(player.getUniqueId())));
        seerLookingCycle.endCycle();
        return END_OF_CONVERSATION;
    }
}
