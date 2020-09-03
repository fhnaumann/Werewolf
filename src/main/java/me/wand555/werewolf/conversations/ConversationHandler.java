package me.wand555.werewolf.conversations;

import me.wand555.werewolf.Werewolf;
import me.wand555.werewolf.conversations.prompts.armor.SelectFirstPlayerCouplePrompt;
import me.wand555.werewolf.conversations.prompts.couple.AcknowledgeEachOtherCouplePrompt;
import me.wand555.werewolf.conversations.prompts.mayor.SayMayorInformationPrompt;
import me.wand555.werewolf.conversations.prompts.seer.SelectToSeeTargetPrompt;
import me.wand555.werewolf.conversations.prompts.werewolfs.SayWerewolfTargetInformationPrompt;
import me.wand555.werewolf.conversations.prompts.witch.WhichPotionUsePrompt;
import me.wand555.werewolf.conversations.villagers.SayVillagerInformationPrompt;
import me.wand555.werewolf.cycles.*;
import me.wand555.werewolf.cycles.armor.ArmorChooseCycle;
import me.wand555.werewolf.cycles.armor.LovedSeeCycle;
import me.wand555.werewolf.cycles.voting.village.VillageVotingCycle;
import me.wand555.werewolf.cycles.voting.village.VillageVotingMayorCycle;
import me.wand555.werewolf.cycles.voting.werewolf.WerewolvesKillCycle;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

public class ConversationHandler {

    private final Werewolf plugin;

    public ConversationHandler(Werewolf plugin) {
        this.plugin = plugin;
    }

    /**
     * Builds the conversation
     * @param cycle at this point cycle is an instance of @{@link me.wand555.werewolf.cycles.ConversationCycle}
     * so casting is safe
     * @param player
     * @return
     */
    public Conversation buildConversation(Cycle cycle, Player player) {
        ConversationFactory cF = new ConversationFactory(plugin)
                .withLocalEcho(false)
                .thatExcludesNonPlayersWithMessage("Should never happend, please contact the author.")
                .withModality(false)
                .withTimeout(cycle.getStartTimeUntilOver()-1)
                .withPrefix((context) -> ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "GameMaster" + ChatColor.GRAY + "]" + ChatColor.RESET);
        switch(cycle.getPhase()) {
            case ARMOR_CHOOSE: return cF.withFirstPrompt(new SelectFirstPlayerCouplePrompt(plugin, (ArmorChooseCycle) cycle)).buildConversation(player);
            case LOVED_SEE: return cF.withFirstPrompt(new AcknowledgeEachOtherCouplePrompt((LovedSeeCycle) cycle)).buildConversation(player);
            case WEREWOLFS_KILL: return cF.withFirstPrompt(new SayWerewolfTargetInformationPrompt(plugin, (WerewolvesKillCycle)cycle)).buildConversation(player);
            case WITCH_CHOOSE: return cF.withFirstPrompt(new WhichPotionUsePrompt(plugin, (WitchChooseCycle) cycle)).buildConversation(player);
            case SEER_CHOOSE: return cF.withFirstPrompt(new SelectToSeeTargetPrompt(plugin, (SeerLookingCycle) cycle)).buildConversation(player);
            case VILLAGE_VOTING_MAYOR: return cF.withFirstPrompt(new SayMayorInformationPrompt(plugin, (VillageVotingMayorCycle) cycle)).buildConversation(player);
            case VILLAGE_VOTING: return cF.withFirstPrompt(new SayVillagerInformationPrompt(plugin, (VillageVotingCycle) cycle)).buildConversation(player);
        }
        //TODO implement convs
        return null;
    }
}
