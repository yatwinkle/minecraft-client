package yatwinkle.injection.wrapper.client;

import yatwinkle.client.service.config.ConfigManager;

public class MinecraftClientWrapper {

    public static void onDisconnect() {
        ConfigManager manager = ConfigManager.getIfInit();

        if (manager != null) {
            manager.saveDefaultConfig();
        }
    }
}
