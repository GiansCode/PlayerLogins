package io.obadiah.pl.listener;

import io.obadiah.pl.PlayerLogins;
import io.obadiah.pl.storage.data.PlayerData;
import io.obadiah.pl.util.Listener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.net.InetAddress;
import java.util.UUID;

public class JoinQuitListener implements Listener {

    private final PlayerLogins logins;

    public JoinQuitListener() {
        this.startListening();

        this.logins = PlayerLogins.get();
    }

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(this.logins, () -> {
            InetAddress address = event.getAddress();
            UUID uuid = event.getUniqueId();

            PlayerData data = this.logins.getPlayerStorage().getPlayerData().stream()
              .filter(d -> d.getUuid().equals(uuid))
              .findFirst()
              .orElse(null);

            if (data == null) {
                this.logins.getPlayerStorage().getPlayerData().add(new PlayerData(uuid, address.getHostAddress(), address.getHostAddress()));
            } else {
                this.logins.getPlayerStorage().getPlayerData().remove(data);

                data.setLastJoin(address.getHostAddress());
                this.logins.getPlayerStorage().getPlayerData().add(data);
            }

            this.logins.getHostnameStorage().getHostnames().compute(address.getHostAddress(), (s, i) -> {
                if (i == null || i < 0) {
                    return 1;
                }

                return ++i;
            });
        });
    }
}
