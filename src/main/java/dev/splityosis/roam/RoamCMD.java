package dev.splityosis.roam;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.xlib.mongodb.util.StringParseUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.security.spec.PSSParameterSpec;
import java.util.ArrayList;
import java.util.List;

public class RoamCMD implements CommandExecutor {


    private List<String> notAllowedAt;

    public RoamCMD(){
        notAllowedAt = Main.instance.getConfig().getStringList("settings.not-allowed-factions-claims");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (! (sender instanceof Player)){
            Util.sendMessage(sender, "&cOnly players can use this command!");
            return false;
        }
        Player player = (Player)sender;

        if (!player.hasPermission("roam.use")){
            Util.sendMessage(player, Main.instance.getConfig().getStringList(Main.instance.getConfig().getString("settings.use-permission")));
            return false;
        }

        if (RoamManager.isPlayerInRoam(player))
            RoamManager.disableRoam(player);
        else {
            String facAt = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation())).getName();
            if (notAllowedAt.contains(facAt)){
                Util.sendMessage(player, Main.instance.getConfig().getStringList("messages.cant-enable-roam-here"));
                return false;
            }
                RoamManager.startRoamWarmup(player);
        }
        return true;
    }
}
