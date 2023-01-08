package io.obadiah.pl.common.storage;

import com.google.common.collect.Maps;
import io.obadiah.pl.common.PlayerLoginsCore;
import io.obadiah.pl.common.util.LoadableConfig;

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
        return PlayerLoginsCore.getPath().resolve("hostnames.json");
    }

    @Override
    public HostnameStorage getDefaultConfig() {
        return DEFAULT_STORAGE;
    }
}
