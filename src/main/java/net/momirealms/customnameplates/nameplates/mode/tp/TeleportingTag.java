package net.momirealms.customnameplates.nameplates.mode.tp;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.mode.ArmorStandManager;
import net.momirealms.customnameplates.nameplates.mode.EntityTag;
import net.momirealms.customnameplates.nameplates.mode.EventListenerE;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;

public class TeleportingTag extends EntityTag {

    private EventListenerE listener;

    private VehicleChecker vehicleChecker;

    private TpPacketsHandler handler;

    private BukkitTask task;

    public TeleportingTag(String name) {
        super(name);
    }

    @Override
    public void load() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            armorStandManagerMap.put(all, new ArmorStandManager(all));
            CustomNameplates.instance.getTeamPacketManager().sendUpdateToOne(all);
            CustomNameplates.instance.getTeamPacketManager().sendUpdateToAll(all);
            for (Player player : Bukkit.getOnlinePlayers())
                spawnArmorStands(player, all);
        }
        listener = new EventListenerE(this);
        Bukkit.getPluginManager().registerEvents(listener, CustomNameplates.instance);
        this.handler = new TpPacketsHandler("TpHandler", this);
        this.handler.load();
        this.vehicleChecker = new VehicleChecker(this);
        this.vehicleChecker.load();
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.instance, () -> {
           for (Player player : Bukkit.getOnlinePlayers()) {
               this.vehicleChecker.refresh(player);
           }
        },10,20);
        super.load();
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(listener);
        super.unload();
        this.handler.unload();
        this.vehicleChecker.unload();
        this.task.cancel();
    }

    @Override
    public void onSneak(Player player, boolean isSneaking) {
        getArmorStandManager(player).setSneak(isSneaking, true);
    }

    @Override
    public void onRespawn(Player player) {
        getArmorStandManager(player).teleport();
    }

    private void spawnArmorStands(Player viewer, Player target) {
        if (target == viewer) return;
        if (viewer.getWorld() != target.getWorld()) return;
        if (viewer.canSee(target))
            getArmorStandManager(target).spawn(viewer);
    }

    @Override
    public void onJoin(Player player) {
        super.onJoin(player);
    }

    @Override
    public void onQuit(Player player) {
        super.onQuit(player);
    }
}