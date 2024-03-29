package dev.xhyrom.peddlerspocket.api;

import dev.xhyrom.peddlerspocket.PeddlersPocket;
import dev.xhyrom.peddlerspocket.structs.Result;
import dev.xhyrom.peddlerspocket.structs.Action;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class PeddlersPocketAPI {
    public static ArrayList<ItemStack> sell(Player player, ItemStack[] items) {
        ArrayList<ItemStack> notSold = new ArrayList<>();

        double result = 0;
        for (ItemStack item : items) {
            if (item == null) continue;
            if (item.getType() == Material.AIR) continue;

            if (isSellable(item.getType())) {
                double price = getPrice(item.getType()) * item.getAmount();
                result += price;
            } else {
                notSold.add(item);
            }
        }

        if (result > 0) {
            for (Action action : PeddlersPocket.getInstance().getActions().get(Result.SUCCESS)) {
                action.execute(player, result);
            }
        } else {
            for (Action action : PeddlersPocket.getInstance().getActions().get(Result.FAIL)) {
                action.execute(player, result);
            }
        }

        return notSold;
    }

    /**
     * Check if an item is sellable
     *
     * @param material The item to check
     * @return `true` if the item is sellable, `false` otherwise
     */
    public static boolean isSellable(Material material) {
        return PeddlersPocket.getInstance().getPrices().containsKey(material);
    }

    /**
     * Get the price of an item
     *
     * @param material The item to get the price of
     * @return The price of the item, or `null` if the item is not sellable
     */
    @Nullable
    public static Double getPrice(Material material) {
        return PeddlersPocket.getInstance().getPrices().get(material);
    }

    /**
     * Get the price of an item, or a default value if the item is not sellable
     *
     * @param material The item to get the price of
     * @param defaultValue The default value to return if the item is not sellable
     * @return The price of the item, or the default value if the item is not sellable
     */
    @Nullable
    public static Double getPriceOrDefault(Material material, double defaultValue) {
        return PeddlersPocket.getInstance().getPrices().getOrDefault(material, defaultValue);
    }
}
