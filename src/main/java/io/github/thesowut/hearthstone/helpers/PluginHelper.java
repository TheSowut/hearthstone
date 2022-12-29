package io.github.thesowut.hearthstone.helpers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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
     * Send a chat message to the command sender if he isn't a player (e.g. command block/console).
     *
     * @param sender - Sender invoking the command
     */
    public void sendSenderNotPlayerMessage(CommandSender sender) {
        sender.sendMessage(this.title + ChatColor.RED + "Must be a player to do that!");
    }

    /**
     * Send a chat message to the player if he tries to get a second hearthstone.
     *
     * @param player - Player using hearthstone
     */
    public void sendHearthstoneCapReachedMessage(Player player) {
        player.sendMessage(this.title + ChatColor.RED + "You can only carry a single Hearthstone!");
    }

    /**
     * Send a chat message to the player after he sets a new home.
     *
     * @param player - Player using the hearthstone
     */
    public void sendHomeSetMessage(Player player) {
        player.sendMessage(this.title + ChatColor.GREEN + "You new home has been set.");
    }

    /**
     * Send a chat message to the player after he receives a hearthstone.
     *
     * @param player - Player using hearthstone
     */
    public void sendHearthstoneReceivedMessage(Player player) {
        player.sendMessage(this.title + ChatColor.GREEN + "A Hearthstone appears in your pocket!");
    }

    /**
     * Send a chat message to the player if he's trying to use the hearthstone while it isn't ready.
     *
     * @param player - Player using hearthstone
     */
    public void sendActiveCooldownMesage(Player player) {
        player.sendMessage(this.title + ChatColor.RED + "The Hearthstone is still warm!");
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
