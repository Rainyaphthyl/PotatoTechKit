package io.github.rainyaphthyl.potteckit.gui;

import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.BaseModelWidget;
import net.minecraft.entity.Entity;

public class EntityWidget extends BaseModelWidget {
    protected Class<? extends Entity> entityClass;

    public EntityWidget(Class<? extends Entity> entityClass) {
        this(16, entityClass);
    }

    public EntityWidget(int dimensions, Class<? extends Entity> entityClass) {
        super(dimensions);
        this.entityClass = entityClass;
    }

    @Override
    protected void renderModel(int x, int y, float z, float scale, ScreenContext ctx) {
    }
}
