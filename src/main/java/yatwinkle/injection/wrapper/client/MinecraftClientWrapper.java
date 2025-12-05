package yatwinkle.injection.wrapper.client;

import yatwinkle.client.service.config.ConfigManager;
import yatwinkle.client.service.render.renderers.impl.BuiltDualKawase;

public class MinecraftClientWrapper {

    public static void onDisconnect() {
        ConfigManager manager = ConfigManager.getIfInit();

        if (manager != null) {
            manager.saveDefaultConfig();
        }
    }

    public static void onResolutionChanged() {
        BuiltDualKawase.blur.onResolutionChanged();
    }
}
