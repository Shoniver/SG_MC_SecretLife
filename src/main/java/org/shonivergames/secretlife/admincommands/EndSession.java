package org.shonivergames.secretlife.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.TasksManager;
import org.shonivergames.secretlife.config_readers.MessageReader;

public class EndSession extends _CommandBase {
    public EndSession() {
        super("_EndSession", false);
    }

    @Override
    public void executeCommand(CommandSender sender, Player irrelevant, boolean irrelevant2) {
        boolean needToExit = false;

        for (Player player : Main.server.getOnlinePlayers()) {
            String errorCode = TasksManager.getEndSessionError(player);
            if(errorCode != null) {
                MessageReader.sendPrivate(baseConfigPath, "specific_errors." + errorCode, sender, player.getName());
                needToExit = true;
            }
        }

        if(needToExit)
            return;

        // Fails the task for everyone who HAS A TASK, but without constant tasks
        for (Player player : Main.server.getOnlinePlayers()) {
            if(TasksManager.getEndSessionFailTaskError(player) == null)
                TasksManager.failTask(player, true);
        }

        printFeedback(sender);
    }
}
