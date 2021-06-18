package com.xbaimiao.viewinv;

import com.xbaimiao.viewinv.utils.PlayerUtils;
import me.albert.amazingbot.bot.Bot;
import me.albert.amazingbot.events.GroupMessageEvent;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.xbaimiao.viewinv.ImageGeneration.*;

public class MessageEvents implements Listener {

    public static void sendImg(GroupMessageEvent e, BufferedImage view) {
        Group g = e.getEvent().getGroup();
        if (view != null) try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(view, "png", os);
            ByteArrayInputStream input = new ByteArrayInputStream(os.toByteArray());
            g.sendMessage(Image.fromId(Contact.uploadImage(g, input).getImageId()));
            input.close();
            return;
        } catch (IOException ee) {
            Bukkit.getLogger().warning("处理图片时出现异常");
            ee.printStackTrace();
        }
        g.sendMessage("查询时出现异常");
    }

    @EventHandler
    public void getInv(GroupMessageEvent e) {
        String id = e.getGroupID().toString();
        List<String> allow = ViewInv.instance.getConfig().getStringList("groups");
        String end = ViewInv.instance.getConfig().getString("end");
        String inv = ViewInv.instance.getConfig().getString("inv");
        if ((!allow.contains("@all") && (allow.size() == 0 || !allow.contains(id)))
                || !(e.getMsg().equals(inv) || e.getMsg().equals(end))) return;
        UUID uuid = Bot.getApi().getPlayer(e.getUserID());
        if (uuid == null) {
            Bot.getApi().sendGroupMsg(e.getGroupID().toString(), "请先绑定");
            return;
        }
        Player p = PlayerUtils.getPlayer(uuid);
        if (p == null) {
            Bot.getApi().sendGroupMsg(e.getGroupID().toString(), "没有找到此玩家");
            return;
        }
        sendImg(e, e.getMsg().equals(inv) ? getInventoryImage(p) : getEndView(p));
    }
}
