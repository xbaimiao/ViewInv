package com.xbaimiao.viewinv;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("viewinv.admin")) {
            ViewInv.instance.reloadConfig();
            sender.sendMessage("§a重载完成");
        } else sender.sendMessage("§c无权限");
//        if (sender instanceof Player) {
//            Player player = (Player) sender;
//            ItemStack itemStack = player.getInventory().getItemInMainHand();
//            if (itemStack.getType().equals(Material.PLAYER_HEAD)) {
//                String base64 = Skull.getBase64((SkullMeta) Objects.requireNonNull(itemStack.getItemMeta()));
//                player.sendMessage("" + base64);
//                player.sendMessage(Objects.requireNonNull(Skull.getUrl(base64)));
//
//                Group group = Bot.getApi().getGroup(533123583L);
//                Image image = group.uploadImage(ExternalResource.create(ImageUtils.imageToBytes(Skull.skullImage(itemStack))));
//                group.sendMessage(image);
//            }
//        }
        return true;
    }
}
