package io.obadiah.pl.spigot.listener;

import io.obadiah.pl.common.PlayerLoginsCore;
import io.obadiah.pl.common.storage.data.Platform;
import io.obadiah.pl.spigot.PlayerLogins;
import io.obadiah.pl.common.storage.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.UUID;

public class JoinQuitListener implements Listener {

    private final PlayerLogins logins;
    private boolean floodgate;

    public JoinQuitListener() {
        this.startListening();

        this.logins = PlayerLogins.get();

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
    private void join(PlayerLoginEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(this.logins, () -> {
            if (event.getPlayer().getAddress() == null)
                return;

            String hostname = event.getHostname();
            String playerIP = event.getAddress().getHostAddress();
            if (hostname.endsWith(":25565"))
                hostname = hostname.substring(0, hostname.length() - 6);


            UUID uuid = event.getPlayer().getUniqueId();
            PlayerData data = this.logins.getPlayerStorage().getPlayerData().stream()
                    .filter(d -> d.getUuid().equals(uuid))
                    .findFirst()
                    .orElse(null);

            if (data == null) {
                this.logins.getPlayerStorage().getPlayerData().add(new PlayerData(uuid, hostname, hostname, getPlatform(uuid), PlayerLoginsCore.getIpDatabase().getCountryFromIP(playerIP)));
            } else {
                //this.logins.getPlayerStorage().getPlayerData().remove(data);

                data.setLastJoin(hostname);
                String country = PlayerLoginsCore.getIpDatabase().getCountryFromIP(playerIP);
                data.setLastLocation(country);
                data.setPlatform(getPlatform(uuid));
                //this.logins.getPlayerStorage().getPlayerData().add(data);
            }

            this.logins.getHostnameStorage().getHostnames().compute(hostname, (s, i) -> {
                if (i == null || i < 0) {
                    return 1;
                }

                return ++i;
            });
        });
    }
}
