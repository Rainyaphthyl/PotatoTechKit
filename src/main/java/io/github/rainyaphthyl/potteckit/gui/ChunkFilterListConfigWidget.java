package io.github.rainyaphthyl.potteckit.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import io.github.rainyaphthyl.potteckit.chunkphase.chunkgraph.ChunkEvent;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.GamePhase;
import io.github.rainyaphthyl.potteckit.chunkphase.phaseclock.TickRecord;
import io.github.rainyaphthyl.potteckit.config.option.multipart.*;
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
                    MultiPartListEntryEditWidget.PartBundle<Object> dimensionFactoryBundle = MultiPartListEntryEditWidget.PartBundle.createEnumObjectFactories(DimensionType.class, value -> {
                        if (value == null) {
                            return "<all>";
                        } else {
                            return String.valueOf(TickRecord.getDimensionChar(value));
                        }
                    }, null);
                    ImmutableList.Builder<PartialValue<Object>> builder = ImmutableList.builder();
                    for (int depth = 0; depth < 8; ++depth) {
                        builder.add(new WrappedValue<>(depth));
                    }
                    ImmutableList<PartialValue<Object>> repeatLevelList = builder.build();
                    return new MultiPartListEntryEditWidget<>(iv, cd, dv, ImmutableList.of(
                            new MultiPartListEntryEditWidget.PartBundle<>(
                                    Boolean.class,
                                    ImmutableList.of(new WrappedValue<>(true), new WrappedValue<>(false)),
                                    value -> value == Boolean.TRUE ? "except" : "ignore",
                                    null),
                            dimensionFactoryBundle,
                            MultiPartListEntryEditWidget.PartBundle.createEnumObjectFactories(
                                    GamePhase.class, value -> value.shortName, null),
                            MultiPartListEntryEditWidget.PartBundle.createEnumObjectFactories(
                                    ChunkEvent.class, value -> value.shortName, null),
                            dimensionFactoryBundle,
                            new MultiPartListEntryEditWidget.PartBundle<>(
                                    Integer.class, repeatLevelList, String::valueOf, null)
                    ), ChunkFilterEntry::fromObjectArray, null);
                },
                "Title Test"
        );
    }
}
