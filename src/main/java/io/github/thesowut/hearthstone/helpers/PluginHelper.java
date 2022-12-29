package io.github.thesowut.hearthstone.helpers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PluginHelper {
    public final String title = ChatColor.DARK_GRAY
            + "[" + ChatColor.DARK_GREEN
            + "HearthStone"
            + ChatColor.DARK_GRAY + "] "
            + ChatColor.WHITE;

    /**
     * Send a chat message to the player if he hasn't set a hearthstone home.
     *
     * @param player - Player using hearthstone
     */
    public void sendNullHomeMessage(Player player) {
        player.sendMessage(this.title + ChatColor.RED + "The Hearthstone doesn't lead anywhere!");
        player.sendMessage(this.title + "Use " + ChatColor.YELLOW + "/hearthstone:sethome" + ChatColor.RESET + " first.");
    }

    /**
     * Send a chat message to the player if he isn't grounded.
     *
     * @param player - Player using hearthstone
     */
    public void sendNotGroundedMessage(Player player) {
        player.sendMessage(this.title + ChatColor.RED + "Must be grounded to perform that!");
    }

    /**
     * Send a chat message to the player post teleportation.
     *
     * @param player - Player using hearthstone
     */
    public void sendTeleportationMessage(Player player, HearthstoneHelper.TeleportationState state) {
        switch (state) {
            case STARTED:
                player.sendMessage(this.title + ChatColor.GREEN + "Teleportation started, use again to cancel.");
                break;
            case SUCCESS:
                player.sendMessage(this.title + ChatColor.GOLD + (ChatColor.ITALIC + "Whoosh."));
                break;
            case CANCELED:
                player.sendMessage(this.title + ChatColor.RED + "Teleportation canceled!");
                break;
        }
    }
}
