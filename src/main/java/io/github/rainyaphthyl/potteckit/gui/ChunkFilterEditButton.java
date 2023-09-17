package io.github.rainyaphthyl.potteckit.gui;

import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.BaseValueListEditScreen;
import fi.dy.masa.malilib.gui.widget.button.BaseValueListEditButton;
import fi.dy.masa.malilib.listener.EventListener;
import io.github.rainyaphthyl.potteckit.config.option.ChunkFilterEntry;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ChunkFilterEditButton extends BaseValueListEditButton<ChunkFilterEntry> {
    public ChunkFilterEditButton(int width, int height, ValueListConfig<ChunkFilterEntry> config, @Nullable EventListener saveListener, Supplier<ChunkFilterEntry> newEntryFactory, BaseValueListEditScreen.ValueListEditEntryWidgetFactory<ChunkFilterEntry> widgetFactory, String screenTitle) {
        super(width, height, config, saveListener, newEntryFactory, widgetFactory, screenTitle);
    }

    @Override
    protected BaseScreen createScreen() {
        return super.createScreen();
    }
}
