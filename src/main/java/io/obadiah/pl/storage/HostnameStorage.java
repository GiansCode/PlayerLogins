package io.obadiah.pl.storage;

import com.google.common.collect.Maps;
import io.obadiah.pl.PlayerLogins;
import io.obadiah.pl.util.LoadableConfig;

import java.nio.file.Path;
import java.util.Map;

public class HostnameStorage extends LoadableConfig<HostnameStorage> {

    private static final HostnameStorage DEFAULT_STORAGE = new HostnameStorage();

    private Map<String, Integer> hostnames;

    /**
     * Represents a configuration file.
     */
    public HostnameStorage() {
        super(HostnameStorage.class);

        this.hostnames = Maps.newConcurrentMap();
    }

    public Map<String, Integer> getHostnames() {
        return this.hostnames;
    }

    @Override
    public Path getPath() {
        return PlayerLogins.get().getPath().resolve("hostnames.json");
    }

    @Override
    public HostnameStorage getDefaultConfig() {
        return DEFAULT_STORAGE;
    }
}
