package author404e.viewinv;

import com.xbaimiao.viewinv.ImageUtils;
import com.xbaimiao.viewinv.skull.Skull;
import me.albert.amazingbot.bot.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Cmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
//        if (sender.hasPermission("viewinv.admin")) {
//            ViewInv.instance.reloadConfig();
//            sender.sendMessage("§a重载完成");
//        } else sender.sendMessage("§c无权限");
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack.getType().equals(Material.PLAYER_HEAD)) {
                String base64 = Skull.getBase64((SkullMeta) Objects.requireNonNull(itemStack.getItemMeta()));
                player.sendMessage("" + base64);
                player.sendMessage(Objects.requireNonNull(Skull.getUrl(base64)));

                Group group = Bot.getApi().getGroup(533123583L);
                Image image = group.uploadImage(ExternalResource.create(ImageUtils.imageToBytes(Skull.skullImage(itemStack))));
                group.sendMessage(image);
            }
        }
        return true;
    }
}
