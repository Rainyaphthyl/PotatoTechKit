package io.github.rainyaphthyl.potteckit.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.HorizontalAlignment;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseOrderableListEditEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import io.github.rainyaphthyl.potteckit.config.option.MultiPartEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Reference: {@link fi.dy.masa.malilib.gui.widget.list.entry.BaseValueListEditEntryWidget}
 *
 * @param <ENTRY> the multipart entry type
 */
public class MultiPartListEntryEditWidget<ENTRY extends MultiPartEntry<ENTRY>> extends BaseOrderableListEditEntryWidget<ENTRY> {
    protected final ENTRY defaultValue;
    protected final ENTRY initialValue;
    protected final List<DropDownListWidget<Object>> dropDownWidgetList = new ArrayList<>();
    protected final GenericButton resetButton;

    public MultiPartListEntryEditWidget(ENTRY initialValue, DataListEntryWidgetData constructData, ENTRY defaultValue, List<PartBundle<Object>> partBundleList, DropDownListWidget.IconWidgetFactory<ENTRY> globalIconWidgetFactory) {
        super(initialValue, constructData);
        this.defaultValue = defaultValue;
        this.initialValue = initialValue;
        newEntryFactory = () -> this.defaultValue;
        resetButton = GenericButton.create(16, "malilib.button.misc.reset.caps");
        initializeSubWidgets(partBundleList);
    }

    private void initializeSubWidgets(@Nonnull List<PartBundle<Object>> partBundleList) {
        labelWidget = new LabelWidget(0xC0C0C0C0, String.format("%3d:", originalListIndex + 1));
        //labelWidget.setAutomaticWidth(false);
        //labelWidget.setWidth(24);
        labelWidget.setAutomaticWidth(true);
        labelWidget.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        resetButton.getBorderRenderer().getNormalSettings().setBorderWidthAndColor(1, 0xFF404040);
        resetButton.setRenderButtonBackgroundTexture(false);
        resetButton.setDisabledTextColor(0xFF505050);
        int ddWidth = getWidth() - resetButton.getWidth() - labelWidget.getWidth()
                - addButton.getWidth() - removeButton.getWidth()
                - upButton.getWidth() - downButton.getWidth() - 20;
        for (int i = 0, size = partBundleList.size(); i < size; ++i) {
            DropDownListWidget<Object> dropDownWidget = getDropDownWidget(partBundleList, i, ddWidth);
            dropDownWidgetList.add(dropDownWidget);
        }
    }

    @Nonnull
    private <DATA> DropDownListWidget<DATA> getDropDownWidget(@Nonnull List<PartBundle<DATA>> partBundleList, int index, int ddWidth) {
        PartBundle<DATA> partBundle = partBundleList.get(index);
        DropDownListWidget<DATA> dropDownWidget = new DropDownListWidget<>(18, 12, partBundle.possibleValues, partBundle.toStringConverter, partBundle.iconWidgetFactory);
        dropDownWidget.setMaxWidth(ddWidth);
        dropDownWidget.setSelectedEntry(partBundle.type.cast(initialValue.getValue(index)));
        dropDownWidget.setSelectionListener(value -> {
            if (originalListIndex < dataList.size()) {
                ENTRY previous = dataList.get(originalListIndex);
                ENTRY updated = previous.copyModified(index, value);
                dataList.set(originalListIndex, updated);
            }
            resetButton.setEnabled(!defaultValue.equals(value));
        });
        return dropDownWidget;
    }

    @Override
    public void reAddSubWidgets() {
        super.reAddSubWidgets();
        for (DropDownListWidget<?> dropDownWidget : dropDownWidgetList) {
            addWidget(dropDownWidget);
        }
        addWidget(resetButton);
    }

    @Override
    protected void updateSubWidgetsToGeometryChangesPre(int x, int y) {
        if (labelWidget != null) {
            labelWidget.setPosition(getX() + 2, y + 6);
        }
        nextWidgetX = x;
        for (DropDownListWidget<?> dropDownWidget : dropDownWidgetList) {
            dropDownWidget.setPosition(nextWidgetX, y + 1);
            nextWidgetX = dropDownWidget.getRight() + 2;
        }
    }

    @Override
    protected void updateSubWidgetsToGeometryChangesPost(int x, int y) {
        resetButton.setPosition(x, y + 2);
    }

    /**
     * @param <DATA> the partial data type
     */
    public static class PartBundle<DATA> {
        public final Class<? extends DATA> type;
        public final List<DATA> possibleValues;
        public final Function<DATA, String> toStringConverter;
        public final DropDownListWidget.IconWidgetFactory<DATA> iconWidgetFactory;

        public PartBundle(Class<? extends DATA> type, List<DATA> possibleValues, Function<DATA, String> toStringConverter, @Nullable DropDownListWidget.IconWidgetFactory<DATA> iconWidgetFactory) {
            this.type = type;
            this.possibleValues = Objects.requireNonNull(possibleValues);
            this.toStringConverter = Objects.requireNonNull(toStringConverter);
            this.iconWidgetFactory = iconWidgetFactory;
        }

