package io.obadiah.pl.util;

import io.obadiah.pl.PlayerLogins;
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
