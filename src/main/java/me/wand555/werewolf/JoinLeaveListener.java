package me.wand555.werewolf;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;
import java.util.stream.Collectors;

public class JoinLeaveListener implements Listener {

    private final Werewolf plugin;

    public JoinLeaveListener(Werewolf plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerCard playerCard = GameMaster.getGameMaster().getCardPointer().getPlayerCard(event.getPlayer().getUniqueId());
        if(playerCard != null) {
            if(playerCard.isConscious()) {
                final Set<PlayerCard> applicablePCards = GameMaster.getGameMaster().getCardPointer().getCards().stream()
                        .filter(pCard -> pCard.partOfCycle())
                        .collect(Collectors.toSet());
                applicablePCards.forEach(toShowPCard -> event.getPlayer().showPlayer(plugin, Bukkit.getPlayer(toShowPCard.getUUID())));
                event.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        GameMaster.getGameMaster().getCardPointer().getPlayerCard(event.getPlayer().getUniqueId()).switchFromOnlineToOffline();
    }
}
