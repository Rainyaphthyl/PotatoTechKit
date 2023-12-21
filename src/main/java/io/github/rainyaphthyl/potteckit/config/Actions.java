package io.github.rainyaphthyl.potteckit.config;

import fi.dy.masa.malilib.action.ActionUtils;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import io.github.rainyaphthyl.potteckit.gui.GuiConfigScreen;
import io.github.rainyaphthyl.potteckit.mixin.access.AccessGuiContainer;
import io.github.rainyaphthyl.potteckit.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class Actions {
    public static final NamedAction OPEN_CONFIG_SCREEN = ActionUtils.register(Reference.MOD_INFO, "openConfigScreen", () -> BaseScreen.openScreen(GuiConfigScreen.create()));
    public static final NamedAction TOGGLE_ANVIL_MATERIAL = ActionUtils.register(Reference.MOD_INFO, "anvilEnchantTrigger", () -> {
        if (Configs.anvilEnchantIndicator.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            Minecraft minecraft = Minecraft.getMinecraft();
            GuiScreen guiScreen = minecraft.currentScreen;
            if (guiScreen instanceof GuiRepair) {
                GuiRepair guiAnvil = (GuiRepair) guiScreen;
                if (guiScreen instanceof AccessGuiContainer) {
                    Slot hoveredSlot = ((AccessGuiContainer) guiScreen).getHoveredSlot();
                    if (hoveredSlot != null) {
                        ItemStack itemStack = hoveredSlot.getStack();
                        if (!itemStack.isEmpty()) {
                            System.out.println(itemStack.getDisplayName());
                        }
                    }
                }
            }
        }
    });

    public static void init() {
    }
}
