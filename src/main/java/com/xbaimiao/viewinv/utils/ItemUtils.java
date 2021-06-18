package com.xbaimiao.viewinv.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemUtils {

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

}
