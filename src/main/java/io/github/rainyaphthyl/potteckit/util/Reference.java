package io.github.rainyaphthyl.potteckit.util;

import fi.dy.masa.malilib.util.data.ModInfo;
import io.github.rainyaphthyl.potteckit.util.version.ModVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reference {
    public static final String VERSION = "0.1.3";
    public static final ModVersion versionObj = ModVersion.getVersion(VERSION);
    public static final String NAME = "PotatoTechKit";
    public static final String SHORT_NAME = "potteckit";
    public static final String ID = SHORT_NAME;
    public static final ModInfo MOD_INFO = new ModInfo(ID, NAME);
    public static final int CONFIG_VERSION = 1;
    public static final Logger LOGGER = LogManager.getLogger("potteckit");
}
