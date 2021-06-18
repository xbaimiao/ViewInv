package com.xbaimiao.viewinv.skull;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.xbaimiao.viewinv.ImageUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Skull {

    public static String getBase64(SkullMeta meta) {
        Class<?> clazz = meta.getClass();
        try {
            Field field = clazz.getDeclaredField("profile");
            field.setAccessible(true);
            GameProfile gameProfile = (GameProfile) field.get(meta);
            Collection<Property> property = gameProfile.getProperties().get("textures");
            if (property == null) {
                return null;
            }
            for (Property property1 : property) {
                return property1.getValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUrl(String base64) {
        String json = new String(Base64.getDecoder().decode(base64));
        Pattern pattern = Pattern.compile("(http://.*?)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            String url = matcher.group(1);
            url = url.replace("http://textures.minecraft.net/texture/", "");
            url = "https://mc-heads.net/head/%s/96".replaceFirst("%s", url);
            return url;
        }
        return null;
    }

    public static BufferedImage skullImage(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof SkullMeta)) {
            return null;
        }
        BufferedImage itemImage = null;
        String url = getUrl(getBase64((SkullMeta) itemMeta));
        try {
            BufferedImage newSkull = ImageUtils.multiply(ImageUtils.downloadImage(url), 0.9);
            BufferedImage newImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = newImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(newSkull, 5, 3, 24, 28, null);
            g2.dispose();
            itemImage = ImageUtils.copyImage(newImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return itemImage;
    }

}
