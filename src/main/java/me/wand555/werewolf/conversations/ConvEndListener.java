package me.wand555.werewolf.conversations;

import me.wand555.werewolf.Werewolf;
import me.wand555.werewolf.cycles.armor.ArmorChooseCycle;
import me.wand555.werewolf.cycles.MakroCycle;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ConvEndListener implements Listener {

    private final Werewolf plugin;
    private MakroCycle makroCycle;

    public ConvEndListener(Werewolf plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onConvEndEvent(ConversationAbandonedEvent event) {
        if(event.getCanceller() instanceof ManuallyAbandonedConversationCanceller) {

        }
        switch(makroCycle.getCurrentCycle().getPhase()) {
            case ARMOR_CHOOSE:
            {
                ArmorChooseCycle armorChooseCycle = (ArmorChooseCycle) makroCycle.getCurrentCycle();
                if(event.gracefulExit()) {

                }
            }

        }
    }
}
