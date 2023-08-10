package dev.badbird.teams.commands.impl.managment;

import net.badbird5907.blib.util.StoredLocation;
import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.commands.annotation.TeamPermission;
import dev.badbird.teams.menu.waypoint.ListWaypointsMenu;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamRank;
import dev.badbird.teams.object.Waypoint;
import net.octopvp.commander.annotation.*;
import org.bukkit.entity.Player;

@Command(name = "teamwaypoint", description = "Team Waypoints")
public class WaypointCommands {
    @Command(name = "create", description = "Set a waypoint for your team.")
    //@Cooldown(10)
    @TeamPermission(TeamRank.TRUSTED)
    public void waypoint(@Sender Player sender, @Sender Team team, @Name("waypoint") @Required @JoinStrings String waypoint) {
        if (team.getWaypoints().stream().anyMatch(w -> w.getName().equalsIgnoreCase(waypoint))) {
            sender.sendMessage(Lang.WAYPOINT_EXISTS.toString());
            return;
        }
        Waypoint w = new Waypoint(waypoint,team);
        w.setLocation(new StoredLocation(sender.getLocation()));
        w.setWorld(sender.getWorld().getName());
        team.getWaypoints().add(w);
        team.broadcast(Lang.WAYPOINT_CREATED.toString(sender.getName(), waypoint));
        team.updateWaypoints();
    }

    @Command(name = "list", description = "List all waypoints for your team.")
    public void list(@Sender Player sender, @Sender Team team) {
        TeamsPlus.getInstance().getWaypointManager().updatePlayerWaypoints(sender);
        new ListWaypointsMenu(team, sender.getUniqueId()).open(sender);
    }
}
