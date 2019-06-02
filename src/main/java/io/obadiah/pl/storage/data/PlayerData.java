package io.obadiah.pl.storage.data;

import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private String firstJoin;
    private String lastJoin;

    /**
     * For Gson instantiation.
     */
    public PlayerData() {}

    public PlayerData(UUID uuid, String firstJoin, String lastJoin) {
        this.uuid = uuid;
        this.firstJoin = firstJoin;
        this.lastJoin = lastJoin;
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
}
