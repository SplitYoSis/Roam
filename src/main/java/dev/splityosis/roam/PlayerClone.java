package dev.splityosis.roam;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerClone {

    private static Map<Player, PlayerClone> playerClones = new HashMap<>();
    private static Map<NPC, PlayerClone> playerClonesNPC = new HashMap<>();

    private Player realPlayer;
    private NPC playerNPC;

    public PlayerClone(Player player) {
        this.realPlayer = player;
    }

    public void spawn(Location location){
        if (playerClones.containsKey(realPlayer)){
            Util.sendMessage(realPlayer, "&cSomething went wrong, please let staff know about this.");
            return;
        }
        playerNPC = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Main.instance.getConfig().getString("player-clone.display-name").replace("%player%", realPlayer.getName()));
        playerNPC.spawn(location);
        playerNPC.setProtected(false);
        SkinTrait skinTrait = playerNPC.getTrait(SkinTrait.class);
        skinTrait.setSkinName(realPlayer.getName());
        playerClones.put(realPlayer, this);
        playerClonesNPC.put(playerNPC, this);
    }

    public void remove(){
        playerClones.remove(realPlayer);
        playerClonesNPC.remove(playerNPC);
        playerNPC.destroy();
    }

    public Player getRealPlayer() {
        return realPlayer;
    }

    public NPC getPlayerNPC() {
        return playerNPC;
    }

    public static PlayerClone getPlayerClone(Player player){
        return playerClones.get(player);
    }

    public static PlayerClone getPlayerClone(NPC npc){
        return playerClonesNPC.get(npc);
    }

}
