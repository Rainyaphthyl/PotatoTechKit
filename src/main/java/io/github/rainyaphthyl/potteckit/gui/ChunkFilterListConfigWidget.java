package io.github.rainyaphthyl.potteckit.gui;

import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.BaseValueListEditButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseOrderableListEditEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.BaseValueListConfigWidget;
import io.github.rainyaphthyl.potteckit.config.option.ChunkFilterEntry;
import io.github.rainyaphthyl.potteckit.config.option.ChunkFilterListConfig;

public class ChunkFilterListConfigWidget extends BaseValueListConfigWidget<ChunkFilterEntry, ChunkFilterListConfig> {
    public ChunkFilterListConfigWidget(ChunkFilterListConfig config, DataListEntryWidgetData constructData, ConfigWidgetContext ctx) {
        super(config, constructData, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, ChunkFilterListConfig config, ConfigWidgetContext ctx) {
        return new BaseValueListEditButton<>(width, height, config,
                this::updateWidgetState,
                () -> null,
                (initialValue, constructData, defaultValue) -> {
                    return new BaseOrderableListEditEntryWidget<ChunkFilterEntry>(initialValue, constructData) {
                        @Override
                        public void reAddSubWidgets() {
                            super.reAddSubWidgets();
                        }
                    };
                },
                "Title"
        );
    }
}
