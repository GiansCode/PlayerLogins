package io.obadiah.pl;

import io.obadiah.pl.storage.data.PlayerData;

import java.util.UUID;

public class HostnameAPI {

    public static String getFirstJoinHostname(UUID uuid) {
        return PlayerLogins.get().getPlayerStorage().getPlayerData().stream()
          .filter(data -> data.getUuid().equals(uuid))
          .map(PlayerData::getFirstJoin)
          .findFirst()
          .orElse(null);
    }

    public static String getLastJoinHostname(UUID uuid) {
        return PlayerLogins.get().getPlayerStorage().getPlayerData().stream()
          .filter(data -> data.getUuid().equals(uuid))
          .map(PlayerData::getLastJoin)
          .findFirst()
          .orElse(null);
    }

    public static int getAmountOfJoins(String hostname) {
        return PlayerLogins.get().getHostnameStorage().getHostnames().get(hostname.toLowerCase());
    }
}
