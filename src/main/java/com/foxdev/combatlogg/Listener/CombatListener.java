package com.foxdev.combatlogg.Listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatListener implements Listener {

    // HashMap om bij te houden welke spelers in combat zijn
    private HashMap<UUID, Long> inCombat = new HashMap<>();

    // Tijd dat een speler als combat wordt beschouwd na laatste schade
    private int combatTime = 10; // 10 seconden

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            // Start combat timer
            startCombat(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isInCombat(player)) {
            // Spawn NPC
            spawnNPC(player);
        }
    }

    private void startCombat(Player player) {
        // Sla laatste schade tijd op
        long time = System.currentTimeMillis();
        inCombat.put(player.getUniqueId(), time);

        // Plan combat timer taak
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            endCombat(player);
        }, combatTime * 20L); // 20 ticks per second
    }

    private boolean isInCombat(Player player) {
        if (inCombat.containsKey(player.getUniqueId())) {
            long time = inCombat.get(player.getUniqueId());
            long diff = System.currentTimeMillis() - time;
            return diff < combatTime * 1000; // Within combat time
        } else {
            return false;
        }
    }

    private void endCombat(Player player) {
        inCombat.remove(player.getUniqueId());
    }

    private void spawnNPC(Player player) {

        Location loc = player.getLocation();

        // Spawn een nieuwe Villager entity
        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);

        // Stel de custom name
        villager.setCustomName(player.getName());
        villager.setCustomNameVisible(true);
        
        // Voorkom despawnen
        villager.setRemoveWhenFarAway(false);

        // Stel AI
        villager.setAI(true);

    }


}