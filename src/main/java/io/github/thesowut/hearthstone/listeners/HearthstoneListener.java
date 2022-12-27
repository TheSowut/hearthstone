package io.github.thesowut.hearthstone.listeners;

import io.github.thesowut.hearthstone.helpers.HearthstoneHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class HearthstoneListener implements Listener {
    private final HearthstoneHelper _hearthstoneHelper;

    public HearthstoneListener(HearthstoneHelper hearthstoneHelper) {
        this._hearthstoneHelper = hearthstoneHelper;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.getPlayer().sendMessage("1");
    }

    @EventHandler
    public void onPlayer(PlayerInteractEvent event) {
        // If the player is holding the Hearthstone in the offhand, do nothing.
        if (event.getHand() != EquipmentSlot.HAND) return;
        // If the player isn't right-clicking with a Hearthstone, do nothing.
        if (!event.getItem().equals(_hearthstoneHelper.hearthstoneItem)) return;

        // TODO
        // Add cooldown to hearthstone usage
        // Add particles when using hearthstone
        // Add channeling
        // Movement or pressing again should cancel channeling
        // make item undroppable

        _hearthstoneHelper.teleportPlayer(event);
    }
}