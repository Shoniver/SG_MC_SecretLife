package org.shonivergames.secretlife.config_readers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.shonivergames.secretlife.LivesManager;
import org.shonivergames.secretlife.Main;
import org.shonivergames.secretlife.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskReader {
    private static final String configName = ".tasks.";
    private static Random rnd;

    public static ItemStack getRandomTask(String configTitle, Player player, String difficulty){
        String configPath = configTitle + configName;

        String taskTitle = SettingReader.getString(configPath, "task_title");
        taskTitle = Util.getFormattedString(taskTitle, player.getName());

        String taskContent = SettingReader.getString(configPath, "task_content");
        taskContent = Util.getFormattedString(taskContent, difficulty.toUpperCase(), getRandomContent(configPath, player, difficulty));

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(taskTitle);
        meta.setDisplayName(taskTitle);
        meta.setAuthor(SettingReader.getAdminName());
        Util.writeBookContent(meta, taskContent);
        book.setItemMeta(meta);

        return book;
    }

    private static String getRandomContent(String configPath, Player player, String difficulty) {
        if (rnd == null)
            rnd = new Random();

        List<String> allTasks = new ArrayList<>();
        if(LivesManager.isRedPlayer(player))
            allTasks.addAll(getTasksFromConfig(configPath + "red." + difficulty));
        else {
            allTasks.addAll(getTasksFromConfig(configPath + difficulty));

            if (LivesManager.isThereYellowPlayer() && !LivesManager.isYellowPlayer(player))
                allTasks.addAll(getTasksFromConfig(configPath + "has_yellows." + difficulty));
            else // If there are no yellow players or THIS player is yellow
                allTasks.addAll(getTasksFromConfig(configPath + "no_yellows." + difficulty));

            if (LivesManager.isThereRedPlayer() && !LivesManager.isRedPlayer(player))
                allTasks.addAll(getTasksFromConfig(configPath + "has_reds." + difficulty));
            else
                allTasks.addAll(getTasksFromConfig(configPath + "no_reds." + difficulty));
        }

        String draw = allTasks.get(rnd.nextInt(allTasks.size()));
        return Util.getFormattedString(draw, Util.getRandomOtherPlayer(player).getName());
    }

    private static List<String> getTasksFromConfig(String configPath){
        List<String> allTasks;
        if(Main.configFile.isList(configPath))
            allTasks = Main.configFile.getStringList(configPath);
        else
            allTasks = new ArrayList<>();
        return allTasks;
    }
}
