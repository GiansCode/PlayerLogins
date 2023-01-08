package io.obadiah.pl.bungee.listener;

import io.obadiah.pl.common.PlayerLoginsCore;
import io.obadiah.pl.common.storage.data.Platform;
import io.obadiah.pl.common.storage.data.PlayerData;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.geysermc.floodgate.api.FloodgateApi;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.UUID;

public class JoinQuitListener implements Listener {

    private boolean floodgate;

    public JoinQuitListener() {
        try {
            FloodgateApi.getInstance();
            floodgate = true;
        } catch (NoClassDefFoundError e) {
            floodgate = false;
        }
    }

    private Platform getPlatform(UUID player) {
        if (!floodgate)
            return Platform.JAVA;
        return FloodgateApi.getInstance().isFloodgatePlayer(player) ? Platform.BEDROCK : Platform.JAVA;
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        InetSocketAddress address = event.getPlayer().getPendingConnection().getVirtualHost();
        UUID uuid = event.getPlayer().getUniqueId();

        PlayerData data = PlayerLoginsCore.getPlayerStorage().getPlayerData().stream()
                .filter(d -> d.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);

        if (data == null) {
            PlayerLoginsCore.getPlayerStorage().getPlayerData().add(new PlayerData(uuid, address.getHostName(), address.getHostName(), getPlatform(uuid), PlayerLoginsCore.getIpDatabase().getCountryFromIP(event.getPlayer().getPendingConnection().getAddress().getAddress().getHostName())));
        } else {
            //this.logins.getPlayerStorage().getPlayerData().remove(data);

            data.setLastJoin(address.getHostName());
            data.setLastLocation(PlayerLoginsCore.getIpDatabase().getCountryFromIP(event.getPlayer().getPendingConnection().getAddress().getAddress().getHostName()));
            data.setPlatform(getPlatform(uuid));
            //this.logins.getPlayerStorage().getPlayerData().add(data);
        }

        PlayerLoginsCore.getHostnameStorage().getHostnames().compute(address.getHostName(), (s, i) -> {
            if (i == null || i < 0) {
                return 1;
            }

            return ++i;
        });
    }

}
