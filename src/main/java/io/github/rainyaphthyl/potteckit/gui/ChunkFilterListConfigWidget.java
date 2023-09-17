package io.github.rainyaphthyl.potteckit.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkEvent;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.config.option.ChunkFilterEntry;
import io.github.rainyaphthyl.potteckit.config.option.ChunkFilterListConfig;
import io.github.rainyaphthyl.potteckit.config.option.MultiPartListConfig;
import net.minecraft.world.DimensionType;

public class ChunkFilterListConfigWidget extends MultiPartListConfigWidget<ChunkFilterEntry> {
    public ChunkFilterListConfigWidget(ChunkFilterListConfig config, DataListEntryWidgetData constructData, ConfigWidgetContext ctx) {
        super(config, constructData, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, MultiPartListConfig<ChunkFilterEntry> config, ConfigWidgetContext ctx) {
        return new ChunkFilterEditButton(width, height, config, this::updateWidgetState,
                () -> ChunkFilterEntry.NULL_WHITE,
                (iv, cd, dv) -> {
                    MultiPartListEntryEditWidget.PartBundle<Object> dimensionFactoryBundle = MultiPartListEntryEditWidget.PartBundle.createEnumObjectFactories(DimensionType.class, value -> value != null ? value.getName() : "<all>", null);
                    return new MultiPartListEntryEditWidget<>(iv, cd, dv, ImmutableList.of(
                            new MultiPartListEntryEditWidget.PartBundle<>(
                                    Boolean.class,
                                    ImmutableList.of(false, true),
                                    value -> value == Boolean.TRUE ? "reject" : "accept",
                                    null),
                            dimensionFactoryBundle,
                            MultiPartListEntryEditWidget.PartBundle.createEnumObjectFactories(
                                    GamePhase.class, value -> value.description, null),
                            MultiPartListEntryEditWidget.PartBundle.createEnumObjectFactories(
                                    ChunkEvent.class, value -> value.name, null),
                            dimensionFactoryBundle
                    ), null);
                },
                "Title Test"
        );
    }
}
