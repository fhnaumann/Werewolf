package me.wand555.werewolf.cycles;

import me.wand555.werewolf.*;
import me.wand555.werewolf.configs.Configuration;
import me.wand555.werewolf.cycles.armor.ArmorChooseCycle;
import me.wand555.werewolf.cycles.armor.LovedSeeCycle;
import me.wand555.werewolf.cycles.daytimechange.BeginDayCycle;
import me.wand555.werewolf.cycles.daytimechange.BeginNightCycle;
import me.wand555.werewolf.cycles.voting.village.VillageVotingCycle;
import me.wand555.werewolf.cycles.voting.village.VillageVotingMayorCycle;
import me.wand555.werewolf.cycles.voting.werewolf.WerewolvesKillCycle;
import me.wand555.werewolf.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Cycle implements StorableBookHistory {

    protected final MakroCycle makroCycle;
    protected final Phase phase;
    protected final Werewolf plugin;
    protected final SideBoardHandler sideBoardHandler;


    public Cycle(Phase phase, MakroCycle makroCycle) {
        this.phase = phase;
        this.plugin = makroCycle.getPlugin();
        this.makroCycle = makroCycle;
        this.sideBoardHandler = makroCycle.getSideBoardHandler();
    }

    /**
     * Starts 'this' cycle, NOT @{@link MakroCycle}
     */
    public void beginCycle() {
        Configuration configuration = makroCycle.getConfiguration();
        configuration.getConfig().set("currentphase", this.getPhase().toString());
        configuration.save();
    }

    /**
     * Ends 'this' cycle, NOT @{@link MakroCycle}.
     * Also immediately connects with the next cycle (after a random delay).
     */
    public void endCycle() {
        makroCycle.getBookInformation().parseHistory(getPageReadyHistory());
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            makroCycle.setToNextCycle(getNextCycle()).beginCycle();
        }, ThreadLocalRandom.current().nextLong(20*1, 20*2));
    }

    protected abstract Cycle getNextCycle();

    /**
     * Many cycles have a max time until it continues. It may continue earlier if the 'goal' is reached
     * before the time runs out.
     * Those that do not have a max Time will return -1 which results in "-" shown in the scoreboard.
     * @return
     */
    public abstract int getStartTimeUntilOver();

    /**
     * FORMAT:
     * PhaseName began
     *
     * ~empty~
     *
     * @return
     */
    @Override
    public String getPageReadyHistory() {
        return DateUtil.formatDuration(GameMaster.getGameMaster().getGameTimer().getTimeRunning()) +
                this.getPhase().getDisplayName() + " began" + "\n\n";
    }

    public Phase getPhase() {
        return this.phase;
    }

    /**
     * Makes every player unconscious except for the playerCards in the parameter.
     * Also excludes spectatorCards.
     * @param notUnconscious
     */
    protected void makeUnconscious(PlayerCard... notUnconscious) {
        final Set<PlayerCard> applicablePCards = makroCycle.getCardPointer().getCards().stream()
                .filter(pCard -> pCard.partOfCycle())
                .collect(Collectors.toSet());
        System.out.println("normal: " + makroCycle.getCardPointer().getCards().size() + "    " + applicablePCards.size() + "applicable !!!!");
        applicablePCards.forEach(pCard -> {
            Player player = Bukkit.getPlayer(pCard.getUUID());
            System.out.println(Arrays.toString(applicablePCards.toArray(new PlayerCard[applicablePCards.size()])));
            System.out.println(Arrays.toString(notUnconscious));
            System.out.println("contains " + pCard.getName() + ": " + containsCard(notUnconscious, pCard));
            if(!containsCard(notUnconscious, pCard)) {
                pCard.setConscious(false);
                if(!player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 10, 10);
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.BLINDNESS,
                            Integer.MAX_VALUE,
                            255,
                            false,
                            false,
                            false));
                    applicablePCards.forEach(toHidePCard -> player.hidePlayer(plugin, Bukkit.getPlayer(toHidePCard.getUUID())));
                }
            }
            else {
                pCard.setConscious(true);
                System.out.println("reached1");
                if(player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                    System.out.println("reached2");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 10);
                    applicablePCards.forEach(toShowPCard -> player.showPlayer(plugin, Bukkit.getPlayer(toShowPCard.getUUID())));
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                }
            }
        });
    }

    private boolean containsCard(PlayerCard[] arr, PlayerCard pCard) {
        return Stream.of(arr).anyMatch(playerCard -> playerCard.equals(pCard));
    }

    public static Cycle instantiateCycle(Phase phase, MakroCycle makroCycle) {
        switch(phase) {
            case ARMOR_CHOOSE: return new ArmorChooseCycle(makroCycle);
            case LOVED_SEE: return new LovedSeeCycle(makroCycle);
            case NIGHT_BEGINS: return new BeginNightCycle(makroCycle);
            case WEREWOLFS_KILL: return new WerewolvesKillCycle(makroCycle);
            case WITCH_CHOOSE: return new WitchChooseCycle(makroCycle);
            case SEER_CHOOSE: return new SeerLookingCycle(makroCycle);
            case DAY_BEGINS: return new BeginDayCycle(makroCycle);
            case HANDLE_NIGHT_EVENTS: return new HandleNightEventsCycle(makroCycle);
            case VILLAGE_VOTING_MAYOR: return new VillageVotingMayorCycle(makroCycle);
            case VILLAGE_VOTING: return new VillageVotingCycle(makroCycle);
        }
        try {
            throw new Exception("Tried instantiating invalid cycle!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Only one phase can be active at a time. The phases are in the order they should be played out.
     * {@link #ARMOR_CHOOSE}, {@link #LOVED_SEE} are only at the start of the game.
     * All other phases run as normal.
     */
    public enum Phase {

        ARMOR_CHOOSE,

        LOVED_SEE,

        /*Doctor?*/

        NIGHT_BEGINS,

        WEREWOLFS_KILL,

        WITCH_CHOOSE,

        SEER_CHOOSE,

        DAY_BEGINS,

        HANDLE_NIGHT_EVENTS,

        /**
         * Only reached on the first iteration or if the old mayor has died during the night OR through villager voting
         */
        VILLAGE_VOTING_MAYOR,

        VILLAGE_VOTING;

        /**
         * From here on it starts again at {@link #NIGHT_BEGINS}
         */



        public String getDisplayName() {
            switch(this) {
                case ARMOR_CHOOSE: return "Armor choosing ";
                case LOVED_SEE: return "Loved ones seeing each other";
                case NIGHT_BEGINS: return "Night begins";
                case WEREWOLFS_KILL: return "Werewolfs killing";
                case WITCH_CHOOSE: return "Witch using potions";
                case SEER_CHOOSE: return "Seer looking";
                case DAY_BEGINS: return "Day begins";
                case VILLAGE_VOTING_MAYOR: return "Voting mayor";
                case VILLAGE_VOTING: return "Villager voting";
                default: return "Unkown";
            }
        }


    }
}
