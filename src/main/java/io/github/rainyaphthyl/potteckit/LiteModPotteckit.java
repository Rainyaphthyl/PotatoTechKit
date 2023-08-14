package io.github.rainyaphthyl.potteckit;

import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import io.github.rainyaphthyl.potteckit.util.version.ModVersion;
import net.minecraft.client.Minecraft;

import java.io.File;

@SuppressWarnings("unused")
public class LiteModPotteckit implements Configurable, InitCompleteListener {
    public static final String NAME = "Potato Tech Kit";
    public static final String VERSION = "0.0.0";
    public static final ModVersion versionObj = ModVersion.getVersion(VERSION);

    /**
     * Get the mod version string
     *
     * @return the mod version as a string
     */
    @Override
    public String getVersion() {
        return VERSION;
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
        return NAME;
    }

    /**
     * Get the class of the configuration panel to use, the returned class must
     * have a default (no-arg) constructor
     *
     * @return configuration panel class
     */
    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass() {
        return null;
    }

    /**
     * Called as soon as the game is initialised and the main game loop is
     * running.
     *
     * @param minecraft Minecraft instance
     * @param loader    LiteLoader instance
     */
    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
    }
}
