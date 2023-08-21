package io.github.rainyaphthyl.potteckit.config.option;

import fi.dy.masa.malilib.action.Action;
import fi.dy.masa.malilib.action.BooleanToggleAction;
import fi.dy.masa.malilib.config.option.BooleanAndIntConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.input.callback.HotkeyCallback;
import fi.dy.masa.malilib.overlay.message.MessageHelpers;
import fi.dy.masa.malilib.util.data.ModInfo;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class HotkeyedBooleanIntConfig extends BooleanAndIntConfig implements Hotkey {
    protected final HotkeyConfig hotkeyConfig;
    protected Action toggleAction;

    public HotkeyedBooleanIntConfig(String name, boolean defaultBoolean, int defaultInt, int minValue, int maxValue, String defaultHotkey) {
        this(name, defaultBoolean, defaultInt, minValue, maxValue, defaultHotkey, KeyBindSettings.INGAME_DEFAULT);
    }

    public HotkeyedBooleanIntConfig(String name, boolean defaultBoolean, int defaultInt, int minValue, int maxValue, String defaultHotkey, KeyBindSettings settings) {
        this(name, defaultBoolean, defaultInt, minValue, maxValue, true, defaultHotkey, name, name, settings);
    }

    public HotkeyedBooleanIntConfig(String name, boolean defaultBoolean, int defaultInt, int minValue, int maxValue, boolean sliderActive, String defaultHotkey, String prettyName, String commentTranslationKey, KeyBindSettings settings, Object... commentArgs) {
        super(name, defaultBoolean, defaultInt, minValue, maxValue, sliderActive, commentTranslationKey, commentArgs);
        hotkeyConfig = new HotkeyConfig(name, defaultHotkey, settings);
        setSpecialToggleMessageFactory(null);
        cacheSavedValue();
    }

    @Override
    public boolean isModified(String newValue) {
        return super.isModified(newValue) || hotkeyConfig.isModified();
    }

    @Override
    public KeyBind getKeyBind() {
        return hotkeyConfig.getKeyBind();
    }

    public Action getToggleAction() {
        return toggleAction;
    }

    /**
     * This will replace the default hotkey callback with the variant that takes in the message factory
     */
    public void setSpecialToggleMessageFactory(@Nullable MessageHelpers.BooleanConfigMessageFactory messageFactory) {
        toggleAction = BooleanToggleAction.of(this, messageFactory, hotkeyConfig.getKeyBind().getSettings()::getMessageType);
        hotkeyConfig.setHotkeyCallback(HotkeyCallback.of(toggleAction));
    }

    public void setHotkeyCallback(HotkeyCallback callback) {
        hotkeyConfig.setHotkeyCallback(callback);
    }

    @Override
    public void setModInfo(ModInfo modInfo) {
        super.setModInfo(modInfo);
        hotkeyConfig.setModInfo(modInfo);
    }

    @Override
    public void setNameTranslationKey(String key) {
        hotkeyConfig.setNameTranslationKey(key);
        super.setNameTranslationKey(key);
    }

    @Override
    public boolean isModified() {
        return super.isModified() || hotkeyConfig.isModified();
    }

    @Override
    public boolean isDirty() {
        return super.isDirty() || hotkeyConfig.isDirty();
    }

    @Override
    public void cacheSavedValue() {
        super.cacheSavedValue();
        // FIXME This method unfortunately gets called already from the super constructor,
        // before the field is set in this class's constructor.
        if (hotkeyConfig != null) {
            hotkeyConfig.cacheSavedValue();
        }
    }

    @Override
    public void resetToDefault() {
        super.resetToDefault();
        hotkeyConfig.resetToDefault();
    }

    public void loadHotkeyedBooleanValueFromConfig(boolean booleanValue) {
        setBooleanValue(booleanValue);
        cacheSavedValue();
        updateEffectiveValue();
        onValueLoaded(effectiveValue);
    }
}
