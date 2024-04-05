package com.lothus.listeners;

import com.lothus.Lobby;
import com.lothus.snow.SnowmanInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SnowListener implements Listener {

    private List<SnowmanInfo> snowmanInfos = new ArrayList<>();

    public SnowListener() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (SnowmanInfo snowmanInfo : snowmanInfos) {
                    snowmanInfo.getSnowman().launchProjectile(Snowball.class);
                    if (snowmanInfo.getDelete() <= System.currentTimeMillis()) {
                        Player passager = snowmanInfo.getPlayer();
                        passager.sendMessage("§eO seu boneco de neve derreteu!");
                        snowmanInfo.getSnowman().remove();

                    }
                }

                snowmanInfos.removeIf(snowmanInfo -> snowmanInfo.getDelete() <= System.currentTimeMillis());
            }
        }.runTaskTimer(Lobby.getPlugin(), 0, 20);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball)) {
            return;
        }

        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Snowball snowball = (Snowball) event.getEntity();
        Player player = (Player) snowball.getShooter();
        Location location = snowball.getLocation();

        Snowman snowman = location.getWorld().spawn(location.clone().add(0, 1.5, 0), Snowman.class);
        snowman.setCustomName("§eBoneco de Neve");
        
        snowmanInfos.add(new SnowmanInfo(player, snowman, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15)));
    }
}
