package me.wand555.werewolf.conversations.prompts.werewolfs;

import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.PlayerCard;
import me.wand555.werewolf.cycles.voting.werewolf.WerewolvesKillCycle;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.PlayerNamePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
@Deprecated
public class SelectedTargetPrompt extends PlayerNamePrompt {

    public static final String NO_TARGET_STRING = "none";
    public static final PlayerCard NO_TARGET_CARD = null;

    private final WerewolvesKillCycle werewolvesKillCycle;

    public SelectedTargetPrompt(Plugin plugin, WerewolvesKillCycle werewolvesKillCycle) {
        super(plugin);
        this.werewolvesKillCycle = werewolvesKillCycle;
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
            return input.equalsIgnoreCase(NO_TARGET_STRING);
        }
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "Enter a player name or 'none'";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, Player player) {
        werewolvesKillCycle.computeTargetVote(
                GameMaster.getGameMaster().getPlayerCard(((Player)conversationContext.getForWhom()).getUniqueId()),
                player == null ? NO_TARGET_CARD : GameMaster.getGameMaster().getPlayerCard(player.getUniqueId()));
        return this;
        //if the computeTargetVote determined that a target/none has been selected, the conversation
        //will be cancelled from outside
    }


}
