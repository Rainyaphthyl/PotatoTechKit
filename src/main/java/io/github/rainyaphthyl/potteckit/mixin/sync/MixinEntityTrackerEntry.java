package io.github.rainyaphthyl.potteckit.mixin.sync;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.util.restriction.UsageRestriction;
import io.github.rainyaphthyl.potteckit.config.Configs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTrackerEntry;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityTrackerEntry.class)
public abstract class MixinEntityTrackerEntry {
    @Shadow
    @Final
    private int range;

    @Redirect(method = "isVisibleTo", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/EntityTrackerEntry;range:I"))
    public int onCheckTrackerDistance(EntityTrackerEntry instance) {
        if (Configs.entityTrackerDistance.getBooleanValue() && Configs.enablePotteckit.getBooleanValue()) {
            BlackWhiteList<Class<? extends Entity>> filter = Configs.entityTrackerTweakList.getValue();
            UsageRestriction.ListType listType = filter.getListType();
            ValueListConfig<Class<? extends Entity>> activeList = filter.getActiveList();
            if (activeList != null) {
                Class<? extends Entity> entityType = instance.getTrackedEntity().getClass();
                ImmutableList<Class<? extends Entity>> list = activeList.getValue();
                if (listType == UsageRestriction.ListType.BLACKLIST) {
                    if (list.contains(entityType)) {
                        return range;
                    }
                } else if (listType == UsageRestriction.ListType.WHITELIST) {
                    if (!list.contains(entityType)) {
                        return range;
                    }
                }
            }
            return Configs.entityTrackerDistance.getIntegerValue() << 4;
        } else {
            return range;
        }
    }
}
