package io.github.thesowut.hearthstone.commands;

import io.github.thesowut.hearthstone.handler.Hearthstone;
import io.github.thesowut.hearthstone.helpers.HearthstoneHelper;
import io.github.thesowut.hearthstone.helpers.PluginHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HearthstoneCommands implements CommandExecutor {
    public enum HSCommand {
        get,
        sethome
    }

    private final FileConfiguration _config;
    private final HearthstoneHelper _hearthstoneHelper;
    private final PluginHelper _pluginHelper;
    private final Hearthstone _main;

    public HearthstoneCommands(
            FileConfiguration config,
            HearthstoneHelper hearthstoneHelper,
            PluginHelper pluginhelper,
            Hearthstone main
    ) {
        this._config = config;
        this._hearthstoneHelper = hearthstoneHelper;
        this._pluginHelper = pluginhelper;
        this._main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            _pluginHelper.sendSenderNotPlayerMessage(sender);
            return true;
        }

        Player player = ((Player) sender).getPlayer();
        HSCommand command = HSCommand.valueOf(cmd.getName().toLowerCase());

        switch (command) {
            case get:
                if (player.getInventory().contains(_hearthstoneHelper.hearthstoneItem)) {
                    _pluginHelper.sendHearthstoneCapReachedMessage(player);
                    break;
                }

                player.getInventory().addItem(_hearthstoneHelper.hearthstoneItem);
                _pluginHelper.sendHearthstoneReceivedMessage(player);
                break;
            case sethome:
                if (!this._hearthstoneHelper.canUseHearthstone(player)) {
                    _pluginHelper.sendNotGroundedMessage(player);
                    break;
                }

                _config.set(player.getName().toLowerCase(), player.getLocation());
                _pluginHelper.sendHomeSetMessage(player);
                _main.saveConfig();
                break;
        }
        return true;
    }

    /**
     * Fetch a list of all the commands the plugin provides.
     *
     * @return commands - all plugin commands
     */
    public ArrayList<String> getCommands() {
        ArrayList<String> commands = new ArrayList<>();
        for (HearthstoneCommands.HSCommand cmd : HearthstoneCommands.HSCommand.values()) {
            commands.add(String.valueOf(cmd));
        }
        return commands;
    }
}