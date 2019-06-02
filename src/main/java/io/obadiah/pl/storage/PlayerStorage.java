package io.obadiah.pl.storage;

import com.google.common.collect.Sets;
import io.obadiah.pl.PlayerLogins;
import io.obadiah.pl.storage.data.PlayerData;
import io.obadiah.pl.util.LoadableConfig;

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
        return PlayerLogins.get().getPath().resolve("players.json");
    }

    @Override
    public PlayerStorage getDefaultConfig() {
        return DEFAULT_STORAGE;
    }
}
