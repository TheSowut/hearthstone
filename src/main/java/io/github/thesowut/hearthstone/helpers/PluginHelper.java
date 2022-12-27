package io.github.thesowut.hearthstone.helpers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PluginHelper {
    public final String title = ChatColor.DARK_GRAY
            + "[" + ChatColor.DARK_GREEN
            + "HearthStone"
            + ChatColor.DARK_GRAY + "] "
            + ChatColor.WHITE;

    public void sendNullHomeMessage(Player player) {
        player.sendMessage(this.title + ChatColor.RED + "The Hearthstone doesn't lead anywhere!");
    }

    public void sendNotGroundedMessage(Player player) {
        player.sendMessage(this.title + ChatColor.RED + "Must be grounded to perform that!");
    }
}
