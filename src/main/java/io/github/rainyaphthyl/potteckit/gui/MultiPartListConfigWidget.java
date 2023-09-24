package io.github.rainyaphthyl.potteckit.gui;

import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.gui.widget.list.entry.config.list.BaseValueListConfigWidget;
import io.github.rainyaphthyl.potteckit.config.option.multipart.MultiPartEntry;
import io.github.rainyaphthyl.potteckit.config.option.multipart.MultiPartListConfig;

public abstract class MultiPartListConfigWidget<ENTRY extends MultiPartEntry<ENTRY>> extends BaseValueListConfigWidget<ENTRY, MultiPartListConfig<ENTRY>> {
    public MultiPartListConfigWidget(MultiPartListConfig<ENTRY> config, DataListEntryWidgetData constructData, ConfigWidgetContext ctx) {
        super(config, constructData, ctx);
    }

    @Override
    protected abstract GenericButton createButton(int width, int height, MultiPartListConfig<ENTRY> config, ConfigWidgetContext ctx);
}
