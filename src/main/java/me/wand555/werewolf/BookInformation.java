package me.wand555.werewolf;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class BookInformation {

    private final ItemStack book;
    private int lastWrittenPage;

    public BookInformation() {
        this.book = createEmptyBook();
        this.lastWrittenPage = 1;
    }

    public ItemStack parseHistory(String parseReadyPage) {
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.addPage(parseReadyPage);
        return book;
    }

    private ItemStack createEmptyBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.setTitle("Game History");
        bookMeta.setAuthor("GameMaster");
        bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);
        bookMeta.addPage("Game History");
        return book;
    }

}
