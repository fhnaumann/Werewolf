package me.wand555.werewolf.conversations.prompts.witch;

import me.wand555.werewolf.GameMaster;
import me.wand555.werewolf.PlayerCard;
import me.wand555.werewolf.Werewolf;
import me.wand555.werewolf.cards.WitchCard;
import me.wand555.werewolf.cycles.WitchChooseCycle;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WhichPotionUsePrompt extends FixedSetPrompt {

    private final Werewolf plugin;
    private final WitchChooseCycle witchChooseCycle;
    private final List<PlayerCard> deadByDay;

    public WhichPotionUsePrompt(Werewolf plugin, WitchChooseCycle witchChooseCycle) {
        super(new String[] {"heal", "safe", "kill", "death", "none"});
        this.plugin = plugin;
        this.witchChooseCycle = witchChooseCycle;
        this.deadByDay = GameMaster.getGameMaster().getCardPointer().getCards().stream()
                .filter(PlayerCard::partOfCycle)
                .filter(PlayerCard::isRemoveOnDay)
                .collect(Collectors.toList());
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "heal/safe for potion to heal. kill/death for potion to kill. 'none'" +
                "The dead player: " + Arrays.toString(deadByDay.toArray()); //TODO fix array display
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "Wrong input! Heal/safe for potion to heal. kill/death for potion to kill.";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String answer) {
        WitchCard card = (WitchCard) witchChooseCycle.getWitch().getCard();
        if(healPot(answer)) {
            return new SelectedPotionTargetPrompt(plugin, witchChooseCycle, card.getHealPotion());
        }
        else if(killPot(answer)) {
            return new SelectedPotionTargetPrompt(plugin, witchChooseCycle, card.getKillPotion());
        }
        else {
            witchChooseCycle.endCycle();
            return END_OF_CONVERSATION;
        }
    }

    private final boolean healPot(String answer) {
        return answer.equalsIgnoreCase("heal") || answer.equalsIgnoreCase("safe");
    }
    private final boolean killPot(String answer) {
        return answer.equalsIgnoreCase("kill") || answer.equalsIgnoreCase("death");
    }

}
