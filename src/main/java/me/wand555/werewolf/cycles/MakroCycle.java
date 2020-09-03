package me.wand555.werewolf.cycles;

import me.wand555.werewolf.*;
import me.wand555.werewolf.configs.Configuration;
import me.wand555.werewolf.conversations.ConversationHandler;
import me.wand555.werewolf.cycles.armor.ArmorChooseCycle;
import me.wand555.werewolf.cycles.daytimechange.BeginNightCycle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.util.Set;

public class MakroCycle {

    private final Werewolf plugin;
    private final CardPointer cardPointer;
    private Cycle currentCycle;
    private RoundInformation roundInformation;
    private final SideBoardHandler sideBoardHandler;

    //only a reference here for when a non-conv cycle is followed by conv cycle
    private final ConversationHandler convHandler;
    private final Configuration configuration;
    private final BookInformation bookInformation;

    private final Location centerPoint;

    public MakroCycle(Werewolf plugin, CardPointer cardPointer, SideBoardHandler sideBoardHandler,
                      ConversationHandler convHandler, Configuration configuration) {
        this(plugin, cardPointer, sideBoardHandler, convHandler, configuration, determineCenterPoint(cardPointer.getVillageDayVotingPlayers(), configuration));
    }

    /**
     * Starts the makroCycle with the given cycle
     * @param plugin
     * @param cardPointer
     * @param sideBoardHandler
     * @param convHandler
     */
    public MakroCycle(Werewolf plugin, CardPointer cardPointer, SideBoardHandler sideBoardHandler,
                      ConversationHandler convHandler, Configuration configuration, Location centerPoint) {
        this(plugin, cardPointer, new RoundInformation(configuration), sideBoardHandler, convHandler, configuration, centerPoint, null);
    }

    public MakroCycle(Werewolf plugin, CardPointer cardPointer, RoundInformation roundInformation, SideBoardHandler sideBoardHandler,
                      ConversationHandler convHandler, Configuration configuration, Location centerPoint) {
        this(plugin, cardPointer, roundInformation, sideBoardHandler, convHandler, configuration, centerPoint, null);
    }

    public MakroCycle(Werewolf plugin, CardPointer cardPointer, RoundInformation roundInformation, SideBoardHandler sideBoardHandler,
                      ConversationHandler convHandler, Configuration configuration, Location centerPoint, Cycle startPoint) {
        this.plugin = plugin;
        this.cardPointer = cardPointer;
        this.roundInformation = roundInformation;
        this.convHandler = convHandler;
        this.configuration = configuration;
        this.sideBoardHandler = sideBoardHandler;
        this.bookInformation = new BookInformation();
        this.centerPoint = centerPoint;
        if(startPoint != null) {
            this.currentCycle = startPoint;
        }
        else {
            if(cardPointer.hasArmor()) {
                this.currentCycle = new ArmorChooseCycle(cardPointer.getArmor(), this);
            }
            else {
                this.currentCycle = new BeginNightCycle(this);
            }
        }

    }

    public void startMakroCycle() {
        currentCycle.beginCycle();
    }

    public void startMakroCycleWith(Cycle cyle) {
        this.currentCycle = cyle;
        startMakroCycle();
    }

    /**
     * "Removes" this makroCycle and replaces it with a new one
     */
    public void endMakroCycle(Cycle startPoint) {
        MakroCycle newMakroCycle = new MakroCycle(plugin, cardPointer, roundInformation.onNewMakroCycle(), sideBoardHandler, convHandler, configuration, centerPoint, startPoint);
        GameMaster.getGameMaster().setMakroCycle(newMakroCycle);
        //newMakroCycle.startMakroCycle();
    }

    public Cycle getCurrentCycle() {
        return currentCycle;
    }


    /**
     * Sets the currentCycle to the next cycle given by the parameter.
     * Also updates the scoreboard.
     * In the occasion of triggering a new makroCycle, this class will start
     * the work to create a new @{@link MakroCycle}.
     * @param nextCycle
     * @return
     */
    public Cycle setToNextCycle(Cycle nextCycle) {
        if(nextCycle.getPhase() != Cycle.Phase.NIGHT_BEGINS) {
            sideBoardHandler.updatePhase(nextCycle.getPhase(), nextCycle.getStartTimeUntilOver());
            this.currentCycle = nextCycle;
        }
        else {
            //new makrocycle
            endMakroCycle(nextCycle);

        }
        return nextCycle;
    }

    private static Location determineCenterPoint(Set<PlayerCard> playingPlayers, Configuration configuration) {
        float totalX = 0f;
        float totalY = 0f;
        float totalZ = 0f;
        for(PlayerCard pCard : playingPlayers) {
            if(pCard.isOnline()) continue; //method run at the start, so this check should be redundant
            Location loc = Bukkit.getPlayer(pCard.getUUID()).getEyeLocation();
            totalX += loc.getX();
            totalY += loc.getY();
            totalZ += loc.getZ();
        }
        float centerX = totalX / playingPlayers.size();
        float centerY = totalY / playingPlayers.size();
        float centerZ = totalZ / playingPlayers.size();
        Location centerPoint = new Location(GameMaster.getGameMaster().gameWorld, centerX, centerY, centerZ);
        configuration.getConfig().set("centerPoint", centerPoint);
        configuration.save();
        return centerPoint;
    }

    public Location getCenterPoint() {
        return centerPoint;
    }

    public RoundInformation getRoundInformation() {
        return roundInformation;
    }

    public CardPointer getCardPointer() {
        return cardPointer;
    }

    public ConversationHandler getConvHandler() {
        return convHandler;
    }

    public SideBoardHandler getSideBoardHandler() {
        return sideBoardHandler;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public BookInformation getBookInformation() {
        return bookInformation;
    }

    public Werewolf getPlugin() {
        return plugin;
    }
}
