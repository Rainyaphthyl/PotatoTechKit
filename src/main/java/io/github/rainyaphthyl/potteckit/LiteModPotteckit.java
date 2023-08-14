package io.github.rainyaphthyl.potteckit;

import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import fi.dy.masa.malilib.registry.Registry;
import io.github.rainyaphthyl.potteckit.config.Configs;
import io.github.rainyaphthyl.potteckit.config.Reference;
import io.github.rainyaphthyl.potteckit.gui.PotteckitConfigPanel;

import java.io.File;

@SuppressWarnings("unused")
public class LiteModPotteckit implements Configurable, LiteMod {
    /**
     * Get the mod version string
     *
     * @return the mod version as a string
     */
    @Override
    public String getVersion() {
        return Reference.VERSION;
    }

    /**
     * Do startup stuff here, minecraft is not fully initialised when this
     * function is called so mods <b>must not</b> interact with minecraft in any
     * way here.
     *
     * @param configPath Configuration path to use
     */
    @Override
    public void init(File configPath) {
        Registry.INITIALIZATION_DISPATCHER.registerInitializationHandler(Configs::registerOnInit);
    }

    /**
     * Called when the loader detects that a version change has happened since
     * this mod was last loaded.
     *
     * @param version       new version
     * @param configPath    Path for the new version-specific config
     * @param oldConfigPath Path for the old version-specific config
     */
    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {
    }

    /**
     * Get the display name
     *
     * @return display name
     */
    @Override
    public String getName() {
        return Reference.NAME;
    }

    /**
     * Get the class of the configuration panel to use, the returned class must
     * have a default (no-arg) constructor
     *
     * @return configuration panel class
     */
    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass() {
        return PotteckitConfigPanel.class;
    }
}
