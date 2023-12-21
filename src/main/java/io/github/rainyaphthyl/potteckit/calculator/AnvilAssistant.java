package io.github.rainyaphthyl.potteckit.calculator;

import com.google.common.collect.Sets;
import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.mixin.access.AccessGuiContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class AnvilAssistant {
    public static final Set<Slot> slotsToEnchant = Sets.newLinkedHashSet();

    public static void toggleHoveredMaterial() {
        if (Configs.anvilEnchantIndicator.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            Minecraft minecraft = Minecraft.getMinecraft();
            GuiScreen guiScreen = minecraft.currentScreen;
            if (guiScreen instanceof GuiRepair && guiScreen instanceof AccessGuiContainer) {
                Slot slot = ((AccessGuiContainer) guiScreen).getHoveredSlot();
                if (slot != null) {
                    ItemStack itemStack = slot.getStack();
                    if (isValidMaterial(itemStack)) {
                        if (slotsToEnchant.contains(slot)) {
                            slotsToEnchant.remove(slot);
                        } else {
                            slotsToEnchant.add(slot);
                        }
                        if (!slotsToEnchant.isEmpty()) {
                            List<ItemStack> list = new ArrayList<>();
                            slotsToEnchant.forEach(s -> {
                                ItemStack inputStack = s.getStack();
                                if (isValidMaterial(inputStack)) {
                                    list.add(inputStack);
                                }
                            });
                            ItemStack[] order = findBestOrder(list);
                            System.out.println(Arrays.toString(order));
                        }
                    }
                }
            }
        }
    }

    public static boolean isValidMaterial(ItemStack itemStack) {
        return itemStack != null && !itemStack.isEmpty() && (itemStack.getItem() == Items.ENCHANTED_BOOK || itemStack.isItemEnchantable() || itemStack.isItemEnchanted());
    }

    public static ItemStack[] findBestOrder(List<ItemStack> rawStackList) {
        if (rawStackList == null) {
            return null;
        }
        int rawLength = 0;
        for (ItemStack stack : rawStackList) {
            if (stack == null || stack.isEmpty()) {
                return null;
            } else {
                rawLength += 1 + stack.getRepairCost();
            }
        }
        int treeDepth = MathHelper.log2DeBruijn(rawLength);
        int arrayLength = 1 << treeDepth;
        ItemStack[] array = new ItemStack[arrayLength];
        return array;
    }
}
