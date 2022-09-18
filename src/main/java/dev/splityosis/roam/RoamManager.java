package dev.splityosis.roam;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

import java.util.Map;

public class RoamManager {

    public static Map<Player, Location> playersInRoam = new HashMap<>();
    public static Map<Player, Long> playersInWarmup = new HashMap<>();

    public static void enableRoam(Player player){
        PlayerClone playerClone = new PlayerClone(player);
        playerClone.spawn(player.getLocation());
        player.setGameMode(GameMode.SPECTATOR);
        playersInRoam.put(player, player.getLocation().clone());
        Util.sendMessage(player, Main.instance.getConfig().getStringList("messages.roam-enabled"));
    }

    public static void startRoamWarmup(Player player){
        if (player.hasPermission(Main.instance.getConfig().getString("settings.bypass-warmup-permission"))){
            enableRoam(player);
            return;
        }

        Util.sendMessage(player, Main.instance.getConfig().getStringList("messages.warmup-started"));
        long pin = System.currentTimeMillis();
        playersInWarmup.put(player, pin);
        new BukkitRunnable(){
            @Override
            public void run() {
                if (playersInWarmup.get(player) != pin) return;
                playersInWarmup.remove(player);
                enableRoam(player);
            }
        }.runTaskLater(Main.instance, 20L *Main.instance.getConfig().getInt("settings.enabling-warmup"));
    }

    public static void disableRoam(Player player){
        if (!isPlayerInRoam(player)) return;
        PlayerClone playerClone = PlayerClone.getPlayerClone(player);
        if (playerClone == null) return;
        playerClone.remove();
        player.teleport(playersInRoam.get(player));
        player.setGameMode(GameMode.SURVIVAL);
        playersInRoam.remove(player);
        playersInWarmup.remove(player);
        Util.sendMessage(player, Main.instance.getConfig().getStringList("messages.roam-disabled"));
    }

    public static boolean isPlayerInRoam(Player player){
        return playersInRoam.containsKey(player);
    }

    public static Location getRoamPlayerOriginalLocation(Player player){
        return playersInRoam.get(player);
    }
}
