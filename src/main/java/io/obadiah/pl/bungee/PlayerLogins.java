package io.obadiah.pl.bungee;

import io.obadiah.pl.bungee.commands.PlayerLoginsCommand;
import io.obadiah.pl.bungee.listener.JoinQuitListener;
import io.obadiah.pl.common.PlayerLoginsCore;
import io.obadiah.pl.common.storage.HostnameStorage;
import io.obadiah.pl.common.storage.PlayerStorage;
import io.obadiah.pl.common.util.IPDatabase;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class PlayerLogins extends Plugin {

    private Path path;

    private Configuration config;
    private File configFile;
    private HostnameStorage hostnameStorage;
    private PlayerStorage playerStorage;
    private static IPDatabase ipDatabase;

    @Override
    public void onEnable() {
        this.configFile = new File(this.getDataFolder(), "config.yml");
        this.saveDefaultConfig();

        path = Paths.get(this.getDataFolder().toURI());
        PlayerLoginsCore.initialise(this.getPath(), this.getLogger());

        ipDatabase = PlayerLoginsCore.getIpDatabase();

        if (System.currentTimeMillis() - this.getConfig().getLong("last-ip-db-download", 0) > 1000 * 60 * 60 * 24 * this.getConfig().getLong("days-to-update-ip-db", 30)) {
            try {
                ipDatabase.downloadDatabase(
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

        ProxyServer.getInstance().getPluginManager().registerListener(this, new JoinQuitListener());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PlayerLoginsCommand());
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

    public static IPDatabase getIpDatabase() {
        return ipDatabase;
    }

    public Configuration getConfig() {
        return config;
    }

    public void saveDefaultConfig() {
        try {
            this.saveResource("config.yml", false);
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + this.getFile().getName());
        }

        File outFile = new File(this.getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(this.getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
               this.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
