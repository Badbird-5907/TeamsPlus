package dev.badbird.teams.commands.impl;

import dev.badbird.teams.manager.MessageManager;
import dev.badbird.teams.object.ChatChannel;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import net.kyori.adventure.text.Component;
import net.octopvp.commander.annotation.*;
import org.bukkit.entity.Player;

@Command(name = "chat", aliases = {"ch"}, description = "Chat with your team/allies")
public class ChatCommands {
    @Command(name = "team", aliases = {"t"}, description = "Chat with your team")
    public void teamChat(@Sender PlayerData sender, @Sender Player player, @Sender @Required Team team, @Optional @JoinStrings String message) {
        if (message != null && !message.isEmpty()) {
            MessageManager.handleTeam(player, message, team);
            return;
        }
        sender.setCurrentChannel(ChatChannel.TEAM);
        sender.sendMessage(Lang.CHAT_SWITCH_TO_TEAM.toString(team.getName()));
    }

    @Command(name = "ally", aliases = {"a"}, description = "Chat with your allies")
    public void allyChat(@Sender PlayerData sender, @Sender @Required Team senderTeam, @Sender Player player, @Required Team team, @Optional @JoinStrings String message) {
        if (!senderTeam.isAlly(team)) {
            sender.sendMessage(Lang.TEAM_NOT_ALLIED_WITH_TEAM.toString(team.getName()));
            return;
        }
        if (senderTeam.getTeamId().equals(team.getTeamId())) {
            sender.sendMessage(Lang.CANNOT_ALLY_CHAT_SELF.toString());
            return;
        }
        if (message != null && !message.isEmpty()) {
            MessageManager.handleAlly(sender, player, senderTeam, message);
            return;
        }
        sender.setCurrentChannel(ChatChannel.ALLY);
        sender.setAllyChatTeamId(team.getTeamId());
        sender.sendMessage(Lang.CHAT_SWITCH_TO_ALLY.toString(team.getName()));
    }

    @Command(name = "all", aliases = {"global", "g", "a"}, description = "Chat with everyone")
    public void globalChat(@Sender PlayerData sender, @Sender Player player, @Optional @JoinStrings String message) {
        if (message != null && !message.isEmpty()) {
            MessageManager.handleGlobal(sender, player, message);
            return;
        }
        sender.setCurrentChannel(ChatChannel.GLOBAL);
        sender.sendMessage(Lang.CHAT_SWITCH_TO_GLOBAL.toString());
    }
}
