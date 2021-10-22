package com.github.dwesolowski.basicheads;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public class BasicHeads extends JavaPlugin implements Listener {

    private String LOST_HEAD, OBTAIN_HEAD, OBTAIN_HEAD_CHANCE;
    private boolean USE_RANDOM;
    private double DROP_RATE;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent evt) {
        // Doesn't need to be final, but ah well.
        final Player dead = evt.getEntity(), killer = dead.getKiller();

        // Do nothing if killer isn't a player, or they don't have permission.
        if (killer == null || killer.hasPermission("basicheads.drops")) return;

        // ThreadLocalRandom is preferred over Random.
        if (!USE_RANDOM || DROP_RATE > ThreadLocalRandom.current().nextDouble()) {
            final ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            final ItemMeta meta = skull.getItemMeta();

            // Remove IDE errors. Usually, you should always receive a new SkullMeta
            // instance and this check would be unnecessary. You never know though.
            if (meta instanceof SkullMeta)
                ((SkullMeta) meta).setOwningPlayer(dead);

            skull.setItemMeta(meta);

            // Alternatively, we could also use 'evt.getDrops().add(skull);'
            dead.getWorld().dropItemNaturally(dead.getLocation(), skull);

            if (LOST_HEAD != null)
                dead.sendMessage(LOST_HEAD.replace("{KILLER}", ChatColor.stripColor(killer.getDisplayName())));

            final String message = USE_RANDOM ? OBTAIN_HEAD_CHANCE : OBTAIN_HEAD;
            if (message != null)
                killer.sendMessage(message.replace("{VICTIM}", ChatColor.stripColor(dead.getDisplayName())));
        }
    }

    private String formatMessage(String key) {
        key = getConfig().getString(key, "").trim();
        return key.isEmpty() ? null : ChatColor.translateAlternateColorCodes('&', key);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        LOST_HEAD = formatMessage("Messages.LostYourHead");
        OBTAIN_HEAD = formatMessage("Messages.ObtainHead");
        OBTAIN_HEAD_CHANCE = formatMessage("Messages.ObtainHeadChance");
        DROP_RATE = getConfig().getDouble("dropRate");
        USE_RANDOM = getConfig().getBoolean("useRandom");
    }

    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
        cs.sendMessage("§7[§aBasicHeads§7] §eConfig Reloaded!");
        reloadConfig();
        return true;
    }
}