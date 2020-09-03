package me.wand555.werewolf;

import me.wand555.werewolf.cards.Card;
import me.wand555.werewolf.cards.SpectatorCard;
import me.wand555.werewolf.cycles.MakroCycle;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.EulerAngle;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@SerializableAs("PlayerCard")
public class PlayerCard implements ConfigurationSerializable {

    private final UUID uuid;
    private Card card;

    /**
     * reference for when the player is offline but his name is still needed for some action
     */
    private final String name;

    private boolean conscious;

    private boolean isMayor;
    private boolean couple;

    /**
     * A marker that indicates, that a player killed during the night should have their death effect at the beginning of the day.
     */
    private boolean removeOnDay;

    public PlayerCard(Player player, Card card) {
        this.uuid = player.getUniqueId();
        this.card = card;
        this.name = player.getName();
        this.isMayor = false;
        this.couple = false;
    }

    public PlayerCard(UUID uuid, String name, Card card, boolean isMayor, boolean couple) {
        this.uuid = uuid;
        this.card = card;
        this.name = name;
        this.isMayor = isMayor;
        this.couple = couple;
    }

    protected PlayerCard() {
        this.uuid = null;
        this.name = null;
    }

    public static PlayerCard deserialize(Map<String, Object> map) {
        UUID uuid = UUID.fromString(map.get("uuid").toString().trim());
        PlayerCard possibleExistingPlayerCard = GameMaster.getGameMaster().getCardPointer().getPlayerCard(uuid);
        if(possibleExistingPlayerCard == null) {
            Card card = (Card) map.get("card");
            String name = (String) map.get("name");
            boolean isMayor = Boolean.valueOf(map.get("ismayor").toString());
            boolean couple = Boolean.valueOf(map.get("iscouple").toString());
            return new PlayerCard(uuid, name, card, isMayor, couple);
        }
        else {
            return possibleExistingPlayerCard;
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Card getCard() {
        return this.card;
    }

    /**
     * Should only be accessed via @cardDeath internally.
     * @param card
     */
    private void setCard(Card card) {
        this.card = card;
    }

    public Scoreboard getScoreboard() {
        return this.getCard().getScoreboard();
    }

    public void cardDeath(MakroCycle makroCycle, SideBoardHandler sideBoardHandler, Werewolf plugin) {
        makroCycle.getCardPointer().removeFromVillageDayVotingPlayers(this);
        if(partOfCycle()) {
            Player player = Bukkit.getPlayer(getUUID());
            player.sendMessage("You died and are now a spectator.");
            if(!conscious) {
                final Set<PlayerCard> applicablePCards = makroCycle.getCardPointer().getCards().stream()
                        .filter(pCard -> pCard.partOfCycle())
                        .collect(Collectors.toSet());
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 10);
                applicablePCards.forEach(toShowPCard -> player.showPlayer(plugin, Bukkit.getPlayer(toShowPCard.getUUID())));
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                setConscious(true);
            }

            if(isMayor) {
                setMayor(false);
            }
            if(couple) {
                setCouple(false);
                PlayerCard couplePartner = GameMaster.getGameMaster().getCardPointer().getCouple().stream()
                        .filter(pCard -> !pCard.getUUID().equals(this.getUUID())).findFirst().get();
                couplePartner.cardDeath(makroCycle, sideBoardHandler, plugin);
            }
            setCard(new SpectatorCard(player)); //TODO handle if player is offline during death
            sideBoardHandler.setUpIndividualSideBoard(this);
        }
    }

    public boolean partOfCycle() {
        return isOnline() && !isDead() && card.getType() != Card.Type.SPECTATOR;
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }

    public boolean isDead() {
        return card.getType() == Card.Type.SPECTATOR;
    }

    /**
     * reference for when the player is offline but his name is still needed for some action
     * @return the player name associated with the card
     * @see PlayerCard#getUUID()
     */
    public String getName() {
        return name;
    }

    public boolean isMayor() {
        return isMayor;
    }

    public void setMayor(boolean mayor) {
        this.isMayor = mayor;
    }

    public boolean isCouple() {
        return couple;
    }

    public void setCouple(boolean couple) {
        this.couple = couple;
    }

    public void switchFromOnlineToOffline() {
        ArmorStand armorStand = createArmorStand(Bukkit.getPlayer(uuid));
    }

    public void switchFromOfflineToOnline(Player player) {
        //TODO
    }

    private ArmorStand createArmorStand(Player player) {
        Location loc = player.getLocation();
        ArmorStand armorStand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStand.getLocation().setDirection(GameMaster.getGameMaster().getMakroCycle().getCenterPoint().toVector());

        armorStand.setAI(false);
        armorStand.setVisible(true);
        armorStand.setCanPickupItems(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setSilent(true);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);

        EntityEquipment entityEquipment = armorStand.getEquipment();
        entityEquipment.setHelmet(createPlayerHead(player));
        ItemStack[] armor = createArmor(randomColor());
        entityEquipment.setChestplate(armor[0]);
        entityEquipment.setLeggings(armor[1]);
        entityEquipment.setBoots(armor[2]);
        entityEquipment.setItemInMainHand(new ItemStack(Material.DIRT));

        armorStand.setArms(true);

        EulerAngle leftArm = new EulerAngle(0, Math.toRadians(90), 0);


        armorStand.setLeftArmPose(leftArm);
        return armorStand;
    }

    private Color randomColor() {
        int red = ThreadLocalRandom.current().nextInt(1, 255);
        int green = ThreadLocalRandom.current().nextInt(1, 255);
        int blue = ThreadLocalRandom.current().nextInt(1, 255);
        return Color.fromRGB(red, green, blue);
    }

    private ItemStack[] createArmor(Color randomColor) {
        ItemStack[] arr = new ItemStack[3];

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        chestplateMeta.setColor(randomColor);
        chestplate.setItemMeta(chestplateMeta);
        arr[0] = chestplate;

        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
        leggingsMeta.setColor(randomColor);
        leggings.setItemMeta(leggingsMeta);
        arr[1] = leggings;

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(randomColor);
        boots.setItemMeta(bootsMeta);
        arr[2] = boots;
        return arr;
    }

    private ItemStack createPlayerHead(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
        head.setItemMeta(meta);
        return head;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uuid.toString());
        map.put("name", name);
        map.put("card", card);
        map.put("ismayor", isMayor);
        map.put("iscouple", couple);
        return map;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof UUID)) return false;
        return uuid.equals(((UUID)obj));
    }

    public boolean isConscious() {
        return conscious;
    }

    public void setConscious(boolean conscious) {
        this.conscious = conscious;
    }

    public boolean isRemoveOnDay() {
        return removeOnDay;
    }

    public void setRemoveOnDay(boolean removeOnDay) {
        this.removeOnDay = removeOnDay;
    }
}
