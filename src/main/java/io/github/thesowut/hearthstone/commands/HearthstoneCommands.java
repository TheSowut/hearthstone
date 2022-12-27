package io.github.thesowut.hearthstone.commands;

import io.github.thesowut.hearthstone.helpers.HearthstoneHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HearthstoneCommands implements CommandExecutor {
    public HearthstoneCommands(FileConfiguration config, ItemStack hearthstone, String pluginTitle, HearthstoneHelper hearthstoneHelper) {
        this._config = config;
        this._hearthstone = hearthstone;
        this._pluginTitle = pluginTitle;
        this._hearthstoneHelper = hearthstoneHelper;
    }
    private final FileConfiguration _config;
    private final ItemStack _hearthstone;
    private final String _pluginTitle;
    private final HearthstoneHelper _hearthstoneHelper;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = ((Player) sender).getPlayer();
        String command = cmd.getName().toLowerCase();

        switch (command) {
            case "get":
                if (player.getInventory().contains(_hearthstone)) {
                    player.sendMessage(this._pluginTitle + ChatColor.RED + "You can only carry a single Hearthstone!");
                    break;
                }

                player.getInventory().addItem(_hearthstone);
                player.sendMessage(this._pluginTitle + ChatColor.GREEN + "A Hearthstone appears in your pocket!");
                break;

            case "sethome":
                if (!this._hearthstoneHelper.canUseHearthstone(player)) {
                    player.sendMessage(this._pluginTitle + ChatColor.RED + "Must be grounded to perform that!");
                    break;
                }

                _config.set(player.getName().toLowerCase(), player.getLocation());
                player.sendMessage(this._pluginTitle + ChatColor.GREEN + "You new home has been set.");
//                this._config.save();
                break;
        }

        return true;
    }
}