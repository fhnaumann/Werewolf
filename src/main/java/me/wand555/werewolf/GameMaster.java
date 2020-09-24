package me.wand555.werewolf;

import me.wand555.werewolf.cards.*;
import me.wand555.werewolf.configs.Configuration;
import me.wand555.werewolf.conversations.ConversationHandler;
import me.wand555.werewolf.cycles.CountdownCycle;
import me.wand555.werewolf.cycles.Cycle;
import me.wand555.werewolf.cycles.MakroCycle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class GameMaster {

    private static GameMaster gameMaster;

    private static final Werewolf PLUGIN = Werewolf.getPlugin(Werewolf.class);

    public HashMap<UUID, PlayerCard> playerCards = new HashMap<>();

    /**
     * Keeps track of who has what card. Dead players are **not** removed.
     */
    private CardPointer cardPointer;

    private SideBoardHandler sideBoardHandler;

    private ConversationHandler convHandler;

    /**
     * The current makroCycle
     */
    private MakroCycle makroCycle;
    /**
     * Timers
     */
    private GameTimer gameTimer;

    private Configuration configuration;

    public World gameWorld;

    private GameMaster() {}


    public PlayerCard getPlayerCard(UUID uuid) {
        PlayerCard pCard = playerCards.get(uuid);
        if(pCard == null) Bukkit.broadcastMessage(ChatColor.DARK_RED + "Major error. Contact author with console log!");
        return pCard;
    }

    /**
     * Suggests how the cards should be split and also gives every player a random card.
     * @param playerAmount
     */
    public void suggestCardSplitting(World world, int playerAmount) {
        HashMap<Card.Type, Integer> splits = new CardsSplitter(playerAmount).getSuggestedSplits();
        //usually display GUI and allow user modifications... -> later feature, for now these splits are set

        //override the current playerCards with randomized maps.
        //playerCards.putAll(toPlayerRandomizedMapped(splits)); CHANGE BACK AFTER TESTING TODO
        Player player1 = Bukkit.getPlayer("wand555");
        playerCards.replace(player1.getUniqueId(), new PlayerCard(player1, new ArmorCard(player1)));
        Player player2 = Bukkit.getPlayer("OfficialFinex");
        playerCards.replace(player2.getUniqueId(), new PlayerCard(player2, new WerewolfCard(player2)));
        startGame(world);
    }

    /**
     * Called by @suggestCardSplitting
     * @param splits
     * @return
     */
    private HashMap<UUID, PlayerCard> toPlayerRandomizedMapped(HashMap<Card.Type, Integer> splits) {
        HashMap<UUID, PlayerCard> newPlayerCards = new HashMap<>();
        for(Map.Entry playerCardsEntry : playerCards.entrySet()) {
            newPlayerCards.put((UUID) playerCardsEntry.getKey(), getRandomFromSplitsMap(splits, (UUID) playerCardsEntry.getKey()));
        }
        return newPlayerCards;
    }

    private PlayerCard getRandomFromSplitsMap(HashMap<Card.Type, Integer> splits, UUID uuid) {
        final List<Card.Type> keysAsArray = new ArrayList<>(splits.keySet());
        final Player player = Bukkit.getPlayer(uuid);
        Card.Type type = keysAsArray.get(Werewolf.random.nextInt(keysAsArray.size()));
        PlayerCard newPlayerCard = new PlayerCard(player, Card.instantiateFromType(type, player));
        Integer amount = splits.get(type);
        if(amount-1 == 0) splits.remove(type);
        else splits.put(type, amount-1);
        return newPlayerCard;
    }

    /**
     * Called when the game should start. Will set up scoreboard on the side and start the timer.
     *
     */
    public void startGame(World world) {
        gameWorld = world;

        //add a new objective to every scoreboard that has a global timer (duration passed since start)
        sideBoardHandler = new SideBoardHandler(playerCards.values());
        sideBoardHandler.setUpSideBoard();

        //keep track of who is who
        cardPointer = new CardPointer(playerCards.values());

        //set up conversation handler so conversations can start
        convHandler = new ConversationHandler(PLUGIN);

        //begin game timer
        gameTimer = new GameTimer(PLUGIN, sideBoardHandler);

        //begin the cycle
        makroCycle = new MakroCycle(PLUGIN, cardPointer, sideBoardHandler, convHandler, configuration);
        makroCycle.startMakroCycle();
    }

    public void pauseGame() {
        if(makroCycle.getCurrentCycle() instanceof CountdownCycle) {
            gameTimer.setPaused(true);
            ((CountdownCycle) makroCycle.getCurrentCycle()).getCountdownTimer().setPaused(true);
        }
        else {

        }
    }

    public void resumeGame() {
        gameTimer.setPaused(false);
        ((CountdownCycle) makroCycle.getCurrentCycle()).getCountdownTimer().setPaused(false);
    }

    public void onStartLoad() {
        Set<PlayerCard> playerCards;
        if(configuration.getConfig().getList("test") != null) {
            playerCards = configuration.getConfig().getList("test").stream()
                    .map(string -> (PlayerCard) string)
                    .collect(Collectors.toSet());
        }
        else {
            playerCards = new HashSet<>();
        }
        cardPointer = new CardPointer(playerCards);
        sideBoardHandler = new SideBoardHandler(cardPointer.getCards());
        sideBoardHandler.setUpSideBoard();
        convHandler = new ConversationHandler(PLUGIN);
        gameTimer = new GameTimer(PLUGIN, sideBoardHandler, configuration.getConfig().getLong("time"));
        WitchPotion healPotion = null;
        WitchPotion killPotion = null;
        if(cardPointer.hasWitch()) {
            healPotion = ((WitchCard)cardPointer.getWitch().getCard()).getHealPotion();
            killPotion = ((WitchCard)cardPointer.getWitch().getCard()).getKillPotion();
        }
        PlayerCard mayor = playerCards.stream().filter(PlayerCard::isMayor).findFirst().orElse(null);
        RoundInformation roundInformation = new RoundInformation(configuration, cardPointer.getCouple().get(0), cardPointer.getCouple().get(1),
                healPotion, killPotion, mayor);
        Location centerPoint = configuration.getConfig().getLocation("centerPoint");
        makroCycle = new MakroCycle(PLUGIN, cardPointer, roundInformation, sideBoardHandler, convHandler, configuration, centerPoint);
        if(configuration.getConfig().get("currentphase") != null) {
            makroCycle.startMakroCycleWith(Cycle.instantiateCycle(Cycle.Phase.valueOf((String) configuration.getConfig().get("currentphase")), makroCycle));
        }
        else {
            makroCycle.startMakroCycleWith(Cycle.instantiateCycle(Cycle.Phase.NIGHT_BEGINS, makroCycle));
        }

    }

    public void onDisableStop(Configuration configuration) {

    }

    /**
     * Call this at the on start/player join
     * @param player
     */
    public void addAsSpectator(Player player) {
        playerCards.put(player.getUniqueId(), new PlayerCard(player, new SpectatorCard(player)));
    }


    public void addAsVillager(Player player) {
        playerCards.replace(player.getUniqueId(), new PlayerCard(player, new VillagerCard(player)));
    }

    public void addAsWerewolf(Player player) {
        playerCards.replace(player.getUniqueId(), new PlayerCard(player, new WerewolfCard(player)));
    }

    public void addAsSeer(Player player) {
        playerCards.replace(player.getUniqueId(), new PlayerCard(player, new SeerCard(player)));
    }

    public void createScoreboard(Player player) {
        addAsSpectator(player);
    }

    public CardPointer getCardPointer() {
        return cardPointer;
    }

    /**
     * Gets the current makro cycle.
     * @return
     */
    public MakroCycle getMakroCycle() {
        return makroCycle;
    }

    public void setMakroCycle(MakroCycle makroCycle) {
        this.makroCycle = makroCycle;
    }

    public GameTimer getGameTimer() {
        return gameTimer;
    }

    public SideBoardHandler getSideBoardHandler() {
        return sideBoardHandler;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public static GameMaster getGameMaster() {
        if(gameMaster == null) gameMaster = new GameMaster();
        return gameMaster;
    }
}
