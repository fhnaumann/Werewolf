package me.wand555.werewolf;

import me.wand555.werewolf.cards.Card;
import org.bukkit.entity.Player;

/**
 * Every operation is unsafe.
 * Only use to mark that a player voted for >>nobody<< instead of null, because
 * a null value may also indicate that the voter didn't vote at least once.
 */
public class NoTargetCard extends PlayerCard {

    public NoTargetCard() {
        super();
    }
}
