package me.wand555.werewolf;

import com.sun.istack.internal.Nullable;
import me.wand555.werewolf.cards.WitchPotion;
import me.wand555.werewolf.configs.Configuration;
import me.wand555.werewolf.cycles.voting.Vote;

/**
 * This class holds all information that is needed between cycles.
 */
public class RoundInformation {

    private final Configuration configuration;

    /**
     * Armor/LovedSee related
     * Can only be null if there is/was no armor in this game.
     * As soon as an armor exists and has picked two players at the beginning, they will
     * always be stored here (also upon death).
     */
    @Nullable
    private PlayerCard firstPlayer;
    @Nullable
    private PlayerCard secondPlayer;

    /**
     * Werewolf related
     */
    @Nullable
    private PlayerCard playerKilledByWerewolfs;

    /**
     * Witch related
     */
    @Nullable
    private WitchPotion healPotion;
    @Nullable
    private WitchPotion killPotion;

    /**
     * Seer related
     */
    @Nullable
    private PlayerCard lookedAt;

    @Nullable
    private PlayerCard playerKilledByVillage;

    /**
     * Can only be null if a mayor hasn't been selected yet.
     */
    @Nullable
    private PlayerCard mayor;

    public RoundInformation(Configuration configuration) {
        this.configuration = configuration;
        return;
    }

    public RoundInformation(Configuration configuration, PlayerCard firstPlayer, PlayerCard secondPlayer, WitchPotion healPotion, WitchPotion killPotion, PlayerCard mayor) {
        this(configuration, firstPlayer, secondPlayer, null, healPotion, killPotion, null, null, mayor);
    }

    public RoundInformation(Configuration configuration, PlayerCard firstPlayer, PlayerCard secondPlayer, PlayerCard playerKilledByWerewolfs,
                            WitchPotion healPotion, WitchPotion killPotion, PlayerCard lookedAt,
                            PlayerCard playerKilledByVillage, PlayerCard mayor) {
        this.configuration = configuration;

        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.playerKilledByWerewolfs = playerKilledByWerewolfs;
        this.healPotion = healPotion;
        this.killPotion = killPotion;
        this.lookedAt = lookedAt;
        this.playerKilledByVillage = playerKilledByVillage;
        this.mayor = mayor;
    }

    public RoundInformation onNewMakroCycle() {
        return new RoundInformation(configuration, firstPlayer, secondPlayer, healPotion, killPotion, mayor);
    }

    public PlayerCard getFirstPlayer() {
        return firstPlayer;
    }

    /**
     *
     * @param firstPlayer
     */
    public void setFirstPlayer(PlayerCard firstPlayer) {
        this.firstPlayer = firstPlayer;
        firstPlayer.setCouple(true);
    }

    public PlayerCard getSecondPlayer() {
        return secondPlayer;
    }

    public void setSecondPlayer(PlayerCard secondPlayer) {
        this.secondPlayer = secondPlayer;
        secondPlayer.setCouple(true);
    }

    public PlayerCard getPlayerKilledByWerewolfs() {
        return playerKilledByWerewolfs;
    }

    /**
     * Null if the werewolfs decided to not kill anyone in the cycle
     * @param playerKilledByWerewolfs
     */
    public void setPlayerKilledByWerewolfs(@Nullable PlayerCard playerKilledByWerewolfs) {
        this.playerKilledByWerewolfs = playerKilledByWerewolfs;
        if(playerKilledByWerewolfs == Vote.NO_TARGET_CARD) return;
        playerKilledByWerewolfs.setRemoveOnDay(true);
        /*
        if(playerKilledByWerewolfs.isMayor()) {
            setMayor(null);
        }
        if(playerKilledByWerewolfs.isCouple()) {
            setFirstPlayer(null);
            setSecondPlayer(null);
        }
        */
    }

    public WitchPotion getHealPotion() {
        return healPotion;
    }

    public void setHealPotion(WitchPotion healPotion) {
        this.healPotion = healPotion;
    }

    public WitchPotion getKillPotion() {
        return killPotion;
    }

    public void setKillPotion(WitchPotion killPotion) {
        this.killPotion = killPotion;
    }

    public PlayerCard getLookedAt() {
        return lookedAt;
    }

    public void setLookedAt(PlayerCard lookedAt) {
        this.lookedAt = lookedAt;
    }

    public PlayerCard getPlayerKilledByVillage() {
        return playerKilledByVillage;
    }

    public void setPlayerKilledByVillage(PlayerCard playerKilledByVillage) {
        this.playerKilledByVillage = playerKilledByVillage;
        if(playerKilledByVillage == Vote.NO_TARGET_CARD) return;
        if(playerKilledByVillage.isMayor()) {
            setMayor(null);
        }
        if(playerKilledByVillage.isCouple()) {
            setFirstPlayer(null);
            setSecondPlayer(null);
        }
    }

    public PlayerCard getMayor() {
        return mayor;
    }

    public void setMayor(PlayerCard mayor) {
        this.mayor = mayor;
        if(mayor != null) mayor.setMayor(true);
    }
}
