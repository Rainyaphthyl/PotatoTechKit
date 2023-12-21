package io.github.rainyaphthyl.potteckit.mixin.inventory;

import com.google.common.collect.ImmutableList;
import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiContainerCreative.class)
public abstract class MixinGuiContainerCreative extends InventoryEffectRenderer {
    public MixinGuiContainerCreative(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Redirect(method = "handleMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;sendSlotPacket(Lnet/minecraft/item/ItemStack;I)V", ordinal = 0))
    public void checkItemDeletion(PlayerControllerMP instance, ItemStack itemStackIn, int slotId) {
        boolean deleting = true;
        if (Configs.protectCreativeSlots.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            EntityEquipmentSlot slot;
            switch (slotId) {
                case 5:
                    slot = EntityEquipmentSlot.HEAD;
                    break;
                case 6:
                    slot = EntityEquipmentSlot.CHEST;
                    break;
                case 7:
                    slot = EntityEquipmentSlot.LEGS;
                    break;
                case 8:
                    slot = EntityEquipmentSlot.FEET;
                    break;
                case 45:
                    slot = EntityEquipmentSlot.OFFHAND;
                    break;
                default:
                    slot = (slotId == mc.player.inventory.currentItem + 36) ? EntityEquipmentSlot.MAINHAND : null;
            }
            if (slot != null) {
                ImmutableList<EntityEquipmentSlot> protectList = Configs.protectCreativeSlotList.getValue();
                if (protectList.contains(slot)) {
                    deleting = false;
                }
            }
        }
        if (deleting) {
            instance.sendSlotPacket(itemStackIn, slotId);
        }
    }
}
