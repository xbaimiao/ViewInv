package author404e.viewinv;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.xbaimiao.viewinv.skull.Skull;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class Main {
    /**
     * 获取带玩家渲染的背包图片
     *
     * @param p 玩家
     * @return 背包预览图片
     */
    public static BufferedImage getInvView(Player p) {
        PlayerInventory inv = p.getInventory();
        BufferedImage invImg;
        try {
            invImg = getInvWithSkin(p.getName());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Graphics2D g2d = invImg.createGraphics();
        //转换图片
        for (int i = 0; i < 41; i++) {
            BufferedImage itemImg = getItemStackImg(inv.getItem(i));
            if (itemImg == null) {
                if (i > 35) g2d.drawImage(getResourcesImg("textures/" + i + ".png"), l[i][0], l[i][1], 48, 48, null);
                continue;
            }
            g2d.drawImage(itemImg, l[i][0], l[i][1], 48, 48, null);
        }
        g2d.dispose();
        return invImg;
    }

    /**
     * 获取玩家末影箱预览
     *
     * @param p 玩家
     * @return 末影箱预览图片
     */
    public static BufferedImage getEndView(Player p) {
        BufferedImage endImg = new BufferedImage(528, 204, 2);
        Graphics2D g2d = endImg.createGraphics();
        g2d.drawImage(getCustomImg("end.png"), 0, 0, 528, 204, null);
        Inventory end = p.getEnderChest();
        //转换图片
//        for (ItemStack itemStack : end) {
//
//        }
        for (int i = 0; i < 27; i++) {
            ItemStack itemStack = end.getItem(i);
            System.out.println(itemStack + "" + i);
            BufferedImage itemImg = getItemStackImg(itemStack);
            if (itemImg == null) continue;
            System.out.println(itemImg + "" + i);
            g2d.drawImage(itemImg, e[i][0], e[i][1], 48, 48, null);
        }
        g2d.dispose();
        return endImg;
    }

    /**
     * 获取物品的渲染图片
     *
     * @param item 物品的ItemStack
     * @return 物品的渲染图片
     */
    public static BufferedImage getItemStackImg(ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR)) return null;
        if (item.hasItemMeta() && item.getItemMeta() instanceof SkullMeta) {
            return Skull.skullImage(item);
        }
        Material m = item.getType();
        String path = "textures/";
        switch (m) {
            case AIR:
                return null;
            case TIPPED_ARROW:
                path += "arrow/" + getPotion(item) + ".png";
                break;
            case POTION:
                path += "potion/" + getPotion(item) + ".png";
                break;
            case SPLASH_POTION:
                path += "splash/" + getPotion(item) + ".png";
                break;
            case LINGERING_POTION:
                path += "lingering/" + getPotion(item) + ".png";
                break;
            case FLOWER_BANNER_PATTERN:
            case CREEPER_BANNER_PATTERN:
            case SKULL_BANNER_PATTERN:
            case MOJANG_BANNER_PATTERN:
            case GLOBE_BANNER_PATTERN:
            case PIGLIN_BANNER_PATTERN:
                path += "PATTERN.png";
                break;
            default:
                path += "normal/" + m.name() + ".png";
        }
        BufferedImage itemImg = getResourcesImg(path);
        Graphics2D g2 = itemImg.createGraphics();
        //附魔
        if (needEnchant(item)) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f));
            g2.drawImage(getResourcesImg("textures/enchant.png"), 0, 0, 48, 48, null);
            g2.dispose();
            g2 = itemImg.createGraphics();
        }
        char[] c = String.valueOf(item.getAmount()).toCharArray();
        g2.drawImage(getAmountImg(c), 12, 24, 36, 24, null);
        g2.dispose();
        return itemImg;
    }

    /**
     * 判断物品是否自带附魔光效
     *
     * @param item 物品的ItemStack
     * @return 是否需要手动添加光效
     */
    public static boolean needEnchant(ItemStack item) {
        ItemMeta im = item.getItemMeta();
        if (im == null || !im.toString().contains("enchants")) return false;
        List<Material> l = Arrays.asList(
                Material.ENCHANTED_BOOK,
                Material.POTION,
                Material.LINGERING_POTION,
                Material.SPLASH_POTION
        );
        return !l.contains(item.getType());
    }

    /**
     * 获取数量的图片
     *
     * @param cs 数字charArray
     * @return 数量图片 0和1不显示
     */
    public static BufferedImage getAmountImg(char[] cs) {
        BufferedImage amountImg = new BufferedImage(36, 24, 2);
        Graphics2D g2 = amountImg.createGraphics();
        if (cs.length == 1) {
            if (!(cs[0] == '1' || cs[0] == '0'))
                g2.drawImage(getResourcesImg("textures/number/" + cs[0] + ".png"), 18, 0, 18, 24, null);
        } else {
            g2.drawImage(getResourcesImg("textures/number/" + cs[0] + ".png"), 0, 0, 18, 24, null);
            g2.drawImage(getResourcesImg("textures/number/" + cs[1] + ".png"), 18, 0, 18, 24, null);
        }
        return amountImg;
    }

    /**
     * 获取带有玩家皮肤的背包贴图
     *
     * @param playerName 玩家名i在
     * @return 带有玩家皮肤的背包贴图
     * @throws IOException 异常
     */
    public static BufferedImage getInvWithSkin(String playerName) throws IOException {
        //名字 -> UUID
        Document d = Jsoup
                .connect("https://api.mojang.com/users/profiles/minecraft/" + playerName)
                .timeout(10000)
                .ignoreContentType(true)
                .get();
        String uuid = d.body().html().trim().split("\",\"id\":\"")[1].split("\"")[0];
        //UUID -> 详细属性
        String profileUrl = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;
        d = Jsoup.connect(profileUrl).timeout(10000).ignoreContentType(true).get();
        JsonElement je = new JsonParser().parse(d.body().html());
        String properties = je.getAsJsonObject()
                .get("properties").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("value").getAsString();
        properties = new String(Base64.getDecoder().decode(properties));
        je = new JsonParser().parse(properties)
                .getAsJsonObject().get("textures")
                .getAsJsonObject().get("SKIN");
        String url = je.getAsJsonObject().get("url").getAsString();
        je = je.getAsJsonObject().get("metadata");
        String model = je == null ? "old" : je.getAsJsonObject().get("model").getAsString();
        BufferedImage skin = getSkin(url);
        if (skin == null) throw new IOException();
        //背包
        BufferedImage inv = new BufferedImage(528, 498, 2);
        Graphics2D g2 = inv.createGraphics();
        //背景贴图
        g2.drawImage(getCustomImg("inv.png"), 0, 0, 528, 498, null);
        if (model.equals("old")) {
            //头
            BufferedImage head = skin.getSubimage(8, 8, 8, 8);
            //身体
            BufferedImage body = skin.getSubimage(20, 20, 8, 12);
            //手
            BufferedImage a = skin.getSubimage(44, 20, 4, 12);
            //腿
            BufferedImage l = skin.getSubimage(4, 20, 4, 12);
            //绘制
            g2.drawImage(head, 132, 56, 42, 42, null);
            g2.drawImage(body, 132, 98, 42, 64, null);
            g2.drawImage(a, 174, 98, 16, 64, null);
            g2.drawImage(a, 116, 98, 16, 64, null);
            g2.drawImage(l, 153, 162, 21, 64, null);
            g2.drawImage(l, 132, 162, 21, 64, null);
            g2.dispose();
        } else {
            BufferedImage la1, la2, ra1, ra2;
            if (model.equals("slim")) {
                //左手
                la1 = skin.getSubimage(36, 52, 3, 12);
                la2 = skin.getSubimage(52, 52, 3, 12);
                //右手
                ra1 = skin.getSubimage(44, 20, 3, 12);
                ra2 = skin.getSubimage(44, 36, 3, 12);
                g2.drawImage(la1, 174, 98, 16, 64, null);
                g2.drawImage(la2, 174, 98, 18, 64, null);
                g2.drawImage(ra1, 116, 98, 16, 64, null);
                g2.drawImage(ra2, 114, 98, 18, 64, null);
            } else {
                //左手
                la1 = skin.getSubimage(36, 52, 4, 12);
                la2 = skin.getSubimage(52, 52, 4, 12);
                //右手
                ra1 = skin.getSubimage(44, 20, 4, 12);
                ra2 = skin.getSubimage(44, 36, 4, 12);
                g2.drawImage(la1, 170, 98, 20, 64, null);
                g2.drawImage(la2, 170, 98, 22, 64, null);
                g2.drawImage(ra1, 116, 98, 20, 64, null);
                g2.drawImage(ra2, 114, 98, 22, 64, null);
            }
            //头
            BufferedImage head1 = skin.getSubimage(8, 8, 8, 8);
            BufferedImage head2 = skin.getSubimage(40, 8, 8, 8);
            //身体
            BufferedImage body1 = skin.getSubimage(20, 20, 8, 12);
            BufferedImage body2 = skin.getSubimage(20, 36, 8, 12);
            //左腿
            BufferedImage ll1 = skin.getSubimage(20, 52, 4, 12);
            BufferedImage ll2 = skin.getSubimage(4, 52, 4, 12);
            //右腿
            BufferedImage rl1 = skin.getSubimage(4, 20, 4, 12);
            BufferedImage rl2 = skin.getSubimage(4, 36, 4, 12);
            //绘制
            g2.drawImage(head1, 132, 56, 42, 42, null);
            g2.drawImage(head2, 128, 52, 50, 50, null);
            g2.drawImage(body1, 132, 98, 42, 64, null);
            g2.drawImage(body2, 132, 98, 42, 64, null);
            g2.drawImage(ll1, 153, 162, 21, 64, null);
            g2.drawImage(ll2, 153, 162, 22, 64, null);
            g2.drawImage(rl1, 132, 162, 21, 64, null);
            g2.drawImage(rl2, 131, 162, 22, 64, null);
        }
        g2.dispose();
        return inv;
    }

    /**
     * 获取玩家背包贴图
     * 不存在自动复制jar中的图片
     *
     * @return 玩家没报贴图
     */
    private static BufferedImage getCustomImg(String name) {
        File f = new File(ViewInv.instance.getDataFolder().getPath() + File.separator + name);
        if (!f.exists()) try (
                InputStream is = ViewInv.class.getClassLoader().getResourceAsStream("textures/" + name);
                FileOutputStream fos = new FileOutputStream(f)) {
            assert is != null;
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1)
                fos.write(buffer, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从皮肤URL中读取图片
     *
     * @param url 皮肤URL
     * @return 皮肤图片
     */
    public static BufferedImage getSkin(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取物品的药水效果
     *
     * @param is 物品
     * @return 药水效果
     */
    public static String getPotion(ItemStack is) {
        ItemMeta im = is.getItemMeta();
        if (im == null || !im.toString().contains("=minecraft:")) return "";
        return im.toString().split("=minecraft:")[1].split("}")[0].toUpperCase();
    }

    /**
     * <背包下标,坐标>
     */
    public static final int[][] l = {
            //快捷栏
            {24, 426}, {78, 426}, {132, 426}, {186, 426}, {240, 426}, {294, 426}, {348, 426}, {402, 426}, {456, 426},
            //背包
            {24, 252}, {78, 252}, {132, 252}, {186, 252}, {240, 252}, {294, 252}, {348, 252}, {402, 252}, {456, 252},
            {24, 306}, {78, 306}, {132, 306}, {186, 306}, {240, 306}, {294, 306}, {348, 306}, {402, 306}, {456, 306},
            {24, 360}, {78, 360}, {132, 360}, {186, 360}, {240, 360}, {294, 360}, {348, 360}, {402, 360}, {456, 360},
            //装备栏
            {24, 186}, {24, 132}, {24, 78}, {24, 24},
            //副手
            {231, 186},
    };

    /**
     * <末影箱下标,坐标>
     */
    public static final int[][] e = {
            {24, 24}, {78, 24}, {132, 24}, {186, 24}, {240, 24}, {294, 24}, {348, 24}, {402, 24}, {456, 24},
            {24, 78}, {78, 78}, {132, 78}, {186, 78}, {240, 78}, {294, 78}, {348, 78}, {402, 78}, {456, 78},
            {24, 132}, {78, 132}, {132, 132}, {186, 132}, {240, 132}, {294, 132}, {348, 132}, {402, 132}, {456, 132},
    };

    public static Player getPlayer(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) return p;
        OfflinePlayer off = loadOffPlayer(uuid);
        return off == null ? null : loadPlayer(off);
    }

    /**
     * 从resource中读取图片
     *
     * @param path 图片路径
     * @return 图片
     */
    public static BufferedImage getResourcesImg(String path) {
        String name = path.substring(path.lastIndexOf("/") + 1);
        try (InputStream is = ViewInv.instance.getResource(path)) {
            if (is != null) return ImageIO.read(is);
            Bukkit.getLogger().warning("未找到" + name + ".png , 将使用AIR.png");
            return getResourcesImg("textures/AIR.png");
        } catch (IOException e) {
            Bukkit.getLogger().warning("读取" + name + ".png 时发生异常, 将使用AIR.png");
            e.printStackTrace();
            return getResourcesImg("textures/AIR.png");
        }
    }

    /**
     * 通过uuid获取Player对象
     *
     * @param uuid 玩家uuid
     * @return Player对象
     */
    public static Player loadOffPlayer(UUID uuid) {
        OfflinePlayer off = matchPlayer(uuid);
        if (off == null) return null;
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

    /**
     * 通过uuid获取离线玩家对象
     *
     * @param uuid 玩家uuid
     * @return 离线玩家
     */
    public static OfflinePlayer matchPlayer(UUID uuid) {
        OfflinePlayer player;

        player = Bukkit.getServer().getPlayer(uuid);
        if (player != null) return player;

        player = Bukkit.getServer().getOfflinePlayer(uuid);
        if (player.hasPlayedBefore()) return player;

        return null;
    }
}
