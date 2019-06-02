package io.obadiah.pl;

import io.obadiah.pl.listener.JoinQuitListener;
import io.obadiah.pl.storage.HostnameStorage;
import io.obadiah.pl.storage.PlayerStorage;
import io.obadiah.pl.util.LoadableConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PlayerLogins extends JavaPlugin {

    private static Path path;

    private static HostnameStorage hostnameStorage;
    private static PlayerStorage playerStorage;

    @Override
    public void onEnable() {
        path = Paths.get(this.getDataFolder().toURI());

        hostnameStorage = (HostnameStorage) LoadableConfig.getByClass(HostnameStorage.class).load();
        playerStorage = (PlayerStorage) LoadableConfig.getByClass(PlayerStorage.class).load();

        new JoinQuitListener();
    }

    @Override
    public void onDisable() {
        hostnameStorage.save();
        playerStorage.save();
    }

    public Path getPath() {
        return path;
    }

    public HostnameStorage getHostnameStorage() {
        return hostnameStorage;
    }

    public PlayerStorage getPlayerStorage() {
        return playerStorage;
    }

    public static PlayerLogins get() {
        return JavaPlugin.getPlugin(PlayerLogins.class);
    }
}
