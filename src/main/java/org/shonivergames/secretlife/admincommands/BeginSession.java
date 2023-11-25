package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.TasksManager;
import org.shonivergames.secretlife.config_readers.MessageReader;
import org.shonivergames.secretlife.PlayerDataManager;

public class BeginSession extends _CommandBase {
    public BeginSession() {
        super("_BeginSession", false);
    }

    @Override
    public void executeCommand(CommandSender sender, Player irrelevant) {
        boolean needToExit = false;

        for (Player player : Main.server.getOnlinePlayers()) {
            String errorCode = TasksManager.getReceiveNewTaskError(player);
            if(errorCode != null) {
                MessageReader.sendPrivate(baseConfigPath, errorCode, sender);
                needToExit = true;
            }
        }

        if(needToExit)
            return;

        for (Player player : Main.server.getOnlinePlayers()) {
            Main.playerData.setCanGift(player, true);
            TasksManager.giveTaskAnimated(player, false);
        }

        printFeedback(sender);
    }
}
