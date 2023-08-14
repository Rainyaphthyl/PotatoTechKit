package io.github.rainyaphthyl.potteckit.input;

import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.HotkeyCategory;
import fi.dy.masa.malilib.input.HotkeyProvider;
import io.github.rainyaphthyl.potteckit.config.ConfigHandler;

import java.util.List;

public class PotteckitHotkeyProvider implements HotkeyProvider {
    @Override
    public List<? extends Hotkey> getAllHotkeys() {
        return ConfigHandler.allHotkeyList;
    }

    @Override
    public List<HotkeyCategory> getHotkeysByCategories() {
        return ConfigHandler.hotkeyCategoryList;
    }
}
