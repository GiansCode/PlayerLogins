package io.obadiah.pl.spigot.listener;

import io.obadiah.pl.spigot.PlayerLogins;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public interface Listener extends org.bukkit.event.Listener {

    default void startListening() {
        Bukkit.getPluginManager().registerEvents(this, PlayerLogins.get());
    }

    default void stopListening() {
        HandlerList.unregisterAll(this);
    }
}
