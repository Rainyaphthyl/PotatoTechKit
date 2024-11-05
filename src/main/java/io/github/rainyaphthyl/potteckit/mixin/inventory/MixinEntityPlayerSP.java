package io.github.rainyaphthyl.potteckit.mixin.inventory;

import com.mojang.authlib.GameProfile;
import fi.dy.masa.malilib.util.inventory.InventoryUtils;
import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
    @Shadow
    private boolean wasFallFlying;
    @Unique
    private ItemStack potatoTechKit$cachedChestStack = ItemStack.EMPTY;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    public void autoDeployElytra(CallbackInfo ci) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.autoSwapElytraChestplate.getBooleanValue()) {
            if (isInWater()) {
                ItemStack chestStack = getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                if (chestStack.getItem() instanceof ItemArmor) {
                    ItemStack elytraStack = ItemStack.EMPTY;
                    Slot elytraSlot = null;
                    int prevDurability = 0;
                    boolean prevMending = false;
                    for (int i = -1; i < 36; ++i) {
                        int id = i == -1 ? 45 : (i < 9 ? i + 36 : i);
                        Slot currSlot = inventoryContainer.getSlot(id);
                        ItemStack currStack = currSlot.getStack();
                        if (!currStack.isEmpty() && currStack.getItem() == Items.ELYTRA) {
                            int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, currStack);
                            int damage = currStack.getItemDamage();
                            boolean mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, currStack) > 0;
                            int durability = (currStack.getMaxDamage() - damage) * (level + 1);
                            // find the first of the best elytra(s):
                            // best remaining durability: (remaining durability) * (unbreaking grade)
                            // mending is better
                            if (elytraStack.isEmpty()
                                    || durability > prevDurability
                                    || durability == prevDurability && !prevMending && mending) {
                                elytraSlot = currSlot;
                                elytraStack = currStack;
                                prevDurability = durability;
                                prevMending = mending;
                            }
                            if (mending && level >= Enchantments.UNBREAKING.getMaxLevel() && damage == 0) {
                                // the perfect elytra
                                break;
                            }
                        }
                    }
                    if (elytraSlot != null && !elytraStack.isEmpty()) {
                        //swap Elytra & Chestplate
                        Slot chestSlot = inventoryContainer.getSlot(6);
                        int slotNumber = elytraSlot.slotNumber;
                        if (slotNumber >= 36 && slotNumber < 45) {
                            // hot-bar
                            InventoryUtils.clickSlot(inventoryContainer, chestSlot, slotNumber - 36, ClickType.SWAP);
                        } else {
                            int currentItem = inventory.currentItem;
                            InventoryUtils.clickSlot(inventoryContainer, elytraSlot, currentItem, ClickType.SWAP);
                            InventoryUtils.clickSlot(inventoryContainer, chestSlot, currentItem, ClickType.SWAP);
                            InventoryUtils.clickSlot(inventoryContainer, elytraSlot, currentItem, ClickType.SWAP);
                        }
                        playEquipSound(elytraStack);
                        potatoTechKit$cachedChestStack = chestStack;
                    }
                }
            }
        }
    }

    @Inject(method = "notifyDataManagerChange", at = @At(value = "RETURN"))
    public void autoDeployChestplate(DataParameter<?> key, CallbackInfo ci) {
        if (Configs.enablePotteckit.getBooleanValue() && Configs.autoSwapElytraChestplate.getBooleanValue()) {
            if (FLAGS.equals(key) && !isElytraFlying() && wasFallFlying) {
                // the new data have been refreshed during EntityDataManager.setEntryValue()
                ItemStack elytraStack = getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                Slot chestSlot = null;
                ItemStack chestStack = ItemStack.EMPTY;
                if (elytraStack.getItem() == Items.ELYTRA) {
                    for (int i = -1; i < 36; ++i) {
                        int id = i == -1 ? 45 : (i < 9 ? i + 36 : i);
                        Slot currSlot = inventoryContainer.getSlot(id);
                        ItemStack currStack = currSlot.getStack();
                        if (!currStack.isEmpty()) {
                            Item currItem = currStack.getItem();
                            if (currItem instanceof ItemArmor
                                    && ((ItemArmor) currItem).armorType == EntityEquipmentSlot.CHEST
                                    && InventoryUtils.areStacksEqualIgnoreDurability(potatoTechKit$cachedChestStack, currStack, false)) {
                                chestSlot = currSlot;
                                chestStack = currStack;
                                break;
                            }
                        }
                    }
                }
                if (chestSlot != null && !chestStack.isEmpty()) {
                    Slot elytraSlot = inventoryContainer.getSlot(6);
                    int slotNumber = chestSlot.slotNumber;
                    if (slotNumber >= 36 && slotNumber < 45) {
                        // hot-bar
                        InventoryUtils.clickSlot(inventoryContainer, elytraSlot, slotNumber - 36, ClickType.SWAP);
                    } else {
                        int currentItem = inventory.currentItem;
                        InventoryUtils.clickSlot(inventoryContainer, chestSlot, currentItem, ClickType.SWAP);
                        InventoryUtils.clickSlot(inventoryContainer, elytraSlot, currentItem, ClickType.SWAP);
                        InventoryUtils.clickSlot(inventoryContainer, chestSlot, currentItem, ClickType.SWAP);
                    }
                    playEquipSound(chestStack);
                    potatoTechKit$cachedChestStack = ItemStack.EMPTY;
                }
            }
        }
    }
}