        @Nonnull
        public static <E extends Enum<E>> PartBundle<E> createEnumFactories(Class<E> enumClass, Function<E, String> toStringConverter, @Nullable DropDownListWidget.IconWidgetFactory<E> iconWidgetFactory) {
            return new PartBundle<E>(
                    enumClass,
                    ImmutableList.copyOf(enumClass.getEnumConstants()),
                    value -> value == null ? "<all>" : toStringConverter.apply(value),
                    iconWidgetFactory
            );
        }

        @Nonnull
        public static <E extends Enum<E>> PartBundle<Object> createEnumObjectFactories(Class<E> enumClass, Function<E, String> toStringConverter, @Nullable DropDownListWidget.IconWidgetFactory<E> iconWidgetFactory) {
            return new PartBundle<>(
                    enumClass,
                    ImmutableList.copyOf(enumClass.getEnumConstants()),
                    value -> enumClass.isAssignableFrom(value.getClass()) ? toStringConverter.apply(enumClass.cast(value)) : "<all>",
                    iconWidgetFactory == null ? null : (
                            value -> enumClass.isAssignableFrom(value.getClass()) ? iconWidgetFactory.create(enumClass.cast(value)) : null
                    )
            );
        }
    }

    //protected final DATATYPE defaultValue;
    //protected final DATATYPE initialValue;
    //protected final DropDownListWidget<DATATYPE> dropDownWidget;
    //protected final GenericButton resetButton;
    //
    //public BaseValueListEditEntryWidget(DATATYPE initialValue,
    //                                    DataListEntryWidgetData constructData,
    //                                    DATATYPE defaultValue,
    //                                    List<DATATYPE> possibleValues,
    //                                    Function<DATATYPE, String> toStringConverter,
    //                                    @Nullable DropDownListWidget.IconWidgetFactory<DATATYPE> iconWidgetFactory)
    //{
    //    super(initialValue, constructData);
    //
    //    this.defaultValue = defaultValue;
    //    this.initialValue = initialValue;
    //    this.newEntryFactory = () -> this.defaultValue;
    //
    //    this.labelWidget = new LabelWidget(0xC0C0C0C0, String.format("%3d:", this.originalListIndex + 1));
    //    this.labelWidget.setAutomaticWidth(false);
    //    this.labelWidget.setWidth(24);
    //    this.labelWidget.setHorizontalAlignment(HorizontalAlignment.RIGHT);
    //
    //    this.resetButton = GenericButton.create(16, "malilib.button.misc.reset.caps");
    //    this.resetButton.getBorderRenderer().getNormalSettings().setBorderWidthAndColor(1, 0xFF404040);
    //
    //    this.resetButton.setRenderButtonBackgroundTexture(false);
    //    this.resetButton.setDisabledTextColor(0xFF505050);
    //
    //    int ddWidth = this.getWidth() - this.resetButton.getWidth() - this.labelWidget.getWidth()
    //            - this.addButton.getWidth() - this.removeButton.getWidth()
    //            - this.upButton.getWidth() - this.downButton.getWidth() - 20;
    //    this.dropDownWidget = new DropDownListWidget<>(18, 12, possibleValues, toStringConverter, iconWidgetFactory);
    //
    //    this.dropDownWidget.setMaxWidth(ddWidth);
    //    this.dropDownWidget.setSelectedEntry(this.initialValue);
    //    this.dropDownWidget.setSelectionListener((entry) -> {
    //        if (this.originalListIndex < this.dataList.size())
    //        {
    //            this.dataList.set(this.originalListIndex, entry);
    //        }
    //
    //        this.resetButton.setEnabled(this.defaultValue.equals(entry) == false);
    //    });
    //
    //    this.resetButton.setEnabled(initialValue.equals(this.defaultValue) == false);
    //    this.resetButton.setActionListener(() -> {
    //        this.dropDownWidget.setSelectedEntry(this.defaultValue);
    //
    //        if (this.originalListIndex < this.dataList.size())
    //        {
    //            this.dataList.set(this.originalListIndex, this.defaultValue);
    //        }
    //
    //        this.resetButton.setEnabled(this.defaultValue.equals(this.dropDownWidget.getSelectedEntry()) == false);
    //    });
    //}
    //
    //@Override
    //public void reAddSubWidgets()
    //{
    //    super.reAddSubWidgets();
    //
    //    this.addWidget(this.dropDownWidget);
    //    this.addWidget(this.resetButton);
    //}
    //
    //@Override
    //protected void updateSubWidgetsToGeometryChangesPre(int x, int y)
    //{
    //    this.labelWidget.setPosition(this.getX() + 2, y + 6);
    //    this.dropDownWidget.setPosition(x, y + 1);
    //    this.nextWidgetX = this.dropDownWidget.getRight() + 2;
    //}
    //
    //@Override
    //protected void updateSubWidgetsToGeometryChangesPost(int x, int y)
    //{
    //    this.resetButton.setPosition(x, y + 2);
    //}
}
