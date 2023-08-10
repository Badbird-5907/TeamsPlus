package dev.badbird.teams.commands.impl.staff;

import dev.badbird.teams.menu.ConfirmMenu;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamRank;
import dev.badbird.teams.util.Permissions;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Required;
import net.octopvp.commander.annotation.Sender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "teamsstaff", description = "Manage teams")
@Permission(Permissions.STAFF)
public class TeamsStaffCommand {
    @Command(name = "disband", aliases = "delete", description = "Disband a team")
    public void disband(@Sender CommandSender sender, Team target) {
        if (sender instanceof Player player) {
            if (target != null && player.hasPermission("teamsplus.staff.disband")) {
                new ConfirmMenu("disband this team", (bool) -> {
                    player.closeInventory();
                    if (bool) {
                        target.disband();
                        player.sendMessage(Lang.STAFF_DISBAND_TEAM.toString(target.getName()));
                    } else {
                        player.sendMessage(Lang.CANCELED.toString());
                    }
                }).open(player);
            }
        } else {
            target.disband();
            sender.sendMessage(Lang.STAFF_DISBAND_TEAM.toString(target.getName()));
        }
    }

    @Command(name = "forcejoin", description = "Force a player to join a team")
    @Permission("teamsplus.staff.forcejoin")
    public void forcejoin(@Sender CommandSender sender, @Required Team target, @Required PlayerData player) {
        target.join(player);
        sender.sendMessage(Lang.STAFF_FORCE_JOIN.toString(player.getName(), target.getName()));
    }

    @Command(name = "forceleave", description = "Force a player to leave a team")
    @Permission("teamsplus.staff.forceleave")
    public void forceleave(@Sender CommandSender sender, @Required Team target, @Required PlayerData player) {
        target.leave(player);
        sender.sendMessage(Lang.STAFF_FORCE_LEAVE.toString(player.getName(), target.getName()));
    }

    @Command(name = "forcerank", description = "Force a player to a rank")
    @Permission("teamsplus.staff.forcerank")
    public void forcerank(@Sender CommandSender sender, @Required Team target, @Required PlayerData player, @Required TeamRank rank) {
        target.setRank(player.getUuid(), rank);
        sender.sendMessage(Lang.STAFF_FORCE_RANK.toString(player.getName(), rank.name(), target.getName()));
    }

    @Command(name = "rename", description = "Rename a team")
    @Permission("teamsplus.staff.rename")
    public void rename(@Sender CommandSender sender, @Required Team target, @Required String newName) {
        String oldName = target.getName();
        target.setName(newName);
        sender.sendMessage(Lang.STAFF_FORCE_RENAME.toString(oldName, newName));
    }
}
