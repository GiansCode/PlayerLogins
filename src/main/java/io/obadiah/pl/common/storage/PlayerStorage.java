package io.obadiah.pl.common.storage;

import com.google.common.collect.Sets;
import io.obadiah.pl.common.PlayerLoginsCore;
import io.obadiah.pl.common.storage.data.PlayerData;
import io.obadiah.pl.common.util.LoadableConfig;

import java.nio.file.Path;
import java.util.Set;

public class PlayerStorage extends LoadableConfig<PlayerStorage> {

    private static final PlayerStorage DEFAULT_STORAGE = new PlayerStorage();

    private Set<PlayerData> playerData;

    /**
     * Represents a configuration file.
     */
    public PlayerStorage() {
        super(PlayerStorage.class);

        this.playerData = Sets.newConcurrentHashSet();
    }

    public Set<PlayerData> getPlayerData() {
        return this.playerData;
    }

    @Override
    public Path getPath() {
        return PlayerLoginsCore.getPath().resolve("players.json");
    }

    @Override
    public PlayerStorage getDefaultConfig() {
        return DEFAULT_STORAGE;
    }
}
