package io.obadiah.pl.spigot;

import io.obadiah.pl.common.PlayerLoginsCore;
import io.obadiah.pl.common.storage.HostnameStorage;
import io.obadiah.pl.common.storage.PlayerStorage;
import io.obadiah.pl.spigot.commands.PlayerLoginsCommand;
import io.obadiah.pl.spigot.listener.JoinQuitListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PlayerLogins extends JavaPlugin {

    private Path path;

    private HostnameStorage hostnameStorage;
    private PlayerStorage playerStorage;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        path = Paths.get(this.getDataFolder().toURI());

        PlayerLoginsCore.initialise(this.getPath(), this.getLogger());

        if (System.currentTimeMillis() - this.getConfig().getLong("last-ip-db-download", 0) > 1000 * 60 * 60 * 24 * this.getConfig().getLong("days-to-update-ip-db", 30)) {
            try {
                PlayerLoginsCore.getIpDatabase().downloadDatabase(
                        this.getConfig().getString("ip-db-url", "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-Country&license_key=YOUR_LICENSE_KEY&suffix=tar.gz"),
                        this.getConfig().getString("ip-db-license", ""));
                this.getConfig().set("last-ip-db-download", System.currentTimeMillis());
                this.saveConfig();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        try {
            PlayerLoginsCore.getIpDatabase().load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        hostnameStorage = PlayerLoginsCore.getHostnameStorage();
        playerStorage = PlayerLoginsCore.getPlayerStorage();


        new JoinQuitListener();
        this.getCommand("playerlogins").setExecutor(new PlayerLoginsCommand());
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
