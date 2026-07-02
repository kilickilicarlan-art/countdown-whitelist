package net.apexnw.countdown;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CountdownWhitelist extends JavaPlugin {

    // Hedef: 16 Temmuz 2026, 00:00 (Türkiye saati)
    private final ZonedDateTime target = ZonedDateTime.of(
            2026, 7, 16, 0, 0, 0, 0,
            ZoneId.of("Europe/Istanbul")
    );

    private BukkitTask task;
    private boolean whitelistTriggered = false;

    @Override
    public void onEnable() {
        task = Bukkit.getScheduler().runTaskTimer(this, this::tick, 0L, 20L);
        getLogger().info("CountdownWhitelist aktif. Hedef: " + target);
    }

    @Override
    public void onDisable() {
        if (task != null) {
            task.cancel();
        }
    }

    private void tick() {
        if (whitelistTriggered) {
            return;
        }

        Duration remaining = Duration.between(ZonedDateTime.now(target.getZone()), target);

        if (remaining.isNegative() || remaining.isZero()) {
            whitelistTriggered = true;
            openWhitelist();
            return;
        }

        long totalSeconds = remaining.getSeconds();
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        String sure = String.format("%02dg %02dsa %02ddk %02dsn",
                days, hours, minutes, seconds);

        Component bar = Component.text("ʜɪᴢᴍᴇᴛɪᴍɪᴢɪɴ ᴋᴀᴘᴀɴᴍᴀѕıɴᴀ: " + sure, NamedTextColor.RED);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendActionBar(bar);
        }
    }

    private void openWhitelist() {
        if (task != null) {
            task.cancel();
        }

        Bukkit.getScheduler().runTask(this, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist on");
            Component msg = Component.text("Whitelist açıldı!", NamedTextColor.RED);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendActionBar(msg);
            }
            getLogger().info("Süre doldu, whitelist açıldı.");
        });
    }
}
