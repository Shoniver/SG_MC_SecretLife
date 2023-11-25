package org.shonivergames.secretlife;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.shonivergames.secretlife.config_readers.SettingReader;
import org.shonivergames.secretlife.config_readers.TitleReader;
import org.shonivergames.secretlife.events.EntityRegenEvent;

import java.util.List;

public class HealthManager {
    private static final String baseConfigPath = "health_manager";

    public static void init(){
        if(SettingReader.getBool(baseConfigPath, "uhc_enabled")) {
            Main.server.getPluginManager().registerEvents(new EntityRegenEvent(), Main.instance);
            setNaturalRegen(false);
        }
        else
            setNaturalRegen(true);
    }

    public static int addHealth(Player player, int health, boolean overflow){
        return addHealthByPlayer(player, health, SettingReader.getAdminName(), overflow);
    }

    public static int addHealthByPlayer(Player player, int health, String givenBy, boolean overflow){
        if(health == 0)
            return 0;
        int maxHealth = getCorrectMaxHealth(overflow);
        int healthChange = health;
        double currentHealth = player.getHealth();

        if(currentHealth > maxHealth)
            healthChange = 0;
        else if(currentHealth + healthChange > maxHealth) {
            healthChange =  maxHealth - (int)Math.round(currentHealth);
            currentHealth = maxHealth;
        }
        else
            currentHealth += healthChange;

        TitleReader.send(baseConfigPath, "given", player, givenBy, getPresentableHeartsCount(healthChange));
        setHealth(player, currentHealth);
        return healthChange;
    }

    public static int removeHealth(Player player, int health, boolean canKill){
        return removeHealthByPlayer(player, health, SettingReader.getAdminName(), canKill);
    }

    public static int removeHealthByPlayer(Player player, int health, String takenBy, boolean canKill){
        if(health == 0)
            return 0;
        int minHealth = getCorrectMinHealth(canKill);
        int healthChange = health;
        double currentHealth = player.getHealth();

        if(currentHealth < minHealth)
            healthChange = 0;
        else if(currentHealth - healthChange < minHealth) {
            healthChange =  (int)Math.round(currentHealth) - minHealth;
            currentHealth = minHealth;
        }
        else
            currentHealth -= healthChange;

        TitleReader.send(baseConfigPath, "taken", player, takenBy, getPresentableHeartsCount(healthChange));
        setHealth(player,currentHealth);
        return healthChange;
    }

    private static int getCorrectMaxHealth(boolean overflow){
        if(overflow)
            return SettingReader.getInt(baseConfigPath, "true_max");
        else
            return SettingReader.getInt(baseConfigPath, "reward_max");
    }

    private static int getCorrectMinHealth(boolean canKill){
        if(canKill)
            return 0;
        else
            return 2;
    }

    private static String getPresentableHeartsCount(int healthCount){
        if(healthCount % 2 == 0)
            return String.valueOf(healthCount / 2);
        else
            return (healthCount / 2) + ".5";
    }

    public static void resetHealth(Player player){
        setHealth(player, SettingReader.getInt(baseConfigPath, "health_on_respawn"));
    }

    public static void initNewPlayer(Player player){
        resetHealth(player);
    }

    public static boolean willGoAboveMaxHealth(Player player, int healthAddition, boolean overflow){
        int maxHealth = getCorrectMaxHealth(overflow);
        return maxHealth < player.getHealth() + healthAddition;
    }

    private static void setHealth(Player player, double health){
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        player.setHealth(health);
    }

    public static void setNaturalRegen(boolean isTrue){
        List<World> worlds = Main.server.getWorlds();
        for (World x: worlds) {
            x.setGameRule(GameRule.NATURAL_REGENERATION, isTrue);
        }
    }
}
