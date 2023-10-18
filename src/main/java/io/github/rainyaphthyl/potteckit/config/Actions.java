package io.github.rainyaphthyl.potteckit.config;

import fi.dy.masa.malilib.action.ActionUtils;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import io.github.rainyaphthyl.potteckit.gui.GuiConfigScreen;
import io.github.rainyaphthyl.potteckit.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Actions {
    public static final NamedAction OPEN_CONFIG_SCREEN = ActionUtils.register(Reference.MOD_INFO, "openConfigScreen", () -> BaseScreen.openScreen(GuiConfigScreen.create()));
    public static final NamedAction INDICATE_PROJECTILE = ActionUtils.register(Reference.MOD_INFO, "indicateProjectile", () -> {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP playerSP = mc.player;
        if (playerSP != null) {
            ItemStack itemStack = playerSP.getHeldItemMainhand();
            Item item = itemStack.getItem();
            if (Configs.projectileAimList.getValue().contains(item)) {
                MessageOutput.CHAT.send("Projectile aim helper is started", MessageDispatcher.generic());
                //ArrowSimulator simulator = new ArrowSimulator(playerSP, mc.world);
                //Renderers.PROJECTILE_AIM_RENDERER.aimListMap.clear();
                //Renderers.PROJECTILE_AIM_RENDERER.aimDamageMap.clear();
                //simulator.predictDestination(3.0F, 1.0F);
                //Renderers.PROJECTILE_AIM_RENDERER.updateCounter.incrementAndGet();
            }
        }
    });

    public static void init() {
    }
}
