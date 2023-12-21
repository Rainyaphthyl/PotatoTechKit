package io.github.rainyaphthyl.potteckit.calculator;

import com.google.common.collect.Sets;
import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.mixin.access.AccessGuiContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
                    if (!itemStack.isEmpty()) {
                        Item item = itemStack.getItem();
                        if (item == Items.ENCHANTED_BOOK || itemStack.isItemEnchantable() || itemStack.isItemEnchanted()) {
                            if (slotsToEnchant.contains(slot)) {
                                slotsToEnchant.remove(slot);
                            } else {
                                slotsToEnchant.add(slot);
                            }
                        }
                    }
                }
            }
        }
    }
}
