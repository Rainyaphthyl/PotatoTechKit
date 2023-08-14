package io.github.rainyaphthyl.potteckit.gui;

import fi.dy.masa.malilib.gui.config.liteloader.RedirectingConfigPanel;

public class PotteckitConfigPanel extends RedirectingConfigPanel {
    public PotteckitConfigPanel() {
        super(GuiConfigScreen::create);
    }
}
