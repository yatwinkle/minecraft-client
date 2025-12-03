package yatwinkle;

import net.fabricmc.api.ClientModInitializer;
import org.lwjgl.glfw.GLFW;
import yatwinkle.client.feature.event.client.EventKeyboardKey;
import yatwinkle.client.service.command.CommandManager;
import yatwinkle.client.service.config.ConfigManager;
import yatwinkle.client.service.event.AtomicBus;
import yatwinkle.client.service.event.Listener;
import yatwinkle.client.service.module.Modules;

public class ClientMain implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Modules.init();
        ConfigManager.init(Modules.get());
        CommandManager.init();
    }
}
