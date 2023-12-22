package io.github.rainyaphthyl.potteckit.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.BaseValueListEditButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.BaseValueListConfigWidget;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.rainyaphthyl.potteckit.config.option.EntityListConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityBat;

public class EntityListConfigWidget extends BaseValueListConfigWidget<Class<? extends Entity>, EntityListConfig> {
    public EntityListConfigWidget(EntityListConfig config, DataListEntryWidgetData constructData, ConfigWidgetContext ctx) {
        super(config, constructData, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, EntityListConfig config, ConfigWidgetContext ctx) {
        String title = StringUtils.translate("potteckit.title.screen.item_list_edit", this.config.getDisplayName());
        return new BaseValueListEditButton<>(width, height, config, this::updateWidgetState,
                () -> EntityBat.class,
                () -> {
                    ImmutableList.Builder<Class<? extends Entity>> builder = ImmutableList.builder();
                    for (Class<? extends Entity> eClass : EntityList.REGISTRY) {
                        builder.add(eClass);
                    }
                    return builder.build();
                },
                eClass -> String.valueOf(EntityList.getKey(eClass)),
                null, title
        );
    }
}
