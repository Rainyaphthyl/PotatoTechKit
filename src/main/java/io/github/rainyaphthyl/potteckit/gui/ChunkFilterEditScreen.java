package io.github.rainyaphthyl.potteckit.gui;

import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.gui.config.BaseValueListEditScreen;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.listener.EventListener;
import io.github.rainyaphthyl.potteckit.config.option.ChunkFilterEntry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ChunkFilterEditScreen extends BaseValueListEditScreen<ChunkFilterEntry> {
    public ChunkFilterEditScreen(String title, ValueListConfig<ChunkFilterEntry> config, @Nullable EventListener saveListener, Supplier<ChunkFilterEntry> newEntrySupplier, ValueListEditEntryWidgetFactory<ChunkFilterEntry> widgetFactory) {
        super(title, config, saveListener, newEntrySupplier, widgetFactory);
    }

    @Override
    protected DataListWidget<ChunkFilterEntry> createListWidget() {
        DataListWidget<ChunkFilterEntry> listWidget = super.createListWidget();
        listWidget.setDataListEntryWidgetFactory((data, constructData) -> {
            List<ChunkFilterEntry> defaultList = this.config.getDefaultValue();
            int index = constructData.listIndex;
            ChunkFilterEntry defaultValue = index < defaultList.size() ? defaultList.get(index) : newEntrySupplier.get();
            return widgetFactory.create(data, constructData, defaultValue);
        });
        return listWidget;
    }
}
