package io.github.rainyaphthyl.potteckit.gui;

import fi.dy.masa.malilib.config.value.HorizontalAlignment;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseOrderableListEditEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import io.github.rainyaphthyl.potteckit.config.option.multipart.MultiPartEntry;
import io.github.rainyaphthyl.potteckit.config.option.multipart.NullableEnum;
import io.github.rainyaphthyl.potteckit.config.option.multipart.PartialValue;
import io.github.rainyaphthyl.potteckit.config.option.multipart.WrappedValue;

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
    protected final List<DropDownListWidget<PartialValue<Object>>> dropDownWidgetList = new ArrayList<>();
    protected final GenericButton resetButton;
    protected final Function<Object[], ENTRY> constructor;

    public MultiPartListEntryEditWidget(ENTRY initialValue, DataListEntryWidgetData constructData, ENTRY defaultValue, List<PartBundle<Object>> partBundleList, Function<Object[], ENTRY> constructor, DropDownListWidget.IconWidgetFactory<ENTRY> globalIconWidgetFactory) {
        super(initialValue, constructData);
        this.defaultValue = defaultValue;
        this.initialValue = initialValue;
        newEntryFactory = () -> this.defaultValue;
        resetButton = GenericButton.create(16, "malilib.button.misc.reset.caps");
        this.constructor = constructor;
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
            DropDownListWidget<PartialValue<Object>> dropDownWidget = getDropDownWidget(partBundleList, i, ddWidth);
            dropDownWidgetList.add(dropDownWidget);
        }
        resetButton.setEnabled(!defaultValue.equals(initialValue));
        resetButton.setActionListener(() -> {
            int length = dropDownWidgetList.size();
            for (int i = 0; i < length; ++i) {
                DropDownListWidget<PartialValue<Object>> widget = dropDownWidgetList.get(i);
                Object object = defaultValue.getValue(i);
                widget.setSelectedEntry(new WrappedValue<>(object));
            }
            if (originalListIndex < dataList.size()) {
                dataList.set(originalListIndex, defaultValue);
            }
            Object[] selectedParts = new Object[length];
            for (int i = 0; i < length; ++i) {
                DropDownListWidget<PartialValue<Object>> widget = dropDownWidgetList.get(i);
                PartialValue<Object> updated = widget.getSelectedEntry();
                if (updated != null) {
                    selectedParts[i] = updated.getValue();
                }
            }
            ENTRY selected = constructor.apply(selectedParts);
            resetButton.setEnabled(!defaultValue.equals(selected));
        });
    }

    @Nonnull
    private DropDownListWidget<PartialValue<Object>> getDropDownWidget(@Nonnull List<PartBundle<Object>> partBundleList, int index, int ddWidth) {
        PartBundle<Object> partBundle = partBundleList.get(index);
        DropDownListWidget<PartialValue<Object>> dropDownWidget = new DropDownListWidget<>(18, 12, partBundle.possibleValues,
                option -> partBundle.toStringConverter.apply(option.getValue()),
                partBundle.iconWidgetFactory == null ? null : option -> partBundle.iconWidgetFactory.create(option.getValue())
        );
        dropDownWidget.setMaxWidth(ddWidth);
        dropDownWidget.setSelectedEntry(new WrappedValue<>(initialValue.getValue(index)));
        dropDownWidget.setSelectionListener(option -> {
            ENTRY updated = null;
            if (originalListIndex < dataList.size() && option != null) {
                ENTRY previous = dataList.get(originalListIndex);
                updated = previous.copyModified(index, option.getValue());
                dataList.set(originalListIndex, updated);
            }
            resetButton.setEnabled(!defaultValue.equals(updated));
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
        public final List<PartialValue<DATA>> possibleValues;
        public final Function<DATA, String> toStringConverter;
        public final DropDownListWidget.IconWidgetFactory<DATA> iconWidgetFactory;

        public PartBundle(Class<? extends DATA> type, List<PartialValue<DATA>> possibleValues, Function<DATA, String> toStringConverter, @Nullable DropDownListWidget.IconWidgetFactory<DATA> iconWidgetFactory) {
            this.type = type;
            this.possibleValues = Objects.requireNonNull(possibleValues);
            this.toStringConverter = Objects.requireNonNull(toStringConverter);
            this.iconWidgetFactory = iconWidgetFactory;
        }

        @Nonnull
        public static <E extends Enum<E>> PartBundle<Object> createEnumObjectFactories(Class<E> enumClass, Function<E, String> toStringConverter, @Nullable DropDownListWidget.IconWidgetFactory<E> iconWidgetFactory) {
            return new PartBundle<>(
                    enumClass,
                    NullableEnum.getObjectListOfType(enumClass),
                    object -> {
                        E value = null;
                        if (object != null && enumClass.isAssignableFrom(object.getClass())) {
                            value = enumClass.cast(object);
                        }
                        return value == null ? "*" : toStringConverter.apply(value);
                    },
                    iconWidgetFactory == null ? null : object -> {
                        E value = null;
                        if (object != null && enumClass.isAssignableFrom(object.getClass())) {
                            value = enumClass.cast(object);
                        }
                        return iconWidgetFactory.create(value);
                    }
            );
        }
    }
}
