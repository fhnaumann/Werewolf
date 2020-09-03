package me.wand555.werewolf.conversations.prompts.armor;

import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.cycles.armor.ArmorChooseCycle;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.PlayerNamePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SelectFirstPlayerCouplePrompt extends PlayerNamePrompt {

    private final Plugin plugin;
    private final ArmorChooseCycle armorChooseCycle;

    public SelectFirstPlayerCouplePrompt(Plugin plugin, ArmorChooseCycle armorChooseCycle) {
        super(plugin);
        this.plugin = plugin;
        this.armorChooseCycle = armorChooseCycle;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Select two players you want to engange each other." +
                "\nStart by writing the first players name.";
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
        return "'" + invalidInput + "' is not a valid player!";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, Player player) {
        Player convPlayer = (Player) conversationContext.getForWhom();
        if(player.getUniqueId().equals(convPlayer.getUniqueId())) convPlayer.sendMessage("You chose yourself as the first player!");
        else convPlayer.sendMessage("You chose " + player.getName() + " as the first player!");
        armorChooseCycle.setFirstPlayer(GameMaster.getGameMaster().getPlayerCard(player.getUniqueId()));
        return new SelectSecondPlayerCouplePrompt(plugin, armorChooseCycle);
    }
}
