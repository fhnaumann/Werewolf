package me.wand555.werewolf.cycles;

import com.sun.istack.internal.Nullable;

/**
 * Used when an event triggered that should be written down in the book.
 */
public interface StorableBookHistory {

    /**
     * This string represents one page formatted correctly.
     * @return the string text or null if the cycle shouldn't has a page.
     */
    @Nullable
    String getPageReadyHistory();
}
