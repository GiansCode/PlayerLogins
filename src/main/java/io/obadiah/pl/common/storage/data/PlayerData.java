package io.obadiah.pl.common.storage.data;

import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private String firstJoin;
    private String lastJoin;
    private Platform platform;
    private String lastLocation;

    /**
     * For Gson instantiation.
     */
    public PlayerData() {}

    public PlayerData(UUID uuid, String firstJoin, String lastJoin, Platform platform, String lastLocation) {
        this.uuid = uuid;
        this.firstJoin = firstJoin;
        this.lastJoin = lastJoin;
        this.lastLocation = lastLocation;
        this.platform = platform;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getFirstJoin() {
        return this.firstJoin;
    }

    public String getLastJoin() {
        return this.lastJoin;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setFirstJoin(String firstJoin) {
        this.firstJoin = firstJoin;
    }

    public void setLastJoin(String lastJoin) {
        this.lastJoin = lastJoin;
    }

    public Platform getPlatform() {
        return platform;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }
}
