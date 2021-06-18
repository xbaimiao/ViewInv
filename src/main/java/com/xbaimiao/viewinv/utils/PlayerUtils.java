package com.xbaimiao.viewinv.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtils {

    public static Player getPlayer(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) return p;
        OfflinePlayer off = loadOffPlayer(uuid);
        return off == null ? null : loadPlayer(off);
    }

    /**
     * 通过uuid获取Player对象
     *
     * @param uuid 玩家uuid
     * @return Player对象
     */
    public static Player loadOffPlayer(UUID uuid) {
        OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
        return loadPlayer(off);
    }

    /**
     * 从存档中加载玩家
     *
     * @param offline 离线玩家
     * @return 玩家
     */
    public static Player loadPlayer(OfflinePlayer offline) {
        if (!offline.hasPlayedBefore()) return null;
        GameProfile profile = new GameProfile(offline.getUniqueId(), offline.getName() != null ? offline.getName() : offline.getUniqueId().toString());
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = server.getWorldServer(World.OVERWORLD);
        if (worldServer == null) return null;
        EntityPlayer entity = new EntityPlayer(server, worldServer, profile, new PlayerInteractManager(worldServer));
        Player target = entity.getBukkitEntity();
        if (target != null) target.loadData();
        return target;
    }

}
