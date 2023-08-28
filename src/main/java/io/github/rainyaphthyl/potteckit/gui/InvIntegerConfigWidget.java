package io.github.rainyaphthyl.potteckit.gui;

import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.gui.widget.list.entry.config.IntegerConfigWidget;

public class InvIntegerConfigWidget extends IntegerConfigWidget {
    public InvIntegerConfigWidget(IntegerConfig config, DataListEntryWidgetData constructData, ConfigWidgetContext ctx) {
        super(config, constructData, ctx);
    }
}
