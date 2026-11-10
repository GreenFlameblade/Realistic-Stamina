package com.gfb.realisticstamina;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import java.util.logging.Level;
import javax.annotation.Nonnull;

public class RealisticStaminaPlugin extends JavaPlugin {
    private final StaminaDebugWatchers debugWatchers = new StaminaDebugWatchers();

    public RealisticStaminaPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }
    @Override
    protected void setup() {
        getEntityStoreRegistry().registerSystem(new StaminaDebugSystem(this));
        getCommandRegistry().registerCommand(new StaminaDebugCommand(debugWatchers));
        getLogger().at(Level.INFO).log("Realistic Stamina loaded!");
    }

    public StaminaDebugWatchers getDebugWatchers() {
        return debugWatchers;
    }
}
