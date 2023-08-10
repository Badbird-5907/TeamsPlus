package dev.badbird.teams.commands.impl.staff;

import dev.badbird.teams.menu.ConfirmMenu;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.util.Permissions;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
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
}
