package dev.splityosis.roam;

import com.jumbo1907.teleportationplus.events.PlayerTeleportToPlayerEvent;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.util.List;

public class Listeners implements Listener {

    private int radius;
    private List<String> tooFar;
    private List<String> whitelsitCMDs;
    private List<String> cantGoThere;

    public Listeners() {
        this.radius = Main.instance.getConfig().getInt("settings.radius-horizontal");
        this.tooFar = Main.instance.getConfig().getStringList("messages.moved-too-far");
        this.whitelsitCMDs = Main.instance.getConfig().getStringList("settings.whitelisted-commands-in-roam");
        this.cantGoThere = Main.instance.getConfig().getStringList("messages.cant-go-there");
    }

    @EventHandler
    public void quit(PlayerQuitEvent e){
        if (!RoamManager.isPlayerInRoam(e.getPlayer())) return;
        RoamManager.disableRoam(e.getPlayer());
    }

    @EventHandler
    public void command(PlayerCommandPreprocessEvent e){
        if (!RoamManager.playersInWarmup.containsKey(e.getPlayer()) && !RoamManager.isPlayerInRoam(e.getPlayer())) return;
        if (e.getMessage().toLowerCase().equalsIgnoreCase("/roam")) return;
        if (whitelsitCMDs.contains("/"+e.getMessage().toLowerCase())) return;
        e.setCancelled(true);
        Util.sendMessage(e.getPlayer(), Main.instance.getConfig().getStringList("messages.cant-do-command-in-warmup"));
    }

    @EventHandler
    public void move(PlayerMoveEvent e){
        if (!RoamManager.isPlayerInRoam(e.getPlayer())) return;
        if (e.getTo().getY() < 1){
            e.setCancelled(true);
            Util.sendMessage(e.getPlayer(), cantGoThere);
        }
        Location original = RoamManager.getRoamPlayerOriginalLocation(e.getPlayer());
        double xDif = (e.getTo().getX() - original.getX());
        double zDif = (e.getTo().getZ() - original.getZ());
        double distance = Math.sqrt(xDif*xDif + zDif*zDif);
        if (distance <= radius) return;
        Vector push = original.toVector().subtract(e.getTo().toVector());
        push.normalize();
        push.multiply(2);
        e.getPlayer().setVelocity(push);
        Util.sendMessage(e.getPlayer(), tooFar);
    }

    @EventHandler
    public void move2(PlayerMoveEvent e){
        if (!RoamManager.playersInWarmup.containsKey(e.getPlayer())) return;
        RoamManager.playersInWarmup.remove(e.getPlayer());
        Util.sendMessage(e.getPlayer(), Main.instance.getConfig().getStringList("messages.warmup-cancelled"));
    }

    @EventHandler
    public void roamIntercat(PlayerInteractEvent e){
        if (RoamManager.isPlayerInRoam(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void roamInv(InventoryClickEvent e){
        if (RoamManager.isPlayerInRoam((Player)e.getWhoClicked())) e.setCancelled(true);
    }

    @EventHandler
    public void roamInv(InventoryDragEvent e){
        if (RoamManager.isPlayerInRoam((Player)e.getWhoClicked())) e.setCancelled(true);
    }

    @EventHandler
    public void roamInv(InventoryOpenEvent e){
        if (RoamManager.isPlayerInRoam((Player) e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void roamInv(InventoryInteractEvent e){
        if (RoamManager.isPlayerInRoam((Player) e.getWhoClicked())) e.setCancelled(true);
    }


    @EventHandler
    public void roamIntercat(PlayerInteractAtEntityEvent e){
        if (RoamManager.isPlayerInRoam(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void npcHIT(NPCDamageByEntityEvent e){
        RoamManager.disableRoam(PlayerClone.getPlayerClone(e.getNPC()).getRealPlayer());
    }

    @EventHandler
    public void tp (PlayerTeleportToPlayerEvent e){
        if (!RoamManager.isPlayerInRoam(e.getTeleportTo())) return;
        e.setTeleportLocation(RoamManager.playersInRoam.get(e.getTeleportTo()));
    }
}
