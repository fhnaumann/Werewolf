package me.wand555.werewolf.conversations.prompts.witch;

import com.sun.istack.internal.Nullable;
import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.PlayerCard;
import me.wand555.werewolf.RoundInformation;
import me.wand555.werewolf.cards.WitchCard;
import me.wand555.werewolf.cards.WitchPotion;
import me.wand555.werewolf.cycles.WitchChooseCycle;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.PlayerNamePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

public class SelectedPotionTargetPrompt extends PlayerNamePrompt {

    private final WitchChooseCycle witchChooseCycle;
    private final WitchPotion potion;

    public SelectedPotionTargetPrompt(Plugin plugin, WitchChooseCycle witchChooseCycle, WitchPotion potion) {
        super(plugin);
        this.witchChooseCycle = witchChooseCycle;
        this.potion = potion;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "You may use a potion to heal or kill someone.";
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        System.out.println("super method call: " + super.isInputValid(context, input));
        if(super.isInputValid(context, input)) {
            Player target = Bukkit.getPlayer(input);
            Collection<PlayerCard> cards = GameMaster.getGameMaster().getCardPointer().getCards();
            switch(potion.getPotionType()) {
                case HEAL:
                    return cards.stream().filter(PlayerCard::isRemoveOnDay).anyMatch(playerCard -> playerCard.getUUID().equals(target.getUniqueId()));
                case KILL:
                    return cards.stream()
                            .filter(playerCard -> playerCard.partOfCycle() && !playerCard.isRemoveOnDay())
                            .anyMatch(playerCard -> playerCard.getUUID().equals(target.getUniqueId()));
            }
        }
        return false;
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "'" + invalidInput + "' is not a valid player!";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, Player player) {
        switch(potion.getPotionType()) {
            case HEAL:
                witchChooseCycle.healPotionUse(GameMaster.getGameMaster().getPlayerCard(player.getUniqueId()));
            case KILL:
                witchChooseCycle.killPotionUse(GameMaster.getGameMaster().getPlayerCard(player.getUniqueId()));
        }
        witchChooseCycle.endCycle();
        return END_OF_CONVERSATION;
    }
}
