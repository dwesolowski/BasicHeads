package com.github.dwesolowski.basicheads;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class BasicHeads extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        LivingEntity victim = e.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            if (killer.hasPermission("basicheads.drops")) {
                boolean shouldDrop = Math.random() < getConfig().getDouble("dropRate");

                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwningPlayer(e.getEntity());
                skull.setItemMeta(meta);

                if (getConfig().getBoolean("useRandom")) {
                    if (shouldDrop) {
                        victim.getLocation().getWorld().dropItemNaturally(victim.getLocation(), skull);
                        if (checkConfigValue("Messages.ObtainHead")) {
                            killer.sendMessage(colorize("Messages.ObtainHeadByChance").replace("{VICTIM}", victim.getName()));
                            victim.sendMessage(colorize("Messages.LostYourHead").replace("{KILLER}", killer.getName()));
                        }
                    }

                } else {
                    victim.getLocation().getWorld().dropItemNaturally(victim.getLocation(), skull);
                    if (checkConfigValue("Messages.ObtainHead")) {
                        killer.sendMessage(colorize("Messages.ObtainHead").replace("{VICTIM}", victim.getName()));
                        victim.sendMessage(colorize("Messages.LostYourHead").replace("{KILLER}", killer.getName()));
                    }
                }
            }
        }
    }

    public String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(str));
    }

    public boolean checkConfigValue(String name) {
        return !StringUtils.isBlank(getConfig().getString(name));
    }
}

