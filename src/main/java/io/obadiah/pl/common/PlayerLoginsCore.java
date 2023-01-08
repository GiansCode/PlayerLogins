package io.obadiah.pl.common;

import io.obadiah.pl.common.storage.HostnameStorage;
import io.obadiah.pl.common.storage.PlayerStorage;
import io.obadiah.pl.common.storage.data.PlayerData;
import io.obadiah.pl.common.util.IPDatabase;
import io.obadiah.pl.common.util.LoadableConfig;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerLoginsCore {

    private static PlayerStorage playerStorage;
    private static HostnameStorage hostnameStorage;
    private static IPDatabase ipDatabase;
    private static Path path;
    private static Logger logger;

    public static void initialise(Path pluginPath, Logger pluginLogger) {
        path = pluginPath;
        logger = pluginLogger;
        hostnameStorage = LoadableConfig.getByClass(HostnameStorage.class).load();
        playerStorage = LoadableConfig.getByClass(PlayerStorage.class).load();
        ipDatabase = new IPDatabase(new File(path.toFile(), "ip-database.mmdb"));
    }

    public static String getFirstJoinHostname(UUID uuid) {
        return playerStorage.getPlayerData().stream()
          .filter(data -> data.getUuid().equals(uuid))
          .map(PlayerData::getFirstJoin)
          .findFirst()
          .orElse(null);
    }

    public static String getLastJoinHostname(UUID uuid) {
        return playerStorage.getPlayerData().stream()
          .filter(data -> data.getUuid().equals(uuid))
          .map(PlayerData::getLastJoin)
          .findFirst()
          .orElse(null);
    }

    public static int getAmountOfJoins(String hostname) {
        return hostnameStorage.getHostnames().get(hostname.toLowerCase());
    }

    public static Path getPath() {
        return path;
    }

    public static HostnameStorage getHostnameStorage() {
        return hostnameStorage;
    }

    public static PlayerStorage getPlayerStorage() {
        return playerStorage;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static IPDatabase getIpDatabase() {
        return ipDatabase;
    }
}
