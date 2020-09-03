package me.wand555.werewolf.conversations.prompts.armor;

import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.cycles.armor.ArmorChooseCycle;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.PlayerNamePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SelectSecondPlayerCouplePrompt extends PlayerNamePrompt {

    private final ArmorChooseCycle armorChooseCycle;

    public SelectSecondPlayerCouplePrompt(Plugin plugin, ArmorChooseCycle armorChooseCycle) {
        super(plugin);
        this.armorChooseCycle = armorChooseCycle;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Now select the second player!";
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        if(super.isInputValid(context, input)) {
            return !armorChooseCycle.getFirstPlayer().getUUID().equals(Bukkit.getPlayer(input).getUniqueId());
        }
        else {
            return false;
        }
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "You cannot select the same player!";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, Player player) {
        Player convPlayer = (Player) conversationContext.getForWhom();
        if(player.getUniqueId().equals(convPlayer.getUniqueId())) convPlayer.sendMessage("You chose yourself as the second player!");
        else convPlayer.sendMessage("You chose " + player.getName() + " as the second player!");
        armorChooseCycle.broadcastUnspecifiedEventMessage();
        armorChooseCycle.setSecondPlayer(GameMaster.getGameMaster().getPlayerCard(player.getUniqueId()));
        armorChooseCycle.endCycle();
        return END_OF_CONVERSATION;
    }

}
